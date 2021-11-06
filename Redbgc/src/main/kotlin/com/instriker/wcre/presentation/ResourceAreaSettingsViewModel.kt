package com.instriker.wcre.presentation

import androidx.databinding.Observable
import com.instriker.wcre.R
import com.instriker.wcre.config.Constants
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.Options.RangeOptions
import com.instriker.wcre.generators.Options.ResourceAreaOptions
import com.instriker.wcre.repositories.CardType

class ResourceAreaSettingsViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    val useBasicResources = BindingsFactory.bindBoolean()
    val useStandardPile = BindingsFactory.bindBoolean()
    val allowDuplicatePiles = BindingsFactory.bindBoolean()
    val allWeaponsByType = BindingsFactory.bindBoolean()
    val pileWeaponsByType = BindingsFactory.bindBoolean()
    val excludedCardsCount = BindingsFactory.bindInteger(0)
    val isSingleExcludedCards = BindingsFactory.bindBoolean(false)
    val mustTrash = BindingsFactory.bindBoolean(false)
    val mustExtraExplore = BindingsFactory.bindBoolean(false)

    val pileCount = BindingsFactory.bind<InputRangeViewModel>()
    val weaponsCount = BindingsFactory.bind<InputRangeViewModel>()
    val itemsCount = BindingsFactory.bind<InputRangeViewModel>()
    val ammunitionsCount = BindingsFactory.bind<InputRangeViewModel>()
    val actionsCount = BindingsFactory.bind<InputRangeViewModel>()

    private var _hasChanges: Boolean = false

    private var _resourceAreaOptions: ResourceAreaOptions? = null
    private val resourceAreaOptions: ResourceAreaOptions get() {
        return _resourceAreaOptions!!
    }

    init {
        this.initResourceAreaOptions()
    }

    private fun initResourceAreaOptions() {
        this._resourceAreaOptions = this.settingsService.resourceAreaOptions

        this.useBasicResources.set(this.resourceAreaOptions.useBasicResources)
        this.useStandardPile.set(this.resourceAreaOptions.useStandardPile)
        this.allowDuplicatePiles.set(this.resourceAreaOptions.allowDuplicatePiles)
        this.allWeaponsByType.set(this.resourceAreaOptions.allWeaponsByType)
        this.pileWeaponsByType.set(this.resourceAreaOptions.pileWeaponsByType)
        this.excludedCardsCount.set(this.resourceAreaOptions.excludedCards.size)
        this.isSingleExcludedCards.set(this.resourceAreaOptions.excludedCards.size == 1)
        this.mustTrash.set(this.resourceAreaOptions.mustTrash)
        this.mustExtraExplore.set(this.resourceAreaOptions.mustExtraExplore)

        this.useBasicResources.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = useBasicResources.get()
                val currentValue = resourceAreaOptions.useBasicResources
                if (newValue !== currentValue) {
                    resourceAreaOptions.useBasicResources = newValue
                    _hasChanges = true
                }
            }
        })

        this.useStandardPile.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = useStandardPile.get()
                val currentValue = resourceAreaOptions.useStandardPile
                if (newValue != currentValue) {
                    resourceAreaOptions.useStandardPile = newValue
                    _hasChanges = true
                }
            }
        })

        this.allowDuplicatePiles.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val currentValue = resourceAreaOptions.allowDuplicatePiles
                val newValue = allowDuplicatePiles.get()
                if (newValue != currentValue) {
                    resourceAreaOptions.allowDuplicatePiles = newValue
                    _hasChanges = true
                }
            }
        })

        this.allWeaponsByType.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = allWeaponsByType.get()
                val currentValue = resourceAreaOptions.allWeaponsByType
                if (newValue != currentValue) {
                    resourceAreaOptions.allWeaponsByType = newValue
                    _hasChanges = true
                }
            }
        })

        this.pileWeaponsByType.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = pileWeaponsByType.get()
                val currentValue = resourceAreaOptions.pileWeaponsByType
                if (newValue != currentValue) {
                    resourceAreaOptions.pileWeaponsByType = newValue
                    _hasChanges = true
                }
            }
        })

        this.mustTrash.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = mustTrash.get()
                val currentValue = resourceAreaOptions.mustTrash
                if (newValue != currentValue) {
                    resourceAreaOptions.mustTrash = newValue
                    _hasChanges = true
                }
            }
        })

        this.mustExtraExplore.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = mustExtraExplore.get()
                val currentValue = resourceAreaOptions.mustExtraExplore
                if (newValue != currentValue) {
                    resourceAreaOptions.mustExtraExplore = newValue
                    _hasChanges = true
                }
            }
        })

        this.pileCount.set(InputRangeViewModel(
                this.resourceServices.getStringArray(R.array.pileCountChoices),
                1, MaxPileCount,
                object : IQuickRangeSetting {
                    override fun getChoiceIndex(minValue: Int, maxValue: Int): Int {
                        return if (minValue == StandardPileCount && maxValue == StandardPileCount) 0 else 1
                    }

                    override fun isCustomChoice(index: Int): Boolean {
                        return index == 1
                    }

                    override fun getStandardRange(index: Int): RangeValue<Int> {
                        return when (index) {
                            0 -> RangeValue(StandardPileCount, StandardPileCount)
                            else -> RangeValue(0, 0)
                        }
                    }
                }, resourceAreaOptions.pileCount))

        this.weaponsCount.set(createCardTypeInputRangeViewModel(CardType.Weapon, resourceAreaOptions.weaponsCount))
        this.itemsCount.set(createCardTypeInputRangeViewModel(CardType.Item, resourceAreaOptions.itemsCount))
        this.ammunitionsCount.set(createCardTypeInputRangeViewModel(CardType.Ammunition, resourceAreaOptions.ammunitionsCount))
        this.actionsCount.set(createCardTypeInputRangeViewModel(CardType.Action, resourceAreaOptions.actionsCount))
    }

    private fun createCardTypeInputRangeViewModel(cardType: CardType, options: RangeOptions): InputRangeViewModel {
        val maxCards = Math.min(NoMaximumCount, Math.min(MaxPileCount, gameContentService.getTotalDistinctCardsCount(cardType)))
        return InputRangeViewModel(this.resourceServices.getStringArray(R.array.scenarioCardTypeCountChoices), NoMinimumCount, maxCards,
                object : IQuickRangeSetting {
                    override fun getChoiceIndex(minValue: Int, maxValue: Int): Int {
                        if (minValue == NoMinimumCount && maxValue >= maxCards) {
                            return CardTypeOrCustomChoicesAny
                        } else if (minValue == 0 && maxValue == 0) {
                            return CardTypeOrCustomChoicesNone
                        } else if (minValue >= maxCards && minValue >= maxCards) {
                            return CardTypeOrCustomChoicesAll
                        } else {
                            return CardTypeOrCustomChoicesCustom
                        }
                    }

                    override fun isCustomChoice(index: Int): Boolean {
                        return index == CardTypeOrCustomChoicesCustom
                    }

                    override fun getStandardRange(index: Int): RangeValue<Int> {
                        return when (index) {
                            CardTypeOrCustomChoicesNone -> RangeValue(0, 0)

                            CardTypeOrCustomChoicesAll -> RangeValue(maxCards, maxCards)

                            CardTypeOrCustomChoicesAny -> RangeValue(NoMinimumCount, maxCards)

                            else -> RangeValue(0, 0)
                        }
                    }
                }, options)
    }

    fun saveChanges() {
        this.settingsService.resourceAreaOptions = this.resourceAreaOptions
        _hasChanges = false
        (this.pileCount.get() as InputRangeViewModel).setNotChanged()
        (this.weaponsCount.get() as InputRangeViewModel).setNotChanged()
        (this.itemsCount.get() as InputRangeViewModel).setNotChanged()
        (this.ammunitionsCount.get() as InputRangeViewModel).setNotChanged()
        (this.actionsCount.get() as InputRangeViewModel).setNotChanged()
    }

    fun hasChanges(): Boolean {
        return this._hasChanges
                || (this.pileCount.get() as InputRangeViewModel).hasChanges()
                || (this.weaponsCount.get() as InputRangeViewModel).hasChanges()
                || (this.itemsCount.get() as InputRangeViewModel).hasChanges()
                || (this.ammunitionsCount.get() as InputRangeViewModel).hasChanges()
                || (this.actionsCount.get() as InputRangeViewModel).hasChanges()
    }

    fun manageExcludedCards() {
        this.activityService.navigateToExcludedScenarioCardsSettings()
    }

    protected fun onExcludedCardsChanged() {
        this.initResourceAreaOptions()
    }

    public override fun onSettingsChanged() {
        onExcludedCardsChanged()
    }

    companion object {
        private val CardTypeOrCustomChoicesAny = 0
        private val CardTypeOrCustomChoicesNone = 1
        private val CardTypeOrCustomChoicesAll = 2
        private val CardTypeOrCustomChoicesCustom = 3

        private val StandardPileCount = 18
        private val NoMinimumCount = 0
        private val NoMaximumCount = Constants.ScenariosMaxAllowedCardsByType
        private val MaxPileCount = Constants.ScenariosMaxPileCount
    }
}
