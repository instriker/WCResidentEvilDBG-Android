package com.instriker.wcre.services

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.nfc.Tag
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Base64
import android.util.Log

import com.android.vending.billing.IInAppBillingService
import com.instriker.wcre.config.Constants

import org.json.JSONException
import org.json.JSONObject

import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.ArrayList

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

class NoAdsInAppBillingService(private val applicationContext: Context, private val _settingsService: SettingsService) {
    private var _disposed = false
    private var _serviceConn: ServiceConnection? = null
    private var _billingService: IInAppBillingService? = null
    private var _billingSupported = true
    private var _didBougthNoAds: Boolean = false
    private var _purchaseListener: INoAdsPurchaseListener? = null

    interface INoAdsPurchaiseAvailableListener {
        fun onNoAdsAvailable(noAds: Boolean)
    }

    interface INoAdsPurchaseListener {
        fun onPurchaseCompleted(completed: Boolean)
    }

    fun start(listener: INoAdsPurchaiseAvailableListener) {
        var invoked = false
        if (_settingsService.noAds) {
            Log.d(TAG, "ads disabled detected")
            _didBougthNoAds = true
            listener.onNoAdsAvailable(_didBougthNoAds)
            invoked = true
        }

        // We are trying to connect even if we are connected in case a refund occur, so we can reset the ads
        // in that case, we accept to not see ads until we reboot.
        val wasInvoked = invoked
        _serviceConn = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName) {
                Log.d(TAG, "onServiceDisconnected")
                _billingService = null
            }

            override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
                Log.d(TAG, "onServiceConnected")

