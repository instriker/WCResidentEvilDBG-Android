package com.instriker.wcre.generators.Options

class ResourceAreaOptions {
    val pileCount = RangeOptions()
    val weaponsCount = RangeOptions()
    val itemsCount = RangeOptions()
    val ammunitionsCount = RangeOptions()
    val actionsCount = RangeOptions()
    var useStandardPile = false
    var useBasicResources = false
    var allowDuplicatePiles = false
    var allWeaponsByType = false
    var pileWeaponsByType = false
    var excludedCards = IntArray(0)
    var mustTrash = false
    var mustExtraExplore = false
}