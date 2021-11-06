package com.instriker.wcre.presentation

import android.app.Activity
import android.content.Context
import com.instriker.wcre.config.Constants

import com.instriker.wcre.services.AdsService
import com.instriker.wcre.services.GameTrackerService
import com.instriker.wcre.services.ActivityService
import com.instriker.wcre.services.GameContentService
import com.instriker.wcre.services.NoAdsInAppBillingService
import com.instriker.wcre.services.PackageService
import com.instriker.wcre.services.PickerService
import com.instriker.wcre.services.RepositoryFactory
import com.instriker.wcre.services.ResourceServices
import com.instriker.wcre.services.SettingsService

class ServiceLocator(val context: Context) {
    private val _activityService: ActivityService?

    val activityService: ActivityService
        get() {
            return _activityService!!
        }

    val gameContentService: GameContentService
    val settingsService: SettingsService
    val resourceServices: ResourceServices
    val packageService: PackageService
    val gameTrackerService: GameTrackerService
    val pickerService: PickerService
    val adsService: AdsService

    init {
        val activityContext = context as Activity?
        this.resourceServices = ResourceServices(this.context)
        this.gameContentService = GameContentService(RepositoryFactory(this.context), this.resourceServices)
        this.settingsService = SettingsService(this.context)
        this.packageService = PackageService(this.context)
        this.gameTrackerService = GameTrackerService(this.settingsService)
        this._activityService = if (activityContext != null) ActivityService(activityContext, this.resourceServices, this.settingsService, this.packageService) else null
        this.pickerService = PickerService(this.context)
        var noAdsService = _noAdsService
        if (noAdsService == null) {
            _noAdsService = NoAdsInAppBillingService(this.context.applicationContext, this.settingsService)
            noAdsService = _noAdsService
        }
        this.adsService = AdsService(this.context, Constants.INTERSTITIAL_AD_UNIT_ID, this.settingsService, noAdsService!!)
    }

    val noAdsService: NoAdsInAppBillingService
        get() {
            return _noAdsService!!
        }

    companion object {
        private var _noAdsService: NoAdsInAppBillingService? = null
    }
}
