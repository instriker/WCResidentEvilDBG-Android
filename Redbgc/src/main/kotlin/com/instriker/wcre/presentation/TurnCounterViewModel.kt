package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.services.GameTrackerService

class TurnCounterViewModel(private val _gameTrackerService: GameTrackerService) {
    val turnsLeft = BindingsFactory.bindInteger(-1)
    val canDecreaseTurns = BindingsFactory.bindBoolean()
    val canIncreaseTurns = BindingsFactory.bindBoolean()

    init {

        this.turnsLeft.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val currentTurnsLeft = turnsLeft.get()
                canDecreaseTurns.set(currentTurnsLeft > 0)
                canIncreaseTurns.set(currentTurnsLeft < MaxTurnsCount)
            }
        })

        turnsLeft.set(_gameTrackerService.turnLeft)
    }

    fun updateTurns(amount: Int) {
        var newTurnsLeft = turnsLeft.get() + amount
        if (newTurnsLeft < 0) {
            newTurnsLeft = 0
        }

        if (newTurnsLeft > MaxTurnsCount) {
            newTurnsLeft = MaxTurnsCount
        }

        turnsLeft.set(newTurnsLeft)
        persistTurns()
    }

    private fun persistTurns() {
        _gameTrackerService.turnLeft = turnsLeft.get()
    }

    companion object {
        private val MaxTurnsCount = 99
    }
}