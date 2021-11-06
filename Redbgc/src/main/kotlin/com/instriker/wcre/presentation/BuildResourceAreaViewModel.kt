package com.instriker.wcre.presentation

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.instriker.wcre.R

import java.util.Arrays
import java.util.Comparator
import java.util.HashMap

import com.instriker.wcre.config.Constants
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.ResourceAreaGenerator
import com.instriker.wcre.generators.ResourceAreaRandomOfficialScenario
import com.instriker.wcre.generators.ScenarioResourceArea
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.generators.Options.ResourceAreaOptions
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.Scenario

class BuildResourceAreaViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    val isStandardScenario = BindingsFactory.bindBoolean(false)
    val displayDetails: ObservableField<ScenarioViewModel> = BindingsFactory.bind()

    val isGenerateMode = BindingsFactory.bindBoolean(false)
    val isPickRandomMode = BindingsFactory.bindBoolean(false)

    val generationSuccess = BindingsFactory.bindBoolean(false)

    private var _resourceAreaCards: Array<Array<Card>>? = null

    init {

        val mode = generationMode
        isGenerateMode.set(mode == GenerateMode)
        isPickRandomMode.set(mode == RandomPickMode)

        loadInitialScenario()
    }

    private fun loadInitialScenario() {
        if (isGenerateMode.get()) {
            generateNewResourceArea()
        } else {
            pickRandomScenario()
        }
    }

    fun pickRandomScenario() {
        isStandardScenario.set(true)

        val extOptions = this.settingsService.extensionOptions

        val generator = ResourceAreaRandomOfficialScenario(gameContentService)
        try {
            val picked = generator.pickRandom(extOptions)
            if (picked != null) {
                // Do not use found cards because we want to use the "UseBasicResources" feature...
                setActiveScenario(picked.scenario, null)
                _resourceAreaCards = picked.piles
            } else {
                setActiveScenario(null, null)
            }
        } catch (ex: Exception) {
            if (Constants.DEBUG) {
                ex.printStackTrace()
            }
            activityService.showMessage("An unpected problem occurred while generating the resource area.")
        }

    }

    fun generateNewResourceArea() {
        isStandardScenario.set(false)

        val options = this.settingsService.resourceAreaOptions
        val extOptions = this.settingsService.extensionOptions
        val generator = ResourceAreaGenerator(gameContentService)

        try {
            val resourceAreaCards = generator.generate(extOptions, options)

            if (resourceAreaCards != null && resourceAreaCards.isNotEmpty()) {
                val scenario = Scenario(-1, resourceServices.getString(R.string.customScenario), null, -1, false, null, false)
                setActiveScenario(scenario, resourceAreaCards)
            } else {
                setActiveScenario(null, null)
            }
        } catch (ex: Exception) {
            if (Constants.DEBUG) {
                ex.printStackTrace()
            }
            activityService.showMessage("An unpected problem occurred while generating the resource area.")
        }

    }

    private fun setActiveScenario(scenarioData: Scenario?, scenariosPiles: Array<Array<Card>>?) {
        val extensionsById = GameExtension.toMap(this.gameContentService.extensions)
        val gameModes = this.resourceServices.getStringArray(R.array.gameModes)

        val succeded = scenarioData != null
        if (succeded) {
            val svm = ScenarioViewModel.Create(this.serviceLocator, scenarioData!!, extensionsById, gameModes)
            svm.loadDetails(scenariosPiles) {
                displayDetails.set(svm)
                if (scenariosPiles != null) {
                    _resourceAreaCards = scenariosPiles
                }
            }
        } else {
            displayDetails.set(null)
        }

        generationSuccess.set(succeded)
    }

    private val generationMode: String
        get() = this.activityService.getParameterString("Mode", RandomPickMode)

    fun changeSettings() {
        this.activityService.navigateToSettings(false, isGenerateMode.get())
    }

    //@SuppressLint("UseSparseArrays")
    fun showCardList() {
        val resourceAreaCards = _resourceAreaCards
        if (resourceAreaCards == null) {
            // If not ready, do not crash, the user will try again in 1 second...
            return
        }

        if (!this.isStandardScenario.get()) {
            sortResults(resourceAreaCards)
        }

        var lastBasicResourcePiles = -1
        for (i in resourceAreaCards.indices) {
            if (resourceAreaCards[i][0].basicResourcePileGroup != null) {
                lastBasicResourcePiles = i
            } else {
                break
            }
        }

        val titles = HashMap<Int, String>()
        if (lastBasicResourcePiles > -1) {
            titles.put(0, this.resourceServices.getString(R.string.piles_basic_resource_group_title))
            titles.put(lastBasicResourcePiles + 1, this.resourceServices.getString(R.string.piles_scenario_group_title))
        } else {
            titles.put(0, this.resourceServices.getString(R.string.piles_scenario_group_title))
        }

        this.activityService.navigateToGeneratedPileList(resourceAreaCards, this.isStandardScenario.get(), titles, false)
    }

    private fun sortResults(cards: Array<Array<Card>>) {
        val cmp = CardComparators.createComparator(CardSortOrder.ByType)

        Arrays.sort(cards) { arg0, arg1 ->
            val card0 = arg0[0]
            val card1 = arg1[0]
            val arg0IsBasic = card0.basicResourcePileGroup != null
            val arg1IsBasic = card1.basicResourcePileGroup != null
            if (arg0IsBasic && arg1IsBasic) {
                card0.basicResourcePileGroup!! - card1.basicResourcePileGroup!!
            } else if (arg0IsBasic == arg1IsBasic) {
                cmp!!.compare(card0, card1)
            } else if (arg0IsBasic) {
                -1
            } else {
                1
            }
        }
    }

    override fun onSettingsChanged() {
        loadInitialScenario()
    }

    companion object {
        val GenerateMode = "generate"
        val RandomPickMode = "random"
    }
}