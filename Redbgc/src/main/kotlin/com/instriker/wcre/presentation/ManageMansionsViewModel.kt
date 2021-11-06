package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableList

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import com.instriker.wcre.framework.*
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.Mansion
import com.instriker.wcre.repositories.Mansions
import com.instriker.wcre.services.*

class ManageMansionsViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val mansions = BindingsFactory.bindCollection<MansionViewModel>()
    val isEmpty = BindingsFactory.bindBoolean(false)
    val displayingViewIndex = BindingsFactory.bindInteger(0)
    val displayDetails = BindingsFactory.bindBoolean(false)

    init {

        this.mansions.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<MansionViewModel>>() {
            override fun onChanged(sender: ObservableList<MansionViewModel>) {
                isEmpty.set(Bindings.getSize(mansions) == 0)
            }

            override fun onItemRangeChanged(sender: ObservableList<MansionViewModel>, positionStart: Int, itemCount: Int) {}

            override fun onItemRangeInserted(sender: ObservableList<MansionViewModel>, positionStart: Int, itemCount: Int) {}

            override fun onItemRangeMoved(sender: ObservableList<MansionViewModel>, fromPosition: Int, toPosition: Int, itemCount: Int) {}

            override fun onItemRangeRemoved(sender: ObservableList<MansionViewModel>, positionStart: Int, itemCount: Int) {}
        })

        this.displayingViewIndex.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                val current = (sender as ObservableInt).get()
                Bindings.getItemAt(mansions, current).loadDetails(null)

                if (current < Bindings.getSize(mansions) - 1) {
                    Bindings.getItemAt(mansions, current + 1).loadDetails(null)
                }

                if (current > 0) {
                    Bindings.getItemAt(mansions, current - 1).loadDetails(null)
                }
            }
        })

        refreshMansions()
    }

    fun showMansionDetail(index: Int?): Any {
        displayingViewIndex.set(index!!)
        displayDetails.set(true)
        return Unit
    }

    private fun refreshMansions() {

        val extensionsById = GameExtension.toMap(this.gameContentService.extensions)
        Bindings.setCollection(this.mansions, getMansions(extensionsById))
    }

    private fun getMansions(extensionsById: Map<Int, GameExtension>): Collection<MansionViewModel> {
        val extOptions = this.settingsService.extensionOptions

        val mansions = ArrayList<MansionViewModel>()

        val gameService = this.gameContentService
        val allMansions = gameService.mansions

        for (mansion in filterMansionsByExtensions(Arrays.asList(*allMansions), extOptions.fromExtensions)) {
            val svm = MansionViewModel.Create(this.serviceLocator, mansion, extensionsById)
            mansions.add(svm)
        }

        return mansions
    }

    private fun filterMansionsByExtensions(allMansions: Collection<Mansion>, fromExtensions: Collection<Int>): Collection<Mansion> {
        return Mansions.filterMansionsByExtensions(allMansions, fromExtensions)
    }

    fun changeSettings() {
        this.activityService.navigateToSettings(false, false)
    }

    override fun onSettingsChanged() {
        refreshMansions()
    }

    override fun onBackPressed(): Boolean {
        if (displayDetails.get()) {
            displayDetails.set(false)
            return false
        }
        return super.onBackPressed()
    }
}