package com.instriker.wcre.presentation

interface IQuickRangeSetting {
    fun getChoiceIndex(minValue: Int, maxValue: Int): Int

    fun isCustomChoice(index: Int): Boolean

    fun getStandardRange(index: Int): RangeValue<Int>
}
