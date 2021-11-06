package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableList
import com.instriker.wcre.R
import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.GameMode
import com.instriker.wcre.repositories.Scenario
import com.instriker.wcre.repositories.Scenarios
import java.util.*

class ManageScenariosViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val scenarios = BindingsFactory.bindCollection<ScenarioViewModel>()
    val isEmpty = BindingsFactory.bindBoolean(false)
    val displayingViewIndex = BindingsFactory.bindInteger(0)
    val displayDetails = BindingsFactory.bindBoolean(false)

    private var _activeSearchQuery: String? = null
    private var _activeGameModeFilter: GameMode? = null
    private var _showSkillSystemOnly: Boolean? = null

    init {

        this.scenarios.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<ScenarioViewModel>>() {
            override fun onChanged(scenarioViewModels: ObservableList<ScenarioViewModel>) {
                isEmpty.set(Bindings.getSize(scenarios) == 0)
            }

            override fun onItemRangeChanged(scenarioViewModels: ObservableList<ScenarioViewModel>, i: Int, i1: Int) {

            }

            override fun onItemRangeInserted(scenarioViewModels: ObservableList<ScenarioViewModel>, i: Int, i1: Int) {

            }

            override fun onItemRangeMoved(scenarioViewModels: ObservableList<ScenarioViewModel>, i: Int, i1: Int, i2: Int) {

            }

            override fun onItemRangeRemoved(scenarioViewModels: ObservableList<ScenarioViewModel>, i: Int, i1: Int) {

            }
        })

        this.displayingViewIndex.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val current = displayingViewIndex.get()
                if (current != null) {
                    Bindings.getItemAt(scenarios, current).loadDetails(null)

                    if (current < Bindings.getSize(scenarios) - 1) {
                        Bindings.getItemAt(scenarios, current + 1).loadDetails(null)
                    }

                    if (current > 0) {
                        Bindings.getItemAt(scenarios, current - 1).loadDetails(null)
                    }
                }
            }
        })

        refreshScenarios()
    }

    override fun onSearched(query: String) {
        _activeSearchQuery = query
        refreshScenarios()
    }

    private fun refreshScenarios() {
        val extensionsById = GameExtension.toMap(this.gameContentService.extensions)
        this.displayDetails.set(false)
        Bindings.setCollection(this.scenarios, getScenarios(extensionsById))
    }

    private fun getScenarios(extensionsById: Map<Int, GameExtension>): Collection<ScenarioViewModel> {
        val extOptions = this.settingsService.extensionOptions

        val scenarios = ArrayList<ScenarioViewModel>()
        val gameModes = this.resourceServices.getStringArray(R.array.gameModes)

        val gameService = this.gameContentService
        var lastScenarioId = Integer.MIN_VALUE
        val allScenarios = gameService.searchScenarios(_activeSearchQuery, _activeGameModeFilter, _showSkillSystemOnly)

        for (scenario in filterScenariosByExtensions(Arrays.asList(*allScenarios), extOptions.fromExtensions)) {
            val svm = ScenarioViewModel.Create(this.serviceLocator, scenario, extensionsById, gameModes)

            if (lastScenarioId != scenario.extensionId) {
                lastScenarioId = scenario.extensionId
                svm.hasGroupTitle.set(true)
                svm.groupTitle.set(String.format(this.resourceServices.getString(R.string.scenarios_group_title), extensionsById.getValue(lastScenarioId).name))
            }

            scenarios.add(svm)
        }

        return scenarios
    }

    private fun filterScenariosByExtensions(allScenarios: Collection<Scenario>, fromExtensions: Collection<Int>): Collection<Scenario> {
        return Scenarios.filterScenariosByExtensions(allScenarios, fromExtensions)
    }

    fun changeSettings() {
        this.activityService.navigateToSettings(false, false)
    }

    @JvmOverloads fun filterGameMode(gameMode: GameMode? = null) {
        // gameMode can be null...
        _activeGameModeFilter = gameMode
        _showSkillSystemOnly = null
        refreshScenarios()
    }

    fun filterSkillSystem() {
        _activeGameModeFilter = null
        _showSkillSystemOnly = true
        refreshScenarios()
    }

    override fun onSettingsChanged() {
        refreshScenarios()
    }

    fun showScenarioDetail(index: Int): Any {
        displayingViewIndex.set(index)
        displayDetails.set(true)
        return Unit
    }

    override fun onBackPressed(): Boolean {
        if (displayDetails.get()) {
            displayDetails.set(false)
            return false
        }
        return super.onBackPressed()
    }
}