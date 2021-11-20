package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.Options.ExtensionOptions

class OwnedExtensionsSettingsViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val hasBaseSet = BindingsFactory.bindBoolean(false)
    val hasAlliance = BindingsFactory.bindBoolean(false)
    val hasOutbreak = BindingsFactory.bindBoolean(false)
    val hasNightmare = BindingsFactory.bindBoolean(false)
    val hasMercenaries = BindingsFactory.bindBoolean(false)
    val hasPromotions = BindingsFactory.bindBoolean(false)

    private var _hasChanges: Boolean = false

    private val _extensionOptions: ExtensionOptions

    init {
        this._extensionOptions = this.settingsService.extensionOptions

        this.initExtensionOptions()
    }

    private fun initExtensionOptions() {
        this.hasBaseSet.set(this._extensionOptions.fromExtensions.contains(1))
        this.hasAlliance.set(this._extensionOptions.fromExtensions.contains(2))
        this.hasOutbreak.set(this._extensionOptions.fromExtensions.contains(3))
        this.hasNightmare.set(this._extensionOptions.fromExtensions.contains(4))
        this.hasMercenaries.set(this._extensionOptions.fromExtensions.contains(5))
        this.hasPromotions.set(this._extensionOptions.fromExtensions.contains(6))

        this.registerOnExtensionOwnedChanged(this.hasBaseSet, 1)
        this.registerOnExtensionOwnedChanged(this.hasAlliance, 2)
        this.registerOnExtensionOwnedChanged(this.hasOutbreak, 3)
        this.registerOnExtensionOwnedChanged(this.hasNightmare, 4)
        this.registerOnExtensionOwnedChanged(this.hasMercenaries, 5)
        this.registerOnExtensionOwnedChanged(this.hasPromotions, 6)
    }

    fun registerOnExtensionOwnedChanged(hasExtension: ObservableBoolean, extensionId: Int) {
        hasExtension.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                onExtensionOwnedChanged(extensionId, (sender as ObservableBoolean).get())
            }
        })
    }

    private fun onExtensionOwnedChanged(extensionId: Int, mustHaveExtension: Boolean) {
        val extensions = this@OwnedExtensionsSettingsViewModel._extensionOptions.fromExtensions
        val hasExtension = extensions.contains(extensionId)

        if (!hasExtension && mustHaveExtension) {
            extensions.add(extensionId)
            this._extensionOptions.setFromExtensions(extensions)
            _hasChanges = true
        } else if (hasExtension && !mustHaveExtension) {
            while (extensions.remove(Integer.valueOf(extensionId))) {
                _hasChanges = true
            }
            this._extensionOptions.setFromExtensions(extensions)
        } else {
            //System.out.println("No change needed for extension " + Integer.toString(extensionId));
        }
    }

    fun saveChanges() {
        this.settingsService.extensionOptions = this._extensionOptions
        this._hasChanges = false
    }

    fun hasChanges(): Boolean {
        return this._hasChanges
    }
}
