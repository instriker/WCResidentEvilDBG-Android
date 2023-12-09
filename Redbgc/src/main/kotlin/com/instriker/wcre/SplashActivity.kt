package com.instriker.wcre

import com.instriker.wcre.framework.OnTaskCompleted
import com.instriker.wcre.presentation.ServiceLocator
import com.instriker.wcre.services.NoAdsInAppBillingService

import android.app.Activity
import android.content.*
import android.os.*
import android.util.Log
import android.widget.Toast
import com.google.ads.consent.ConsentStatus
import com.instriker.wcre.services.AdsService
import com.instriker.wcre.services.ConsentReceivedListener

import java.util.concurrent.CountDownLatch

class SplashActivity : Activity() {
    private val TAG = "SplashActivity"
    private val SPLASH_DISPLAY_LENGTH = 500

    private var _task: InitAppTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate starting")

        // http://www.jameselsey.co.uk/blogs/techblog/how-to-add-a-splash-screen-to-your-android-application-in-under-5-minutes/
        super.onCreate(savedInstanceState)
        setContentView(com.instriker.wcre.R.layout.activity_splash)
    }

    @Deprecated("To be updated to coroutine")
    override fun onBackPressed() {
        this._task!!.cancel(true)

        super.onBackPressed()
    }

    override fun onResume() {
        Log.d(TAG, "onResume starting")

        super.onResume()

        val start = System.currentTimeMillis()

        Log.d(TAG, "InitAppTask starting")
        val initTask = InitAppTask()
        this._task = initTask
        initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object : OnTaskCompleted<Boolean> {
            override fun onTaskCompleted(result: Boolean) {
                Log.d(TAG, "initTask completed")

                val end = System.currentTimeMillis()
                val elapsedMs = end - start

                var remainingSlashScreen = SPLASH_DISPLAY_LENGTH - elapsedMs
                remainingSlashScreen = if (remainingSlashScreen < 0) 0 else remainingSlashScreen

                Log.d(TAG, "Closing in $remainingSlashScreen")
                Handler().postDelayed(
                        {
                            Log.d(TAG, "finishing activity")

                            //Finish the splash activity so it can't be returned to.
                            this@SplashActivity.finish()

                            Log.d(TAG, "starting MainActivity")
                            // Create an Intent that will start the main activity.
                            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                            this@SplashActivity.startActivity(mainIntent)
                        }, remainingSlashScreen)
            }

            override fun onTaskFailed() {
                Toast
                        .makeText(this@SplashActivity, "Problem while initializing the application. The application will now quit.", Toast.LENGTH_SHORT)
                        .show()

                Handler().postDelayed(
                        { this@SplashActivity.finish() }, 2000)
            }
        })
    }

    private inner class InitAppTask : AsyncTask<OnTaskCompleted<Boolean>, Void, Boolean>() {

        private val TAG = "SplashActivity"

        private var listener: OnTaskCompleted<Boolean>? = null

        @Deprecated("To be updated to coroutine")
        override fun doInBackground(vararg arg0: OnTaskCompleted<Boolean>): Boolean? {
            Log.d(TAG, "InitAppTask doInBackground starting")

            this.listener = arg0[0]

            val adsStarted = CountDownLatch(1)

            val serviceLocator = ServiceLocator(this@SplashActivity)
            val noAdsService = serviceLocator.noAdsService
            val adsService = serviceLocator.adsService

            Log.d(TAG, "InitAppTask ads init starting")
            noAdsService.start(object : NoAdsInAppBillingService.INoAdsPurchaiseAvailableListener {
                override fun onNoAdsAvailable(didBougthNoAds: Boolean) {
                    Log.i(TAG, "onNoAdsAvailable callback: $didBougthNoAds")
                    if (didBougthNoAds) {
                        Log.i(TAG, "No ads bought")
                        adsStarted.countDown()
                    } else {
                        Log.d(TAG, "showing consent")
                        showConsent(adsService, noAdsService) {
                            Log.d(TAG, "showConsent callback invoked")
                            adsStarted.countDown()
                        }
                    }
                }
            })

            Log.d(TAG, "InitAppTask db init starting")
            serviceLocator.gameContentService.scenarios

            Log.d(TAG, "awaiting adsStarted")
            adsStarted.await()
            Log.d(TAG, "awaiting released")

            return true
        }

        private fun showConsent(adsService: AdsService, noAdsService: NoAdsInAppBillingService, callback: () -> Unit) {
            adsService.promptUserConsent(object : ConsentReceivedListener {
                override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                    Log.i(TAG, "User Ads consent updated: $consentStatus")
                    callback()
                }

                override fun onAdFreeRequested() {
                    Log.i(TAG, "Purchasing Ad free version")

                    noAdsService.purchaseNoAds(this@SplashActivity, object : NoAdsInAppBillingService.INoAdsPurchaseListener {
                        override fun onPurchaseCompleted(completed: Boolean) {
                            if (completed) {
                                callback()
                            }
                            Log.i(TAG, "purchaseNoAds cancelled")
                            showConsent(adsService, noAdsService, callback)
                        }
                    })
                }

                override fun onFailedToUpdateConsentInfo(reason: String) {
                    Log.e(TAG, "onFailedToUpdateConsentInfo: $reason")
                    showConsent(adsService, noAdsService, callback)
                }
            })
        }

        @Deprecated("To be updated to coroutine")
        override fun onPostExecute(result: Boolean?) {
            if (this.listener != null) {
                if (result!!) {
                    listener!!.onTaskCompleted(result)
                } else {
                    listener!!.onTaskFailed()
                }
            }
        }
    }
}