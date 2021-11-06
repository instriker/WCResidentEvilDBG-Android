package com.instriker.wcre.presentation

import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.framework.Promises
import com.instriker.wcre.framework.IStateStore
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.GameExtension
import java.util.*

class GeneratedPileListViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val cards = BindingsFactory.bindCollection<PileViewModel>()
    val isLoading = BindingsFactory.bindBoolean(true)
    val displayingViewIndex = BindingsFactory.bindInteger(0)
    val displayCardDetails = BindingsFactory.bindBoolean(false)

    private var _sorting: Boolean = false
    private var _currentSortOrder: CardSortOrder? = null

    override fun onStart() {
        if (Bindings.isEmptyCollection(cards)) {
            StartSortingPiles(defaultCardSortOrder)
        }
    }

    private val defaultCardSortOrder: CardSortOrder
        get() = if (isOfficialCards) CardSortOrder.AsRulebook else CardSortOrder.ByType

    private fun StartSortingPiles(sortMode: CardSortOrder) {
        this._currentSortOrder = sortMode
        this._sorting = true

        val gameContentService = this.gameContentService

        // onPreExecute
        this.isLoading.set(true)

        Promises.run({
            val cardSortOrder = sortMode
            val extensionsById = GameExtension.toMap(gameContentService.extensions)
            val sorted = getCardList(extensionsById, cardSortOrder)
            sorted
        }, { sorted ->
            isLoading.set(false)
            displayCardDetails.set(false)
            Bindings.setCollection(this@GeneratedPileListViewModel.cards, sorted)
            this@GeneratedPileListViewModel._sorting = false
        }, {
            isLoading.set(false)
            displayCardDetails.set(false)
            Bindings.clearCollection(this@GeneratedPileListViewModel.cards)
            this@GeneratedPileListViewModel._sorting = false
        })
    }

    //	@Override
    //	protected void onStop() {
    //		SortCardsTask task = this._task;
    //		if (task != null) {
    //			task.cancel(true);
    //			this._task = null;
    //		}
    //	};

    val isOfficialCards: Boolean
        get() = this.activityService.getParameterBoolean("com.instriker.wcre.IsOfficialPiles", false)

    private fun getCardList(extensionsById: HashMap<Int, GameExtension>, sortMode: CardSortOrder): Collection<PileViewModel> {
        val pilesTitles = this.activityService.getParameter<HashMap<Int, String>?>("com.instriker.wcre.PilesTitles", null)

        val ids = piles
        val cardsWanted = getCardsById(ids)
        val cards = generatePilesViewModels(ids, cardsWanted, extensionsById, pilesTitles, sortMode)

        return cards
    }

    private fun generatePilesViewModels(
            ids: Array<IntArray>,
            cardsById: HashMap<Int, Card>,
            extensionsById: HashMap<Int, GameExtension>,
            pilesTitles: HashMap<Int, String>?,
            sortMode: CardSortOrder): ArrayList<PileViewModel> {

        val cards = ArrayList<PileViewModel>()

        var pileIndex = -1

        for (pile in ids) {
            pileIndex++

            var isNewPile = true

            val countByCard = HashMap<Int, Int>()
            val pileStripped = pile.clone()

            val DuplicateId = -1

            for (cardIndex in pileStripped.indices) {
                val cardId = pileStripped[cardIndex]

                var sameCount = 1
                for (sameIndex in pileStripped.size - 1 downTo cardIndex + 1) {
                    if (pileStripped[sameIndex] == cardId) {
                        sameCount++
                        pileStripped[sameIndex] = DuplicateId
                    }
                }
                countByCard.put(cardId, sameCount)
            }

            val cardTypeNames = this.gameContentService.cardTypeNames

            for (cardId in pileStripped) {
                if (cardId == DuplicateId) {
                    continue
                }

                val card = cardsById.getValue(cardId)

                val vm = PileViewModel.Create(card, extensionsById, pileIndex, cardTypeNames, resourceServices)
                vm.count.set(countByCard.getOrDefault(card.id, 0))
                vm.pileNumber.set(pileIndex + 1)

                val title = if (pilesTitles == null || !isNewPile) null else pilesTitles[pileIndex]
                if (title != null) {
                    vm.groupTitle.set(title)
                    vm.hasGroupTitle.set(true)
                }

                cards.add(vm)
                isNewPile = false
            }
        }

        sortByGroup(cards, sortMode)

        return cards
    }

    private fun sortByGroup(cards: ArrayList<PileViewModel>, sortMode: CardSortOrder) {
        if (sortMode == CardSortOrder.AsRulebook) {
            return
        }

        // Split groups to keep them independent
        val groups = splitByGroups(cards)

        // Sort each group individually
        for (currentGroup in groups) {
            // Keep the title
            var titlePileViewModel = currentGroup[0]
            val groupTitle = titlePileViewModel.groupTitle.get()

            // Clear the actual group title
            titlePileViewModel.groupTitle.set(null)
            titlePileViewModel.hasGroupTitle.set(false)

            // Remove rows that are part of the same pile so they are not
            // sorted. They will be inserted after the sort to their new
            // positions so the pile order is kept
            val pilesInfos = extractCardsFromSamePile(currentGroup, sortMode)

            sort(currentGroup, sortMode)

            // Add removed cards to their corresponding pile
            putBackCardsIntoTheirCorrespondingPile(sortMode, currentGroup, pilesInfos)

            // Put back the title to the first item.
            titlePileViewModel = currentGroup[0]
            titlePileViewModel.groupTitle.set(groupTitle)
            titlePileViewModel.hasGroupTitle.set(groupTitle != null && groupTitle.isNotEmpty())
        }

        // Concat groups
        cards.clear()
        for (currentGroup in groups) {
            cards.addAll(currentGroup)
        }

        // Change index / colors
        var pileIndex = -1
        var previousPileNumber = -1
        for (card in cards) {
            val currentPileNumber = card.pileNumber.get()

            if (currentPileNumber != previousPileNumber) {
                pileIndex++
            }

            card.pileNumber.set(pileIndex + 1)
            card.isOdd.set(pileIndex % 2 != 0)

            previousPileNumber = currentPileNumber
        }
    }

    private fun extractCardsFromSamePile(cards: ArrayList<PileViewModel>, sortMode: CardSortOrder): HashMap<PileViewModel, ArrayList<PileViewModel>> {
        val pilesInfos = HashMap<PileViewModel, ArrayList<PileViewModel>>()
        var currentPileCardRoot: PileViewModel? = null

        var index = cards.size - 1

        while (index >= 0) {
            val card = cards[index]

            if (currentPileCardRoot != null && currentPileCardRoot.pileNumber.get() == card.pileNumber.get()) {
                var currentPile: ArrayList<PileViewModel>? = pilesInfos[currentPileCardRoot]
                if (currentPile == null) {
                    currentPile = ArrayList()
                    pilesInfos[currentPileCardRoot] = currentPile
                    currentPile.add(currentPileCardRoot)
                }

                cards.removeAt(index)
                currentPile.add(card)
            } else {
                currentPileCardRoot = card
            }

            index--
        }

        // Sort each groups so groups are sorted by their first item
        val piles = ArrayList(pilesInfos.keys)

        for (currentGroupKey in piles) {
            val currentGroup = pilesInfos.getValue(currentGroupKey)
            sort(currentGroup, sortMode)

            val newGroupHeader = currentGroup.get(0)
            if (newGroupHeader !== currentGroupKey) {
                val globalIndex = cards.indexOf(currentGroupKey)
                cards.removeAt(globalIndex)
                cards.add(globalIndex, newGroupHeader)

                pilesInfos.remove(currentGroupKey)
                pilesInfos.put(newGroupHeader, currentGroup)
            }
        }

        return pilesInfos
    }

    private fun putBackCardsIntoTheirCorrespondingPile(sortMode: CardSortOrder, currentGroup: ArrayList<PileViewModel>,
                                                       pilesInfos: HashMap<PileViewModel, ArrayList<PileViewModel>>) {
        for (alreadySorted in pilesInfos.keys) {
            // Find the position of the pile
            val position = currentGroup.indexOf(alreadySorted)

            val othersInPile = pilesInfos.getValue(alreadySorted)

            // sort each of thoses groups before putting them back at their
            // place in the group
            sort(othersInPile, sortMode)

            currentGroup.remove(alreadySorted)
            currentGroup.addAll(position, othersInPile)
        }
    }

    private fun splitByGroups(cards: ArrayList<PileViewModel>): ArrayList<ArrayList<PileViewModel>> {
        val groups = ArrayList<ArrayList<PileViewModel>>()

        var currentGroup: ArrayList<PileViewModel>? = null
        for (card in cards) {
            if (currentGroup == null || card.groupTitle.get()?.isNotEmpty() == true) {
                currentGroup = ArrayList()
                groups.add(currentGroup)
            }

            currentGroup.add(card)
        }

        return groups
    }

    private fun sort(cards: ArrayList<PileViewModel>, sortMode: CardSortOrder) {
        val comparator: Comparator<PileViewModel>?

        when (sortMode) {
            CardSortOrder.ByName -> {
                comparator = Comparator { lhs, rhs ->
                    var comparison = lhs.name.get()!!.compareTo(rhs.name.get()!!)
                    if (comparison == 0) {
                        comparison = Integer.valueOf(lhs.extensionId.get()).compareTo(rhs.extensionId.get())
                    }
                    comparison
                }
            }
            CardSortOrder.ByType -> {
                comparator = Comparator { lhs, rhs ->
                    var comparison = Integer.valueOf(lhs.cardTypeId!!.ordinal).compareTo(rhs.cardTypeId!!.ordinal)
                    if (comparison == 0) {
                        comparison = lhs.name.get()!!.compareTo(rhs.name.get()!!)
                    }
                    if (comparison == 0) {
                        comparison = Integer.valueOf(lhs.extensionId.get()).compareTo(rhs.extensionId.get())
                    }
                    comparison
                }
            }

            CardSortOrder.ByNumber -> {
                comparator = Comparator { lhs, rhs ->
                    var comparison = lhs.cardNumber.get()!!.compareTo(rhs.cardNumber.get()!!)
                    if (comparison == 0) {
                        comparison = Integer.valueOf(lhs.extensionId.get()).compareTo(rhs.extensionId.get())
                    }
                    comparison
                }
            }

            else -> comparator = null
        }

        if (comparator != null) {
            java.util.Collections.sort(cards, comparator)
        }
    }

    private val piles: Array<IntArray>
        get() {
            return this.activityService.getParameter("com.instriker.wcre.Piles", arrayOf())
        }

    //@SuppressLint("UseSparseArrays")	// < 1000 values
    private fun getCardsById(ids: Array<IntArray>): HashMap<Int, Card> {

        val idsFlattenetd = ArrayList<Int>()
        for (currentPile in ids) {
            for (cardId in currentPile) {
                idsFlattenetd.add(cardId)
            }
        }

        val cardsWanted = this.gameContentService.getCards(idsFlattenetd.toIntArray()).toList()
        val cardsWantedById = HashMap<Int, Card>()
        for (c in cardsWanted) {
            cardsWantedById.put(c.id, c)
        }
        return cardsWantedById
    }

    @JvmOverloads fun sort(sortMode: CardSortOrder = CardSortOrder.AsRulebook) {
        if (!this._sorting) {
            StartSortingPiles(sortMode)
        }
    }

    fun showCardDetails(index: Int?): Any {
        displayingViewIndex.set(index!!)
        displayCardDetails.set(true)
        return Unit
    }

    override fun onBackPressed(): Boolean {
        if (displayCardDetails.get()) {
            displayCardDetails.set(false)
            return false
        }
        return super.onBackPressed()
    }

    override fun onSaveInstanceState(savedInstanceState: IStateStore) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("com.instriker.wcre.presentation.GeneratedPileListViewModel.currentSortOrder", _currentSortOrder!!.ordinal)
    }

    override fun onRestoreInstanceState(savedInstanceState: IStateStore) {
        super.onRestoreInstanceState(savedInstanceState)
        StartSortingPiles(CardSortOrder.values()[savedInstanceState.getInt("com.instriker.wcre.presentation.GeneratedPileListViewModel.currentSortOrder", defaultCardSortOrder.ordinal)])
    }
}
