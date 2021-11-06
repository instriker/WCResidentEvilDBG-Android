package com.instriker.wcre.generators

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import com.instriker.wcre.framework.Randomizer
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.generators.Options.RangeOptions
import com.instriker.wcre.generators.Options.ResourceAreaOptions
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.WeaponCategory
import com.instriker.wcre.services.GameContentService

class ResourceAreaGenerator(private val _gameContentService: GameContentService) {
    private var _standardMutualyExclusiveCards: Array<IntArray>? = null
    private var _alloweds = HashMap<CardType, Int>()

    @Throws(Exception::class)
    fun generate(extOptions: ExtensionOptions, options: ResourceAreaOptions): Array<Array<Card>> {
        val allCards = resourceAreaCards
        filterExtensions(allCards, extOptions.fromExtensions)
        filterExcludedCards(allCards, options.excludedCards)

        val allPiles = generatePiles(options, allCards)
        shufflePiles(allPiles)

        val results = ArrayList<Array<Card>>()

        _alloweds = initAllowedCards(allPiles, options)
        _standardMutualyExclusiveCards = this._gameContentService.standardMutualyExclusiveCard

        chooseCards(allPiles, results, getBasicResourcesSelector(options).choosePiles(allPiles), options)

        // Right now, there is no card that allow trash AND extra explore at the same time, and none of them exists as basic resources.
        if (options.mustTrash) {
            val choosen = RandomActionAllowTrashPileSelector(1).choosePiles(allPiles)
            chooseCards(allPiles, results, choosen, options)
        }

        if (options.mustExtraExplore) {
            val choosen = RandomAllowExtraExplorePileSelector(1).choosePiles(allPiles)
            chooseCards(allPiles, results, choosen, options)
        }
        completeMinimumCards(allPiles, results, options)
        completeCards(allPiles, results, options)

        return results.toTypedArray<Array<Card>>()
    }

    private fun initAllowedCards(allPiles: ArrayList<Array<Card>>, options: ResourceAreaOptions): HashMap<CardType, Int> {
        val alloweds = HashMap<CardType, Int>()
        alloweds.put(CardType.Action, Math.max(0, options.actionsCount.maximumItems))
        alloweds.put(CardType.Weapon, Math.max(0, options.weaponsCount.maximumItems))
        alloweds.put(CardType.Item, Math.max(0, options.itemsCount.maximumItems))
        alloweds.put(CardType.Ammunition, Math.max(0, options.ammunitionsCount.maximumItems))

        // ClearMaximums already met
        alloweds.keys
                .filter { alloweds.getValue(it) <= 0 }
                .forEach { clearCardsOfType(allPiles, it) }

        return alloweds
    }

    private fun completeMinimumCards(allPiles: ArrayList<Array<Card>>, results: ArrayList<Array<Card>>, options: ResourceAreaOptions) {
        completeMinimumCards(allPiles, results, options.actionsCount, CardType.Action, options)
        completeMinimumCards(allPiles, results, options.weaponsCount, CardType.Weapon, options)
        completeMinimumCards(allPiles, results, options.itemsCount, CardType.Item, options)
        completeMinimumCards(allPiles, results, options.ammunitionsCount, CardType.Ammunition, options)
    }

    private fun completeMinimumCards(allPiles: ArrayList<Array<Card>>, results: ArrayList<Array<Card>>, rangeOptions: RangeOptions, type: CardType,
                                     options: ResourceAreaOptions) {
        val minimum = rangeOptions.minimumItems
        val actual = getCountsOfType(results, type)
        var missing = minimum - actual

        var found = true
        while (missing > 0 && found) {
            val chosen = RandomPileSelector(missing, type).choosePiles(allPiles)
            missing -= chooseCards(allPiles, results, chosen, options)
            found = chosen != null && chosen.size > 0
        }
    }

    private fun getCountsOfType(results: ArrayList<Array<Card>>, type: CardType): Int {
        var found = 0
        for (current in results) {
            if (current[0].cardType == type) {
                found++
            }
        }
        return found
    }

