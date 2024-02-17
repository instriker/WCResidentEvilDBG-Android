package com.instriker.wcre.services

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.MobileAds.initialize
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.instriker.wcre.R
import com.instriker.wcre.config.Constants
import java.util.*


interface ConsentReceivedListener {
    fun onConsentInfoUpdated(consentStatus: ConsentStatus)
    fun onAdFreeRequested()
    fun onFailedToUpdateConsentInfo(reason: String)
}

class AdsService(private val _context: Context, private val _adUnitId: String,
                 private val _settingsService: SettingsService,
                 private val _noAdsInAppBillingService: NoAdsInAppBillingService) : InterstitialAdLoadCallback() {
    private val TAG = "AdsService"
    private var _interstitial: InterstitialAd? = null
    private var _interstitialLoading: Boolean = false
    private val TestDevice = "1C59FA0B93125C19381DF0FC3CA682CB"

    companion object {
        private var initializedContext: Context? = null
    }

    fun setup(context: Context) {
        Log.d(TAG, "setup starting")
        if (initializedContext != null) {
            Log.d(TAG, "Mobile Ads already initialized")
            return
        }

        Log.d(TAG, "Mobile Ads initializing")
        initializedContext = context

        initialize(context, OnInitializationCompleteListener(
                fun(_: InitializationStatus) {
                    Log.d(TAG, "Mobile Ads Initialized")
                }
        ))
    }

    fun needUserConsent(): Boolean {
        val consentInformation = ConsentInformation.getInstance(_context)
        if (Constants.DEBUG) {
            if (consentInformation.debugGeography == DebugGeography.DEBUG_GEOGRAPHY_EEA) {
                Log.d(TAG, "Debug only: fake eea detected")
                return true
            }
        }

        return consentInformation.isRequestLocationInEeaOrUnknown
    }

    fun promptUserConsent(consentInfoUpdateListener: ConsentReceivedListener) {
        Log.d(TAG, "showUserContentFormIfNecessary starting")
        val consentInformation = ConsentInformation.getInstance(_context)
        if (Constants.DEBUG) {
            Log.d(TAG, "Debug only: Setting consent debugGeography")
            consentInformation.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            consentInformation.addTestDevice(TestDevice)
            consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
            // consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA
        }

        val publisherIds = arrayOf(Constants.PUBLISHER_ID)
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                val updatedConsentStatus = consentStatus
                // val updatedConsentStatus  = ConsentStatus.UNKNOWN   // DEBUG ONLY!

                Log.d(TAG, "onConsentInfoUpdated: $updatedConsentStatus")

                this@AdsService._settingsService.consentStatus = updatedConsentStatus
                when (updatedConsentStatus) {
                    ConsentStatus.UNKNOWN -> {
                        val needUserConsent = this@AdsService.needUserConsent()
                        if (!needUserConsent) {
                            Log.i(TAG, "Not asking for user consent in this region")
                            consentInfoUpdateListener.onConsentInfoUpdated(ConsentStatus.PERSONALIZED)
                            return
                        }

                        Log.i(TAG, "Region needing user consent detected. Asking for use consent")
                        showUserConsentForm(consentInfoUpdateListener)
                    }
                    else -> {
                        Log.i(TAG, "User consent detected: $updatedConsentStatus")
                        consentInfoUpdateListener.onConsentInfoUpdated(updatedConsentStatus)
                    }
                }
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                consentInfoUpdateListener.onFailedToUpdateConsentInfo(errorDescription)
            }
        })
    }

    fun showUserConsentForm(consentInfoUpdateListener: ConsentReceivedListener? = null) {
        var form: ConsentForm? = null
        var formBuilder = ConsentForm.Builder(_context, Constants.PrivacyUrl)
                .withListener(object : ConsentFormListener() {
                    override fun onConsentFormLoaded() {
                        Log.d(TAG, "Showing ConsentForm")
                        form!!.show()
                    }

                    override fun onConsentFormClosed(
                            consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
                        Log.d(TAG, "ConsentForm closed")
                        if (userPrefersAdFree == true) {
                            consentInfoUpdateListener?.onAdFreeRequested()
                        } else {
                            this@AdsService._settingsService.consentStatus = consentStatus!!
                            consentInfoUpdateListener?.onConsentInfoUpdated(consentStatus!!)
                        }
                    }

                    override fun onConsentFormError(errorDescription: String?) {
                        Log.d(TAG, "ConsentForm error")
                        consentInfoUpdateListener?.onFailedToUpdateConsentInfo(errorDescription
                                ?: "")
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
        if (_noAdsInAppBillingService.canBuyNoAds()) {
            Log.d(TAG, "Billing supported. Adding AdFree Option")
            formBuilder = formBuilder.withAdFreeOption()
        }

        form = formBuilder.build()

        Log.d(TAG, "Loading ConsentForm")
        form.load()
    }

    fun showInterstitial() {
        Log.d(TAG, "showInterstitial starting")
        val lastShown = _settingsService.lastInterstitialShown
        val now = Calendar.getInstance().time
        val elapsedMs = now.time - lastShown.time
        val minDelayInMs = Constants.InterstitialAdsDelayInMs
        if (elapsedMs < minDelayInMs) {
            Log.d(TAG, "showInterstitial skipping (too recent)")
            return
        }

        Log.d(TAG, "showInterstitial loading request")
        if (_interstitialLoading) {
            return
        }

        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(_context, _adUnitId, adRequest, this);
        _interstitialLoading = true
    }

    override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
        Log.i(TAG, "onAdLoaded")

        _interstitialLoading = false
        _interstitial = interstitialAd

        if (interstitialAd != null) {
            interstitialAd.show(_context as Activity)
            _settingsService.setInterstitialShown()
        }
    }

    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        Log.e(TAG, loadAdError.message)

        _interstitialLoading = false
        _interstitial = null
    }

    fun showBanner() {
        val adView = banner
        if (adView != null) {
            if (adView.adSize == null) {
                // Unexpected, just skip
                return
            }
            if (adView.adUnitId.isNullOrEmpty()) {
                // For now, this does't work. We always have a "Required XML attribute "adSize" was missing
                // even if we set the adUnitId here. Further investigations would be need to solve
                // So, for now, we continue to set the adUnitId through xml (admobdefault.xml)
                adView.adUnitId = Constants.BANNER_AD_UNIT_ID
            }

            val adRequest = buildAdRequest()
            adView.loadAd(adRequest)
        }
    }

    private fun buildAdRequest(): AdRequest {
        setup(this._context)

        val extras = Bundle()
        if (this@AdsService._settingsService.consentStatus != ConsentStatus.PERSONALIZED
                && needUserConsent()) {
            Log.d(TAG, "Non personalized Ad request")
            extras.putString("npa", "1")
        } else {
            Log.d(TAG, "Personalized Ad request")
        }

        var adRequestBuilder: AdRequest.Builder = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)

        return adRequestBuilder.build()
    }

    fun pauseBanner() {
        val adView = banner
        adView?.pause()
    }

    fun resumeBanner() {
        val adView = banner
        adView?.resume()
    }

    fun destroyBanner() {
        val adView = banner
        adView?.destroy()
    }

    private val banner: AdView?
        get() {
            val activity = _context as Activity?
            return activity?.findViewById(R.id.adView) as AdView?
        }
}
