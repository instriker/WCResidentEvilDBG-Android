package com.instriker.wcre.generators.Options

class MansionDeckOptions {
    val bossOptions = MansionDeckBossOptions()
    val itemsOptions = RangeOptions()
    val tokensOptions = RangeOptions()
    val eventsOptions = RangeOptions()
    val infectedOptions = MansionDeckInfectedOptions()
    var excludedCards: IntArray = IntArray(0)
}

