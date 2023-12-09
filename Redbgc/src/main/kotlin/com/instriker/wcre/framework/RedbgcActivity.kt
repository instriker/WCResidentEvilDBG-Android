package com.instriker.wcre.framework

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.google.ads.consent.ConsentStatus

import com.instriker.wcre.BR
import com.instriker.wcre.R
import com.instriker.wcre.presentation.ServiceLocator
import com.instriker.wcre.services.ActivityService
import com.instriker.wcre.services.AdsService
import com.instriker.wcre.services.ConsentReceivedListener
import com.instriker.wcre.services.NoAdsInAppBillingService

abstract class RedbgcActivity : Activity() {
    private val LogTag = "RedbgcActivity"
    private var _viewModel: RedbgcViewModel? = null
    private var _adsService: AdsService? = null
    private var _noAdsInAppBillingService: NoAdsInAppBillingService? = null

    protected abstract val initialLayout: Int

    protected abstract fun createViewModel(): RedbgcViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = viewModel

        val serviceLocator = serviceLocator
        _adsService = serviceLocator.adsService
        _noAdsInAppBillingService = serviceLocator.noAdsService

        _noAdsInAppBillingService!!.start(object : NoAdsInAppBillingService.INoAdsPurchaiseAvailableListener {
            override fun onNoAdsAvailable(noAds: Boolean) {
                _noAds = noAds
                val showAds = !noAds && vm.supportsAds()

                var container: View?
                container = if (showAds) {
                    this@RedbgcActivity.layoutInflater.inflate(R.layout.layoutwithads, null, true)
                } else {
                    this@RedbgcActivity.layoutInflater.inflate(R.layout.layoutwithoutads, null, true)
                }

                val mainContent = container!!.findViewById(R.id.mainContent) as FrameLayout
                val binding = DataBindingUtil.inflate<ViewDataBinding>(this@RedbgcActivity.layoutInflater, initialLayout, mainContent, true)
                binding.setVariable(BR.mainViewModel, vm)

                this@RedbgcActivity.setContentView(container)

                // Get the intent, verify the action and get the query
                val intent = intent
                handleIntent(intent)

                // Display banner ad if possible
                if (showAds) {
                    _adsService!!.setup(this@RedbgcActivity)
                    _adsService!!.showBanner()
                }

                onInflated()
            }
        })
    }

    protected open fun onInflated() {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuId = this.menu ?: return false

        this.menuInflater.inflate(menuId, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (this.menu == null) {
            return false
        }

        val buyNoAdsEnabled = _noAdsInAppBillingService!!.canBuyNoAds()
        val adsEnabled = !_noAds
        val needUserConsent = _adsService!!.needUserConsent()

        val buyNoAdsMenuItem = menu.findItem(R.id.buyNoAds)
        val modifyConsentItem = menu.findItem(R.id.modifyConsent)

        buyNoAdsMenuItem?.isVisible = adsEnabled && buyNoAdsEnabled
        modifyConsentItem?.isVisible = adsEnabled && needUserConsent

        return true
    }

    protected val viewModel: RedbgcViewModel
        get() {
            if (this._viewModel == null) {
                this._viewModel = createViewModel()
            }
            return this._viewModel!!
        }

    override fun onStart() {
        super.onStart()

        viewModel.onStart()
    }

    override fun onNewIntent(intent: Intent) {
        // Set the new intent as current
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        // Get the intent, verify the action and get the query
        if (intent != null && Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            viewModel.onSearched(query ?: "")
        }
    }

    protected open val menu: Int?
        get() = R.menu.mainmenu

    override fun onStop() {
        super.onStop()

        viewModel.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuabout -> {
                showAbout()
                return true
            }
            R.id.menuRate -> {
                rateMyApplication()
                return true
            }
            R.id.buyNoAds -> {
                buyNow()
                return true
            }
            R.id.modifyConsent -> {
                modifyConsent()
                return true
            }
            R.id.menuShare -> {
                shareMyApplication()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("To be converted")
    override fun onBackPressed() {
        var finishActivity = true
        try {
            finishActivity = viewModel.onBackPressed()
        } catch (ex: Exception) {
        }

        if (finishActivity) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (_noAdsInAppBillingService!!.handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        if (viewModel is RedbgcViewModel) {
            when (requestCode) {
                ActivityService.CHANGE_SETTINGS_REQUEST -> {
                    if (resultCode == RESULT_OK) {
                        viewModel.onSettingsChanged()
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        viewModel.onSaveInstanceState(StateStoreFactory.create(savedInstanceState))

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState)

        viewModel.onRestoreInstanceState(StateStoreFactory.create(savedInstanceState))
    }

    private fun rateMyApplication() {
        this.viewModel.rateMyApplication()
    }

    private fun shareMyApplication() {
        this.viewModel.shareMyApplication()
    }

    private fun modifyConsent() {
        _adsService!!.showUserConsentForm(object: ConsentReceivedListener{
            override fun onFailedToUpdateConsentInfo(reason: String) {
                Log.e(this@RedbgcActivity.LogTag, "onFailedToUpdateConsentInfo: $reason")
            }

            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                Log.i(this@RedbgcActivity.LogTag, "onConsentInfoUpdated: $consentStatus")
            }

            override fun onAdFreeRequested() {
                this@RedbgcActivity.buyNow()
            }
        })
    }

    private fun buyNow() {
        _noAdsInAppBillingService!!.purchaseNoAds(this, object : NoAdsInAppBillingService.INoAdsPurchaseListener {
            override fun onPurchaseCompleted(completed: Boolean) {
                val locator = serviceLocator
                val resources = locator.resourceServices
                if (completed) {
                    locator.activityService.showDialog(
                            resources.getString(R.string.noads_purchase_title),
                            resources.getString(R.string.noads_purchase_completed),
                            resources.getString(android.R.string.ok)
                    ) { recreate() }
                }/* else {
            locator.getActivityService().showDialog(
                    resources.getString(R.string.noads_purchase_title),
                    resources.getString(R.string.noads_purchase_notCompleted),
                    resources.getString(android.R.string.ok),
                    null
            );
        }*/
            }
        })
    }

    private fun showAbout() {
        this.viewModel.navigateToAbout()
    }

    public override fun onPause() {
        _adsService!!.pauseBanner()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        _adsService!!.resumeBanner()
    }

    public override fun onDestroy() {
        _adsService!!.destroyBanner()
        super.onDestroy()
    }

    private val serviceLocator: ServiceLocator
        get() = ServiceLocator(this)

    companion object {
        private var _noAds: Boolean = false
    }
}