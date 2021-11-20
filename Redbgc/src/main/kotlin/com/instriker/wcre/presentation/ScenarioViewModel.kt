package com.instriker.wcre.presentation

import com.instriker.wcre.R
import com.instriker.wcre.framework.*
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.Scenario
import java.util.*

class ScenarioViewModel constructor(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val scenarioId = BindingsFactory.bindInteger()
    val extensionName = BindingsFactory.bindString()
    val gameModeName = BindingsFactory.bindString()
    val name = BindingsFactory.bindString()
    val extensionId = BindingsFactory.bindInteger(1)
    val description = BindingsFactory.bindString()
    val piles = BindingsFactory.bindCollection<PileSummaryViewModel>()
    val useBasicResources = BindingsFactory.bindBoolean()
    val isOfficialScenario = BindingsFactory.bindBoolean()
    val groupTitle = BindingsFactory.bindString()
    val hasGroupTitle = BindingsFactory.bindBoolean(false)
    val requiredExtensionNames = BindingsFactory.bindString()
    val requiredOtherExtensions = BindingsFactory.bindBoolean(false)
    val generationSummary = BindingsFactory.bind<ResourceAreaSummaryViewModel>()

    private val _extensionsById: HashMap<Int, GameExtension>
    private var _piles: Array<Array<Card>>? = null
    private var _detailsLoaded: Boolean = false

    init {
        this._extensionsById = GameExtension.toMap(this.gameContentService.extensions)
    }

    fun showCardList(): Any {
        val piles = this._piles ?: return Unit

        val fullPilesData: Array<Array<Card>>
        val titles = HashMap<Int, String>()
        if (useBasicResources.get()) {
            val basicResourcesPiles = this.gameContentService.getBasicResourcesPiles(firstExtensionIdWithBasicResources)
            fullPilesData = basicResourcesPiles + piles

            titles.put(0, this.resourceServices.getString(R.string.piles_basic_resource_group_title))
            titles.put(basicResourcesPiles.size, this.resourceServices.getString(R.string.piles_scenario_group_title))
        } else {
            fullPilesData = piles
            titles.put(0, this.resourceServices.getString(R.string.piles_scenario_group_title))
        }
        this.activityService.navigateToGeneratedPileList(fullPilesData, true, titles, false)
        return Unit
    }

    private // SHOULD REFACTOR...
            // It's part of an extension containing basic resources
            // Basic
            // Alliance
            // Mercenaries
    val firstExtensionIdWithBasicResources: Int
        get() {

            val fromExtensionId = extensionId.get()
            when (fromExtensionId) {
                1, 2, 5 -> return fromExtensionId

                else -> return 1
            }
        }

    fun showFullDescription() {
        val descr = description.get() ?: ""
        if (descr.isNotEmpty()) {
            val titleButton = resourceServices.getString(R.string.description)
            val closeButton = resourceServices.getString(R.string.close)
            activityService.showDialog(titleButton, descr, closeButton, null)
        }
    }

    fun loadDetails(completedCallback: (() -> Unit)?) {
        loadDetails(null, completedCallback)
    }

    private inner class LoadDetailsResult {
        var _pilesData: Array<Array<Card>>? = null
        var _additionalrequiredExtensions: String? = null
        var _piles: ArrayList<PileSummaryViewModel>? = null
        var _generationSummary: ResourceAreaSummaryViewModel? = null
    }

    fun loadDetails(scenariosPiles: Array<Array<Card>>?, completedCallback: (() -> Unit)?) {
        if (_detailsLoaded) {
            return
        }

        val gameContentService = gameContentService

        Promises.run({
            val piles1 = ArrayList<PileSummaryViewModel>()
            val pilesData = scenariosPiles ?: gameContentService.getCardPilesForScenario(scenarioId.get())

            val requiredExtensions = TreeMap<Int, Boolean>()
            var pileIndex = 0
            for (cards in pilesData) {
                for (card in cards) {
                    // We don't care about quantity here
                    card.quantity = 1
                }
                val pvm = PileSummaryViewModel.Create(cards)
                pvm.isOdd.set(pileIndex % 2 != 0)
                pvm.pileNumber.set(pileIndex + 1)

                for (card in cards) {
                    val exId = card.extensionId
                    if (!requiredExtensions.containsKey(exId)) {
                        requiredExtensions.put(exId, true)
                    }
                }

                piles1.add(pvm)
                pileIndex++
            }

            var additionalrequiredExtensions = ""
            val iterator = requiredExtensions.keys.iterator()
            while (iterator.hasNext()) {
                val id = iterator.next()
                if (id !== extensionId.get()) {
                    val extName = _extensionsById.getValue(id).name
                    if (additionalrequiredExtensions.length > 0) {
                        additionalrequiredExtensions += " & "
                    }
                    additionalrequiredExtensions += extName
                }
            }

            val result = LoadDetailsResult()
            result._generationSummary = generateSummary(pilesData)
            result._additionalrequiredExtensions = additionalrequiredExtensions
            result._piles = piles1
            result._pilesData = pilesData
            result
        }, { result ->
            Bindings.setCollection(this@ScenarioViewModel.piles, result._piles!!)
            this@ScenarioViewModel.requiredExtensionNames.set(result._additionalrequiredExtensions)
            this@ScenarioViewModel.requiredOtherExtensions.set(result._additionalrequiredExtensions!!.length > 0)
            this@ScenarioViewModel.generationSummary.set(result._generationSummary)
            this@ScenarioViewModel._piles = result._pilesData
            this@ScenarioViewModel._detailsLoaded = true

            completedCallback?.invoke()
        }, { exception ->
            this@ScenarioViewModel.activityService.showMessage(this@ScenarioViewModel.resourceServices.getString(R.string.errorLoadingCards))
        })
    }

    companion object {

        fun Create(serviceLocator: ServiceLocator, scenario: Scenario, extensionsById: Map<Int, GameExtension>, gameModes: Array<String>): ScenarioViewModel {
            val svm = ScenarioViewModel(serviceLocator)
            svm.isOfficialScenario.set(scenario.id >= 0)
            svm.name.set(scenario.name)
            svm.description.set(scenario.description)
            svm.scenarioId.set(scenario.id)
            svm.useBasicResources.set(scenario.useBasicResources)
            svm.extensionId.set(scenario.extensionId)

            if (scenario.gameMode != null) {
                svm.gameModeName.set(gameModes[scenario.gameMode.ordinal])
            } else if (scenario.skillSystem) {
                svm.gameModeName.set(serviceLocator.resourceServices.getString(R.string.skillSystem))
            }
            svm.extensionName.set(getExtensionName(scenario, extensionsById))
            return svm
        }

        private fun getExtensionName(scenario: Scenario, extensionsById: Map<Int, GameExtension>): String? {
            val extension = extensionsById[scenario.extensionId]
            return extension?.name
        }

        private fun generateSummary(scenariosCards: Array<Array<Card>>): ResourceAreaSummaryViewModel {
            val gmsvm = ResourceAreaSummaryViewModel()
            gmsvm.generateSummary(scenariosCards)
            return gmsvm
        }
    }
}