    private fun completeCards(allPiles: ArrayList<Array<Card>>, results: ArrayList<Array<Card>>, options: ResourceAreaOptions) {
        val opt = options.pileCount
        var missing = Randomizer.getRandom(opt.minimumItems, opt.maximumItems) - results.size
        if (missing > 0) {
            var found: Boolean
            do {
                var successfullyAddedCount = 0
                found = false
                for (currentTypeToTry in getRandomTypesToPickByPriority(missing)) {
                    val choosen = RandomPileSelector(1, currentTypeToTry).choosePiles(allPiles)
                    successfullyAddedCount += chooseCards(allPiles, results, choosen, options)

                    found = choosen != null && choosen.size > 0
                    if (found) {
                        break
                    }
                }

                missing -= successfullyAddedCount
            } while (missing > 0 && found)
        }
    }

    private fun getRandomTypesToPickByPriority(pilesLefts: Int): ArrayList<CardType> {
        val types = ArrayList<CardType>()
        var done: Boolean
        do {
            val current = getRandomTypeToPickByPriority(types, pilesLefts)
            done = current == null
            if (!done) {
                types.add(current!!)
            }
        } while (!done)
        return types
    }

    private fun getRandomTypeToPickByPriority(toExclude: ArrayList<CardType>, pilesLefts: Int): CardType? {
        var totalLefts = 0
        for (type in _alloweds.keys) {
            if (toExclude.contains(type)) {
                continue
            }

            val currentAllowesCount = _alloweds.getValue(type)
            if (currentAllowesCount > 0) {
                totalLefts += Math.min(currentAllowesCount, pilesLefts)
            }
        }

        var wanted = Randomizer.getRandom(0, totalLefts - 1)

        for (type in _alloweds.keys) {
            if (toExclude.contains(type)) {
                continue
            }

            val currentAllowedCount = _alloweds.getValue(type)
            if (currentAllowedCount > 0) {
                wanted -= Math.min(currentAllowedCount, pilesLefts)
                if (wanted < 0) {
                    return type
                }
            }
        }

        return null
    }

    private fun clearCardsOfType(allCards: ArrayList<Array<Card>>, typeToRemove: CardType) {
        for (index in allCards.indices.reversed()) {
            val current = allCards[index]

            if (current[0].cardType == typeToRemove) {
                allCards.removeAt(index)
            }
        }
    }

    private fun filterExcludedCards(allCards: ArrayList<Card>, excludedCards: IntArray) {
        if (excludedCards.size == 0) {
            return
        }

        Arrays.sort(excludedCards)

        for (index in allCards.indices.reversed()) {
            val current = allCards[index]

            val position = Arrays.binarySearch(excludedCards, current.id)
            if (position >= 0) {
                allCards.removeAt(index)
            }
        }
    }

