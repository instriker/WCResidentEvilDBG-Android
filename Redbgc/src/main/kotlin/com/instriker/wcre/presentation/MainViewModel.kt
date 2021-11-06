package com.instriker.wcre.presentation

import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.services.NoAdsInAppBillingService

class MainViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    init {

        this.activityService.displayNewVersionInfoOnce()
    }

    override fun onStart() {
        this.noAdsService.start(object : NoAdsInAppBillingService.INoAdsPurchaiseAvailableListener {
            override fun onNoAdsAvailable(noAds: Boolean) {
                if (noAds) {
                    return
                }
                adsService.setup(this@MainViewModel.context)
                adsService.showInterstitial()
            }
        })
    }

    fun createScenario() {
        activityService.navigateToCreateScenario()
    }

    fun pickRandomScenario() {
        activityService.navigateToPickRandomScenario()
    }

    fun viewAllScenarios() {
        activityService.navigateToViewAllScenarios()
    }

    fun createMansion() {
        activityService.navigateToCreateMansion()
    }

    fun pickRandomMansion() {
        activityService.navigateToPickRandomMansion()
    }

    fun viewAllMansions() {
        activityService.navigateToViewAllMansions()
    }

    fun viewCustomStartingInventory() {
        activityService.navigateToChooseCustomStartingInventoryCharacterActivity()
    }

    fun viewGameTracker() {
        activityService.navigateToResumeGameTracker()
    }

    fun viewTools() {
        activityService.navigateToTools()
    }
}
