package com.instriker.wcre.presentation

import androidx.databinding.ObservableField
import com.instriker.wcre.R
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.Options.MansionDeckOptions
import com.instriker.wcre.generators.Options.RangeOptions
import com.instriker.wcre.repositories.GameExtension
import java.util.*

class MansionSettingsViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    val eventCount = BindingsFactory.bind<InputRangeViewModel>()
    val infectedCount = BindingsFactory.bind<InputRangeViewModel>()
    val tokensCount = BindingsFactory.bind<InputRangeViewModel>()
    val itemsCount = BindingsFactory.bind<InputRangeViewModel>()
    val excludedCardsCount = BindingsFactory.bindInteger(0)
    val isSingleExcludedCards = BindingsFactory.bindBoolean(false)

    private var _hasChanges: Boolean = false
    private var _mansionDeckOptions: MansionDeckOptions? = null

    private var mansionDeckOptions: MansionDeckOptions
        get() {
            return _mansionDeckOptions!!
        }
        set(value) {
            _mansionDeckOptions = value
        }

    init {
        this.initMansionOptions()
    }

    private fun initMansionOptions() {
        this.mansionDeckOptions = this.settingsService.mansionDeckOptions

        val asExtFormat = this.resourceServices.getString(R.string.as_extension_format)
        val extensions = this.gameContentService.extensions
        for (ext in extensions) {
            ext.name = String.format(asExtFormat, ext.name)
        }

        initItemsCounts(extensions)
        initTokensCounts(extensions)
        initEventCounts(extensions)
        initInfectedCounts(extensions)
        initExcludedCards()
    }

    private fun initExcludedCards() {
        this.excludedCardsCount.set(this.mansionDeckOptions.excludedCards.size)
        this.isSingleExcludedCards.set(this.mansionDeckOptions.excludedCards.size == 1)
    }

    private fun initItemsCounts(extensionsLabels: Array<GameExtension>) {
        val countsByExtensions = this.gameContentService
                .mansionItemsCountByExtensions
        initCardCounts(extensionsLabels, countsByExtensions, this.itemsCount, mansionDeckOptions.itemsOptions)
    }

    private fun initTokensCounts(extensionsLabels: Array<GameExtension>) {
        val countsByExtensions = this.gameContentService.tokensCountByExtensions
        initCardCounts(extensionsLabels, countsByExtensions, this.tokensCount, mansionDeckOptions.tokensOptions)
    }

    private fun initEventCounts(extensionsLabels: Array<GameExtension>) {
        val countsByExtensions = this.gameContentService.eventCountByExtensions
        initCardCounts(extensionsLabels, countsByExtensions, this.eventCount, mansionDeckOptions.eventsOptions)
    }

    private fun initInfectedCounts(extensionsLabels: Array<GameExtension>) {
        val countsByExtensions = this.gameContentService.infectedCountByExtensions
        initCardCounts(extensionsLabels, countsByExtensions, this.infectedCount, mansionDeckOptions.infectedOptions.totalCount)
    }

    private fun initCardCounts(
            extensionsLabels: Array<GameExtension>,
            countsByExtensions: Map<Int, Int>,
            rangeViewModel: ObservableField<InputRangeViewModel>,
            valueSetting: RangeOptions) {
        val extensionsByIndex = HashMap<Int, Int>()

        val tokensChoices = ArrayList<String>()
        var tokensGameIndex = 0
        for (ext in extensionsLabels) {
            if (countsByExtensions.getOrDefault(ext.id, 0) > 0) {
                extensionsByIndex.put(tokensGameIndex++, ext.id)
                tokensChoices.add(ext.name)
            }
        }
        tokensChoices.addAll(Arrays.asList(*this.resourceServices.getStringArray(R.array.asExtensionOrCustomChoices)))

        val total = countsByExtensions.keys.sumBy { key: Int ->
            countsByExtensions.getOrDefault(key, 0)
        }

        rangeViewModel.set(InputRangeViewModel(tokensChoices.toTypedArray(), 0, total,
                object : IQuickRangeSetting {
                    override fun getChoiceIndex(minValue: Int, maxValue: Int): Int {
                        if (minValue == maxValue) {
                            if (minValue == 0) {
                                return ExtensionOrCustomChoicesNone + extensionsByIndex.size
                            }

                            for (key in countsByExtensions.keys) {
                                val cardCount = countsByExtensions[key]
                                if (cardCount == minValue) {
                                    return extensionsByIndex.filter { it.value == key }.getValue(0)
                                }
                            }

                            if (minValue == total) {
                                return ExtensionOrCustomChoicesAll + extensionsByIndex.size
                            }
                        }

                        return ExtensionOrCustomChoicesCustom + extensionsByIndex.size
                    }

                    override fun isCustomChoice(index: Int): Boolean {
                        return index == ExtensionOrCustomChoicesCustom + extensionsByIndex.size
                    }

                    override fun getStandardRange(index: Int): RangeValue<Int> {
                        val adjustedIndex = index - extensionsByIndex.size
                        if (adjustedIndex < 0) {
                            val extensionId = extensionsByIndex.getValue(index)
                            val count = countsByExtensions.getOrDefault(extensionId, 0)
                            return RangeValue(count, count)
                        } else {
                            return when (adjustedIndex) {
                                ExtensionOrCustomChoicesNone -> RangeValue(0, 0)

                                ExtensionOrCustomChoicesAll -> RangeValue(total, total)

                                else -> RangeValue(0, 0)
                            }
                        }
                    }
                }, valueSetting))
    }

    fun saveChanges() {
        this.settingsService.mansionDeckOptions = this.mansionDeckOptions

        this._hasChanges = false
        (this.itemsCount.get() as InputRangeViewModel).setNotChanged()
        (this.tokensCount.get() as InputRangeViewModel).setNotChanged()
        (this.eventCount.get() as InputRangeViewModel).setNotChanged()
        (this.infectedCount.get() as InputRangeViewModel).setNotChanged()
    }

    fun hasChanges(): Boolean {
        return this._hasChanges || (this.itemsCount.get() as InputRangeViewModel).hasChanges()
                || (this.tokensCount.get() as InputRangeViewModel).hasChanges()
                || (this.eventCount.get() as InputRangeViewModel).hasChanges()
                || (this.infectedCount.get() as InputRangeViewModel).hasChanges()
    }

    fun manageExcludedCards() {
        this.activityService.navigateToExcludedMansionCardsSettings()
    }

    protected fun onExcludedCardsChanged() {
        this.initMansionOptions()
    }

    public override fun onSettingsChanged() {
        onExcludedCardsChanged()
    }

    companion object {
        private val ExtensionOrCustomChoicesNone = 0
        private val ExtensionOrCustomChoicesAll = 1
        private val ExtensionOrCustomChoicesCustom = 2
    }
}