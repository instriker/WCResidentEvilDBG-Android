package com.instriker.wcre.presentation

import com.instriker.wcre.R
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.IStateStore
import com.instriker.wcre.framework.RedbgcViewModel

class SettingsViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val showMansionSettings = BindingsFactory.bindBoolean(false)
    val showResourceAreaSettings = BindingsFactory.bindBoolean(false)

    val ownedExtensions = BindingsFactory.bind<OwnedExtensionsSettingsViewModel>()
    val mansionSettings = BindingsFactory.bind<MansionSettingsViewModel>()
    val resourceAreaSettings = BindingsFactory.bind<ResourceAreaSettingsViewModel>()

    private var _hasChanges: Boolean = false

    init {
        this.showMansionSettings.set(this.activityService.getParameterBoolean("com.instriker.wcre.showMansionSettings", false))
        this.showResourceAreaSettings.set(this.activityService.getParameterBoolean("com.instriker.wcre.showResourceAreaSettings", false))

        this.ownedExtensions.set(OwnedExtensionsSettingsViewModel(this.serviceLocator))
        this.mansionSettings.set(MansionSettingsViewModel(this.serviceLocator))
        this.resourceAreaSettings.set(ResourceAreaSettingsViewModel(this.serviceLocator))

        this.activityService.observeNavigating { saveIfChanged() }
    }

    protected fun saveChanges() {
        try {
            this.mansionSettings.get()!!.saveChanges()
            this.ownedExtensions.get()!!.saveChanges()
            this.resourceAreaSettings.get()!!.saveChanges()
        } catch (ex: Exception) {
            this.activityService.showMessage(this.resourceServices.getString(R.string.settingsSaveError))
            return
        }

        this.activityService.showMessage(this.resourceServices.getString(R.string.settingsSaved))
    }

    override fun onBackPressed(): Boolean {
        saveIfChanged()
        return super.onBackPressed()
    }

    override fun onSettingsChanged() {
        _hasChanges = true
        resourceAreaSettings.get()!!.onSettingsChanged()
        mansionSettings.get()!!.onSettingsChanged()
        // loaded();
        this@SettingsViewModel.activityService.setResultOK()
    }

    private fun saveIfChanged() {
        if (this.mansionSettings.get()!!.hasChanges() || this.ownedExtensions.get()!!.hasChanges() || this.resourceAreaSettings.get()!!.hasChanges()) {
            this.saveChanges()
            this.activityService.setResultOK()
        } else {
            if (_hasChanges) {
                this.activityService.setResultOK()
            } else {
                this.activityService.setResultCanceled()
            }
        }
    }

    override fun onStop() {
        saveIfChanged()

        super.onStop()
    }

    fun getMansionSettings(): MansionSettingsViewModel {
        return this.mansionSettings.get()!!
    }

    override fun onSaveInstanceState(savedInstanceState: IStateStore) {
        savedInstanceState.putBoolean("_hasChanges", _hasChanges)
    }

    override fun onRestoreInstanceState(savedInstanceState: IStateStore) {
        _hasChanges = savedInstanceState.getBoolean("_hasChanges", false)
    }
}
