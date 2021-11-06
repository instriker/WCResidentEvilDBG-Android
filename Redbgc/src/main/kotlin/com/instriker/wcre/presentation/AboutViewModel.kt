package com.instriker.wcre.presentation

import com.instriker.wcre.R

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel

class AboutViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    val versionNumber = BindingsFactory.bindString()

    init {
        versionNumber.set(this.packageService.versionName)
    }

    fun emailContactInfo() {
        val resService = resourceServices
        val email = resService.getString(R.string.contactEmail)
        val title = String.format(resService.getString(R.string.contactEmailTitle), resService.getString(R.string.app_name))
        this.activityService.sendEmail(email, title)
    }

    override fun supportsAds(): Boolean {
        return false
    }

}