                base64Signature
                try {
                    _billingService = IInAppBillingService.Stub.asInterface(service)
                    val response = _billingService!!.isBillingSupported(3, applicationContext.packageName, ITEM_TYPE_INAPP)

                    _billingSupported = response == BILLING_RESPONSE_RESULT_OK
                    if (_billingSupported) {
                        val price = noAdsPrice
                        Log.d(TAG, "price=" + price!!)
                        val didBougthNoAds = didBougthNoAds()
                        if (didBougthNoAds) {
                            removeAds()
                        } else {
                            addAds()
                        }
                    }
                } catch (ex: RemoteException) {
                    Log.e(TAG, "isBillingSupported validation failed", ex)
                    _billingSupported = false
                } finally {
                    if (!wasInvoked) {
                        listener.onNoAdsAvailable(_didBougthNoAds)
                    }
                }
            }
        }

        try {
            val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
            serviceIntent.`package` = "com.android.vending"
            if (!applicationContext.bindService(serviceIntent, _serviceConn!!, Context.BIND_AUTO_CREATE)) {
                Log.e(TAG, "bindService failed to bind")
                _billingSupported = false
                if (!wasInvoked) {
                    listener.onNoAdsAvailable(false)
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "bindService failed", ex)
            _billingSupported = false
            if (!wasInvoked) {
                listener.onNoAdsAvailable(false)
            }
        }
    }

    private fun removeAds() {
        setMustShowAds(true)
    }

    private fun addAds() {
        setMustShowAds(false)
    }

    private fun setMustShowAds(bought: Boolean) {
        _settingsService.noAds = bought
        _didBougthNoAds = bought
    }

    fun canBuyNoAds(): Boolean {
        return _billingSupported
    }

    private val noAdsPrice: String?
        get() {
            val skuList = ArrayList<String>()
            skuList.add(SKU_NoAds)
            val querySkus = Bundle()
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList)

            try {
                val skuDetails = _billingService!!.getSkuDetails(3, applicationContext.packageName, ITEM_TYPE_INAPP, querySkus)

                val response = skuDetails.getInt("RESPONSE_CODE")
                if (response == 0) {
                    val responseList = skuDetails.getStringArrayList("DETAILS_LIST")
                    for (thisResponse in responseList!!) {
                        try {

                            val `object` = JSONObject(thisResponse)
                            val sku = `object`.getString("productId")
                            val price = `object`.getString("price")
                            if (sku == SKU_NoAds) {
                                return price
                            }
                        } catch (ex: JSONException) {
                            Log.e(TAG, "Failed to read DETAILS_LIST", ex)
                        }

                    }
                }
            } catch (ex: RemoteException) {
                Log.e(TAG, "Failed to read DETAILS_LIST", ex)
            }

            return null
        }

    fun didBougthNoAds(): Boolean {
        try {
            val ownedItems = _billingService!!.getPurchases(3, applicationContext.packageName, ITEM_TYPE_INAPP, null)

            val response = ownedItems.getInt("RESPONSE_CODE")
            if (response == BILLING_RESPONSE_RESULT_OK) {
                val ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")
                val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                val signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")
                val continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN")
                if (continuationToken != null && !continuationToken.isEmpty()) {
                    Log.w(TAG, "received continuationToken : Might be missing some items")
                }

                for (i in ownedSkus!!.indices) {
                    val purchaseData = purchaseDataList!![i]
                    val signature = signatureList!![i]
                    val sku = ownedSkus[i]
                    if (verifyPurchase(purchaseData, signature)) {
                        if (SKU_NoAds == sku && !Constants.DEBUG_FORCE_ADS) {
                            removeAds()
                            return true
                        }
                    } else {
                        Log.e(TAG, "Signature validation failed")
                    }
                }
            } else {
                Log.e(TAG, "Failed to retreive purchases. response=" + response)
            }
        } catch (ex: RemoteException) {
            Log.e(TAG, "Failed to retreive purchases", ex)
        }

        return false
    }

    fun purchaseNoAds(activity: Activity, purchaseListener: INoAdsPurchaseListener) {
        try {
            _purchaseListener = purchaseListener

            // No developer payload : No way of validating the user account as we do not have any login...
            val buyIntentBundle = _billingService!!.getBuyIntent(3, applicationContext.packageName, SKU_NoAds, ITEM_TYPE_INAPP, "")
            when (val responseCode = buyIntentBundle.getInt("RESPONSE_CODE")) {
                BILLING_RESPONSE_RESULT_OK -> {
                    val pendingIntent = buyIntentBundle.getParcelable<PendingIntent>("BUY_INTENT")
                    activity.startIntentSenderForResult(pendingIntent!!.intentSender, NO_ADS_REQUEST_CODE, Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0))
                }
                BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED -> {
                    removeAds()
                    invokePurchaseCompleted(true)
                }
                else -> {
                    Log.w(TAG, "Error starting purchase. responseCode =$responseCode")
                    invokePurchaseCompleted(false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start purchase", e)
            e.printStackTrace()

            invokePurchaseCompleted(false)
        }
    }

    private fun invokePurchaseCompleted(completed: Boolean) {
        if (_purchaseListener != null) {
            _purchaseListener!!.onPurchaseCompleted(completed)
            _purchaseListener = null
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != NO_ADS_REQUEST_CODE)
            return false

        var didPurchase = false
        if (data == null) {
            Log.w(TAG, "Invalid data received")
        } else {
            val responseCode = getResponseCodeFromIntent(data)
            val purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA")
            val dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE")

            if (resultCode == Activity.RESULT_OK && responseCode == BILLING_RESPONSE_RESULT_OK) {
                if (purchaseData != null && dataSignature != null) {
                    try {
                        val o = JSONObject(purchaseData)
                        val sku = o.optString("productId")

                        if (verifyPurchase(purchaseData, dataSignature)) {
                            didPurchase = true
                            removeAds()
                            Log.d(TAG, "Purchase signature successfully verified.")
                        } else {
                            Log.e(TAG, "Purchase signature verification FAILED for sku " + sku)
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "Failed to parse purchase data.", e)
                        e.printStackTrace()
                    }

                }
            } else if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Result code was OK but in-app billing response was not OK: " + getResponseDesc(responseCode))
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Purchase canceled - Response: " + getResponseDesc(responseCode))
            } else {
                Log.e(TAG, "Purchase failed. Result code: " + Integer.toString(resultCode) + ". Response: " + getResponseDesc(responseCode))
            }
        }

        invokePurchaseCompleted(didPurchase)
        return true
    }

    // Workaround to bug where sometimes response codes come as Long instead of Integer
    private fun getResponseCodeFromIntent(i: Intent): Int {
        val o = i.extras?.get("RESPONSE_CODE")
        return when (o) {
            null -> {
                Log.e(TAG, "Intent with no response code, assuming OK (known issue)")
                BILLING_RESPONSE_RESULT_OK
            }
            is Int -> o.toInt()
            is Long -> o.toLong().toInt()
            else -> {
                Log.e(TAG, "Unexpected type for intent response code.")
                Log.e(TAG, o.javaClass.name)
                throw RuntimeException("Unexpected type for intent response code: " + o.javaClass.name)
            }
        }
    }

    fun dispose() {
        if (_disposed) {
            return
        }

        _billingSupported = false
        val serviceConn = _serviceConn
        if (serviceConn != null) {
            applicationContext.unbindService(serviceConn)
        }
        _serviceConn = null
        _billingService = null
        _disposed = true
    }

    companion object {
        private val _encryptedBase64Signature = "IXEEJwgPLyMSIlkcMB0rdQEdCHUoaXYJMTcMBwIjS3Z+IzEGFFA5CDI2KCkpdgI4FREJOzxqUkto\n" +
                "NBsLXjB+J30wHFgWHQVCDS0ZDFsjDCEMLRguAUQvHAVsPSYaXyFdKR4rQS8IKjI6P1tTEBEHCz55\n" +
                "aAJ2YhdGCyI/GyBvHAU5DCAMDF0XKyIQDXEnBwYWQFY6biALBh1xH0MtEj48WxhSYW4Lbhg+ARAR\n" +
                "Ek8VPnQVLQw/SjMaER4CMTVkSSF9EgMLZSMODw4NeFAtBg52N14qMDElPSNTAAljOjAXDi1fIkpj\n" +
                "KBcnPgYFFzYVJG4lFAtafD4hMFozARs3eFFqUiICZBw9LyQVDhE5RRIjKg0BBA8/EgoAORAlRCJd\n" +
                "aQdfCB5tXWAPQTEMMVcPVQZ3MjM5LB8gWhIEBBUQFzoMHAYxBFFbRRt0AUBnHjA9QlZZcwoyKAYn\n" +
                "AB4cZ1stfw9+IUYCBicJFzcSCiweOBIIQAtqRC8GCBtBGEJCfABbRywlcSM6c3I7OQY=\n"
        private var _base64Signature: String? = null

        private val KEY_FACTORY_ALGORITHM = "RSA"
        private val SIGNATURE_ALGORITHM = "SHA1withRSA"

        // https://developer.android.com/google/play/billing/billing_reference.html#billing-codes
        private val BILLING_RESPONSE_RESULT_OK = 0
        private val BILLING_RESPONSE_RESULT_USER_CANCELED = 1
        private val BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2
        private val BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3
        private val BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4
        private val BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5
        private val BILLING_RESPONSE_RESULT_ERROR = 6
        private val BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7
        private val BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8
        private val ITEM_TYPE_INAPP = "inapp"
        private val SKU_RealNoAds = "no_ads"

        // adb shell pm clear com.android.vending # Reset android.test.purchased
        private val SKU_StaticPurchased = "android.test.purchased"
        private val SKU_StaticCanceled = "android.test.canceled"
        private val SKU_StaticRefunded = "android.test.refunded"
        private val SKU_StaticItemUnavailable = "android.test.item_unavailable"
        private val SKU_NoAds = SKU_RealNoAds
        private val TAG = "Billing"


        private val NO_ADS_REQUEST_CODE = 16548

        private fun getResponseDesc(code: Int): String {
            val iab_msgs = ("0:OK/1:User Canceled/2:Unknown/" +
                    "3:Billing Unavailable/4:Item unavailable/" +
                    "5:Developer Error/6:Error/7:Item Already Owned/" +
                    "8:Item not owned").split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val iabhelper_msgs = ("0:OK/-1001:Remote exception during initialization/" +
                    "-1002:Bad response received/" +
                    "-1003:Purchase signature verification failed/" +
                    "-1004:Send intent failed/" +
                    "-1005:User cancelled/" +
                    "-1006:Unknown purchase response/" +
                    "-1007:Missing token/" +
                    "-1008:Unknown error/" +
                    "-1009:Subscriptions not available/" +
                    "-1010:Invalid consumption attempt").split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

            val IABHELPER_ERROR_BASE = -1000
            if (code <= IABHELPER_ERROR_BASE) {
                val index = IABHELPER_ERROR_BASE - code
                if (index >= 0 && index < iabhelper_msgs.size)
                    return iabhelper_msgs[index]
                else
                    return code.toString() + ":Unknown IAB Helper Error"
            } else if (code < 0 || code >= iab_msgs.size)
                return code.toString() + ":Unknown"
            else
                return iab_msgs[code]
        }

        //String encrypted = xor("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg3FbygdZYSdy8GCy5G5M1fl6DMcqKTOUbyFu4eAD8uMEBTUdI8JozWSqwaDsKjofQI3zKL+OO5u79RJriXrdCuJjZjNsJEnENuma15RXNOhLEhuAdxjoNcV/d/zvVTEU7wU2OgBSzvXZh3dzV9S2ffASL6mbL27F4e2R5IxcSNrbqN4nrbin1hr3CSPWgoVzyv6iaZb9KfH8GQSeOa+5xjSXZUaveHn/xDcIkFjXWNrnbh3X+Qj6Pg7n9httefcZmMGzepnVMnQSPfIvcZkVa44oub5L4Pqioua5B9pMAdBteQ9e7UOd71uLe/zwKIpUBMrf22m40qy/+zOO+1oa2QIDAQAB", xorKey);
        // String base64Encrypted = Base64.encodeToString(encrypted.getBytes(), Base64.DEFAULT);
        private val base64Signature: String
            get() {
                if (_base64Signature == null) {
                    val xorKey = "l8MeAenmPE2mXvB28j87i83OpvCDCrs73jxDW7rKsgmhNEDZlvmae962PsXrkwKjLVpnRPf3FyVY9ZJT8HYj91bYG8hBSgk2SIx0XinAqU45AX4qu5CM9Wbq2puiI7nazyjfV7YXhUc4irkwqch6nDnQ4w6lvFV4V17AoAbHWDTGxbkFZJNl0EBKv1UO2prOteJ6o8blAJgk2kDekcHRvsQ1qGWTBugCnJ8PkDwiajALlRXLuQ8EuGxbtPHR70AgZh7DgzEckYWjjgIDjBegEDrWrMwzv8miXyZ3Yg5EiW4U8K0HVIBIm4CWTsYaYVwPP0e40yAMt7oYR77l13BeGCBjy6bHHZ1Eq3sk"

                    val toDecrypt = Base64.decode(_encryptedBase64Signature, Base64.DEFAULT)
                    val result = xor(String(toDecrypt), xorKey)
                    _base64Signature = result
                }
                return _base64Signature!!
            }


        fun xor(message: String?, key: String?): String? {
            try {
                if (message == null || key == null) return null

                val keys = key.toCharArray()
                val mesg = message.toCharArray()

                val ml = mesg.size
                val kl = keys.size
                val newmsg = CharArray(ml)

                for (i in 0..ml - 1) {
                    newmsg[i] = (mesg[i].toByte().xor(keys[i % kl].toByte())).toChar()
                }

                return String(newmsg)
            } catch (e: Exception) {
                return null
            }

        }//xorMessage

        private fun verifyPurchase(signedData: String, signature: String): Boolean {
            if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64Signature)) {
                Log.e(TAG, "Purchase verification failed: missing data.")
                return false
            }

            if (TextUtils.isEmpty(signature)) {
                if (Constants.DEBUG) {
                    Log.w(TAG, "No signature to validate. Skipping validation (DEBUG ONLY)")
                    return true
                }
                return false
            }

            val key = generatePublicKey()
            return verify(key, signedData, signature)
        }

        private fun generatePublicKey(): PublicKey {
            try {
                val decodedKey = Base64.decode(base64Signature, Base64.DEFAULT)
                val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
                return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            } catch (e: InvalidKeySpecException) {
                Log.e(TAG, "Invalid key specification.")
                throw IllegalArgumentException(e)
            }

        }

        private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
            val signatureBytes: ByteArray
            try {
                signatureBytes = Base64.decode(signature, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Base64 decoding failed.")
                return false
            }

            try {
                val sig = Signature.getInstance(SIGNATURE_ALGORITHM)
                sig.initVerify(publicKey)
                sig.update(signedData.toByteArray())
                if (!sig.verify(signatureBytes)) {
                    Log.e(TAG, "Signature verification failed.")
                    return false
                }
                return true
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "NoSuchAlgorithmException.")
            } catch (e: InvalidKeyException) {
                Log.e(TAG, "Invalid key specification.")
            } catch (e: SignatureException) {
                Log.e(TAG, "Signature exception.")
            }

            return false
        }
    }
}
