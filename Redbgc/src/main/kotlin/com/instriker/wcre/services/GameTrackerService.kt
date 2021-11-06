package com.instriker.wcre.services

import androidx.databinding.Observable
import androidx.databinding.ObservableField

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.gametracking.Character

class GameTrackerService(private val _settingsService: SettingsService) {

    private val _gameStatusChanged = BindingsFactory.bind<Any>()

    fun observeGameChanged(observer: (Any) -> Unit) {
        _gameStatusChanged.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                observer(_gameStatusChanged.get() ?: Any())
            }
        })
    }

    fun validGameExists(): Boolean? {
        var hasPlayers = false
        var hasMissingPartners = false
        val partnerMode = partnerMode

        for (i in 1..MaxPlayers) {
            val hasPlayer = _settingsService.getTrackedPlayer(i) != null
            val hasPartner = _settingsService.getTrackedPartner(i) != null

            if (hasPlayer) {
                hasPlayers = true
            }

            if (partnerMode && hasPartner != hasPlayer) {
                hasMissingPartners = true
            }
        }

        return hasPlayers && !hasMissingPartners
    }

    fun clear() {
        for (i in 1..MaxPlayers) {
            setPlayer(i, null)
            setPartner(i, null)
        }
        turnLeft = 15
    }

    fun setCharacter(playerNumber: Int, player: Character?, isPartner: Boolean) {
        if (isPartner) {
            setPartner(playerNumber, player)
        } else {
            setPlayer(playerNumber, player)
        }
    }

    fun getCharacter(playerNumber: Int, isPartner: Boolean): Character? {
        if (isPartner) {
            return getPartner(playerNumber)
        } else {
            return getPlayer(playerNumber)
        }
    }

    private fun setPlayer(playerNumber: Int, player: Character?) {
        _settingsService.setTrackedPlayer(playerNumber, player)
        _gameStatusChanged.set(Any())
    }

    private fun getPlayer(playerNumber: Int): Character? {
        return _settingsService.getTrackedPlayer(playerNumber)
    }

    private fun setPartner(playerNumber: Int, player: Character?) {
        _settingsService.setTrackedPartner(playerNumber, player)
        _gameStatusChanged.set(Any())
    }

    private fun getPartner(playerNumber: Int): Character? {
        return _settingsService.getTrackedPartner(playerNumber)
    }

    var useTurnCounter: Boolean
        get() = _settingsService.trackerUseTurnCounter
        set(useTurnCounters) {
            _settingsService.trackerUseTurnCounter = useTurnCounters
        }

    var trackSkills: Boolean
        get() = _settingsService.trackerTrackSkills
        set(value) {
            _settingsService.trackerTrackSkills = value
        }

    var turnLeft: Int
        get() = _settingsService.turnLeft
        set(turnLeft) {
            _settingsService.turnLeft = turnLeft
        }

    var partnerMode: Boolean
        get() = _settingsService.trackerPartnerMode
        set(value) {
            _settingsService.trackerPartnerMode = value
            _gameStatusChanged.set(Any())
        }

    companion object {
        private const val MaxPlayers = 4
    }
}
