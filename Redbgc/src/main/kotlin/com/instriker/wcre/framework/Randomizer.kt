package com.instriker.wcre.framework

object Randomizer {
    fun getRandom(minimumValue: Int, maximumValue: Int): Int {
        return minimumValue + Math.floor(Math.random() * (maximumValue - minimumValue + 1)).toInt()
    }
}
