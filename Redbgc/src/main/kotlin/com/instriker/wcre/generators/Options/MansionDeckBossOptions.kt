package com.instriker.wcre.generators.Options

class MansionDeckBossOptions {
    var isRandom: Boolean = false
        private set
    var fromExtensionId: Int = 0
        set(id) {
            field = id
            isRandom = false
        }

    init {
        isRandom = true
    }

    fun setIsRandom() {
        isRandom = true
    }
}