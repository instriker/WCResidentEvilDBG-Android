package com.instriker.wcre.presentation

import com.instriker.wcre.R
import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.Promises
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.Mansion
import java.util.*

class MansionViewModel constructor(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    val mansionId = BindingsFactory.bindInteger()
    val extensionName = BindingsFactory.bindString()
    val difficulty = BindingsFactory.bindString()
    val hasDifficulty = BindingsFactory.bindBoolean()
    val name = BindingsFactory.bindString()
    val description = BindingsFactory.bindString()
    val generationSummary = BindingsFactory.bind<MansionSummaryViewModel?>(null)
    val cards = BindingsFactory.bindCollection<PileSummaryViewModel>()
    val hideCardsList = BindingsFactory.bindBoolean(false)

    private var _mansionCards: Array<Card>? = null
    private var _detailsLoaded: Boolean = false

    fun showCardList(): Any {
        val mansionCards = _mansionCards ?: return Unit
        val result = mansionCards
                .map { current ->
                    Array(current.quantity) {
                        current
                    }
                }
                .toTypedArray()

        val titles = HashMap<Int, String>()
        titles[0] = this.name.get() ?: ""

        this.activityService.navigateToGeneratedPileList(result, true, titles, false)
        return Unit
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

    private inner class LoadDetailsResult(val cards: ArrayList<PileSummaryViewModel>,
                                          val pilesData: Array<Card>,
                                          val summary: MansionSummaryViewModel) {
    }

    fun loadDetails(mansionCards: Array<Card>?, completedCallback: (() -> Unit)?) {
        // onattach=better?
        if (_detailsLoaded) {
            return
        }

        val gameContentService = gameContentService

        Promises.run({
            val cards = ArrayList<PileSummaryViewModel>()
            val pilesData = mansionCards ?: gameContentService.getCardsForMansion(mansionId.get())

            for ((pileIndex, card) in pilesData.withIndex()) {
                val pvm = PileSummaryViewModel.Create(arrayOf(card))
                pvm.isOdd.set(pileIndex % 2 == 0)
                pvm.pileNumber.set(pileIndex + 1)

                cards.add(pvm)
            }

            LoadDetailsResult(cards, pilesData, generateSummary(pilesData))
        }, { result ->
            this@MansionViewModel.generationSummary.set(result.summary)
            Bindings.setCollection(this@MansionViewModel.cards, result.cards)
            this@MansionViewModel._mansionCards = result.pilesData
            this@MansionViewModel._detailsLoaded = true

            completedCallback?.invoke()
        }) { value -> this@MansionViewModel.activityService.showMessage(this@MansionViewModel.resourceServices.getString(R.string.errorLoadingCards)) }
    }

    private fun generateSummary(mansionCards: Array<Card>): MansionSummaryViewModel {
        val gmsvm = MansionSummaryViewModel()
        gmsvm.generateSummary(mansionCards)
        return gmsvm
    }

    companion object {
        fun Create(serviceLocator: ServiceLocator, mansion: Mansion, extensionsById: Map<Int, GameExtension>): MansionViewModel {
            val svm = MansionViewModel(serviceLocator)
            svm.name.set(mansion.name)
            svm.description.set(mansion.description)
            svm.mansionId.set(mansion.id)
            svm.extensionName.set(getExtensionName(mansion, extensionsById))

            svm.hasDifficulty.set(mansion.difficulty != null)
            if (mansion.difficulty != null) {
                val mansionDifficulties = serviceLocator.resourceServices.getStringArray(R.array.mansionDifficulties)
                svm.difficulty.set(mansionDifficulties[mansion.difficulty.ordinal])
            }

            return svm
        }

        private fun getExtensionName(mansion: Mansion, extensionsById: Map<Int, GameExtension>): String? {
            val extension = extensionsById[mansion.extensionId]
            return extension?.name
        }
    }
}