    private fun isSamePile(pileToCheck: Array<Card>, pileToCompare: Array<Card>): Boolean {
        for (cardToCheck in pileToCheck) {
            for (cardToCompare in pileToCompare) {
                if (isSameCard(cardToCheck, cardToCompare)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isSameCard(cardToCheck: Card, cardToCompare: Card): Boolean {
        val idA = getComparableId(cardToCheck.id)
        val idB = getComparableId(cardToCompare.id)
        return idA == idB
    }

    private fun isSameWeaponTypePile(pileToCheck: Array<Card>, pileToCompare: Array<Card>): Boolean {
        val ref = pileToCheck[0]

        for (cardToCompare in pileToCheck) {
            if (!isSameWeaponType(ref, cardToCompare))
                return false
        }

        for (cardToCompare in pileToCompare) {
            if (!isSameWeaponType(ref, cardToCompare))
                return false
        }

        return true
    }

    private fun isSameWeaponType(cardToCheck: Card, cardToCompare: Card): Boolean {
        return cardToCheck.cardType == CardType.Weapon && cardToCompare.cardType == CardType.Weapon
                && cardToCheck.weaponCategory == cardToCompare.weaponCategory
    }

    private fun getComparableId(id: Int): Int {
        for (ids in this._standardMutualyExclusiveCards!!) {
            for (exclusiveId in ids) {
                if (exclusiveId == id) {
                    return ids[0]
                }
            }
        }
        return id
    }

    private fun generatePiles(options: ResourceAreaOptions, allCards: ArrayList<Card>): ArrayList<Array<Card>> {
        val allPiles = ArrayList<Array<Card>>()

        if (options.allWeaponsByType) {
            val cardsByWeaponType = HashMap<WeaponCategory, ArrayList<Card>>()

            for (card in allCards) {
                if (card.cardType == CardType.Weapon) {
                    val weaponType = card.weaponCategory
                    if (weaponType != null) {
                        var cardsForCategory: ArrayList<Card>? = cardsByWeaponType[weaponType]
                        if (cardsForCategory == null) {
                            cardsForCategory = ArrayList<Card>()
                            cardsByWeaponType.put(weaponType, cardsForCategory)
                        }
                        cardsForCategory.add(card)
                    }
                }
            }

            for (weaponCategory in cardsByWeaponType.keys) {
                val pile = cardsByWeaponType.getValue(weaponCategory)
                allPiles.add(pile.toTypedArray<Card>())

                for (weaponCard in pile) {
                    allCards.remove(weaponCard)
                }
            }
        }

        if (options.useStandardPile) {
            val standardPiles = _gameContentService.standardPiles
            for (currentPile in standardPiles) {

                val pile = ArrayList<Card>(currentPile.size)
                for (cardId in currentPile) {
                    for (card in allCards) {
                        if (card.id == cardId) {
                            pile.add(card)
                            allCards.remove(card)
                            break
                        }
                    }
                }

                if (!pile.isEmpty()) {
                    allPiles.add(pile.toTypedArray<Card>())
                }
            }
        }

        for (card in allCards) {
            allPiles.add(arrayOf(card))
        }

        return allPiles
    }

    private val resourceAreaCards: ArrayList<Card>
        @Throws(Exception::class)
        get() {
            val allCards = ArrayList<Card>()
            val cards = _gameContentService.cards ?: throw Exception("Could not read game card infos.")

            for (card in cards) {
                if (isResourceAreaCard(card)) {
                    allCards.add(card)
                }
            }
            return allCards
        }

    private fun isResourceAreaCard(card: Card): Boolean {
        return card.cardType == CardType.Ammunition || card.cardType == CardType.Weapon || card.cardType == CardType.Item
                || card.cardType == CardType.Action
    }

    private fun filterExtensions(allCards: ArrayList<Card>, fromExtensions: Collection<Int>) {
        for (index in allCards.indices.reversed()) {
            val current = allCards[index]
            if (!fromExtensions.contains(current.extensionId)) {
                allCards.removeAt(index)
            }
        }
    }

    private fun shufflePiles(allCards: ArrayList<Array<Card>>) {
        val allCardsRandomized = ArrayList<Array<Card>>()
        while (allCards.size > 0) {
            val index = Randomizer.getRandom(1, allCards.size) - 1
            val choosen = allCards[index]
            allCardsRandomized.add(choosen)
            allCards.removeAt(index)
        }
        allCards.addAll(allCardsRandomized)
    }

    private fun chooseCards(allCards: ArrayList<Array<Card>>, results: ArrayList<Array<Card>>, toAdd: Array<Array<Card>>, options: ResourceAreaOptions): Int {
        var addedCount = 0
        for (current in toAdd) {

            var canAdd = true
            if (!options.allowDuplicatePiles) {
                for (pileToCompare in results) {
                    if (isSamePile(current, pileToCompare)) {
                        canAdd = false
                        break
                    }
                }
            }

            if (canAdd && options.pileWeaponsByType) {
                for (toCompareIdx in results.indices) {
                    val pileToCompare = results[toCompareIdx]
                    if (isSameWeaponTypePile(current, pileToCompare)) {
                        val combined = ArrayList<Card>()
                        combined.addAll(Arrays.asList(*pileToCompare))
                        combined.addAll(Arrays.asList(*current))

                        results[toCompareIdx] = combined.toTypedArray<Card>()

                        canAdd = false
                        break
                    }
                }
            }

            allCards.remove(current)

            if (canAdd) {
                results.add(current)
                addedCount++

                // Do not pick from types when we reached the maximum
                val chosenType = current[0].cardType
                val left = _alloweds.getOrDefault(chosenType, 0) - 1
                _alloweds.put(chosenType, left)

                if (left <= 0) {
                    clearCardsOfType(allCards, chosenType)
                }
            }
        }

        return addedCount
    }

    private fun getBasicResourcesSelector(options: ResourceAreaOptions): IPileSelector {
        if (options.useBasicResources) {
            return BasicResourcesSelector()
        } else {
            return EmptySelector()
        }
    }
}