package com.instriker.wcre.framework

import android.content.Context
import androidx.databinding.ObservableBoolean

import com.instriker.wcre.presentation.ServiceLocator
import com.instriker.wcre.services.ActivityService
import com.instriker.wcre.services.AdsService
import com.instriker.wcre.services.GameContentService
import com.instriker.wcre.services.GameTrackerService
import com.instriker.wcre.services.NoAdsInAppBillingService
import com.instriker.wcre.services.PackageService
import com.instriker.wcre.services.PickerService
import com.instriker.wcre.services.ResourceServices
import com.instriker.wcre.services.SettingsService

abstract class RedbgcViewModel protected constructor(protected val serviceLocator: ServiceLocator) {

    fun rateMyApplication() {
        this@RedbgcViewModel.activityService.rateMyApplication()
    }

    fun shareMyApplication() {
        this@RedbgcViewModel.activityService.shareMyApplication()
    }

    fun navigateToAbout() {
        this@RedbgcViewModel.activityService.navigateToAbout()
    }

    protected val context: Context
        get() = this.serviceLocator.context

    protected val activityService: ActivityService
        get() = this.serviceLocator.activityService

    protected val gameContentService: GameContentService
        get() = this.serviceLocator.gameContentService

    protected val settingsService: SettingsService
        get() = this.serviceLocator.settingsService

    protected val adsService: AdsService
        get() = this.serviceLocator.adsService

    protected val noAdsService: NoAdsInAppBillingService
        get() = this.serviceLocator.noAdsService

    protected val resourceServices: ResourceServices
        get() = this.serviceLocator.resourceServices

    protected val packageService: PackageService
        get() = this.serviceLocator.packageService

    protected val pickerService: PickerService
        get() = this.serviceLocator.pickerService

    protected val gameTrackerService: GameTrackerService
        get() = this.serviceLocator.gameTrackerService

    open fun onStart() {}

    open fun onStop() {}

    open fun onSaveInstanceState(savedInstanceState: IStateStore) {}

    open fun onRestoreInstanceState(savedInstanceState: IStateStore) {}

    open fun onBackPressed(): Boolean {
        return true
    }

    open fun onSettingsChanged() {}

    open fun onSearched(searchText: String) {}

    open fun supportsAds(): Boolean {
        return true
    }
}
