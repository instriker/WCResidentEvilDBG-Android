package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

import java.util.ArrayList

import com.instriker.wcre.config.Constants
import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.gametracking.Character

class PickPlayerCardViewModel(serviceLocator: ServiceLocator, characters: ArrayList<CardViewModel>, playerNumber: Int, partnerEnabled: Boolean) : RedbgcViewModel(serviceLocator) {
    val playerNumber = BindingsFactory.bindInteger()
    val choosen = BindingsFactory.bind<CardViewModel?>(null)
    val choosenIndex = BindingsFactory.bindInteger()
    val choosenPartner = BindingsFactory.bind<CardViewModel?>(null)
    val choosenPartnerIndex = BindingsFactory.bindInteger()
    val partnerEnabled = BindingsFactory.bindBoolean()
    val characters = BindingsFactory.bindCollection<CardViewModel>()

    init {

        ensurePartnerConsistency()
        chooseCharacterOnChange()
        choosePartnerOnChange()

        Bindings.addToCollection(this.characters, CardViewModel.None)
        Bindings.addToCollection(this.characters, characters)
        this.playerNumber.set(playerNumber)
        this.partnerEnabled.set(partnerEnabled)
    }

    fun setCharacters(characters: ArrayList<CardViewModel>) {
        Bindings.clearCollection(this.characters)
        Bindings.addToCollection(this.characters, CardViewModel.None)
        Bindings.addToCollection(this.characters, characters)
    }

    fun setPartnerEnabled(isEnabled: Boolean) {
        partnerEnabled.set(isEnabled)
    }

    private fun chooseCharacterOnChange() {
        this.choosen.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val selected = choosen.get()
                val hasSelection = selected != null && selected !== CardViewModel.None

                if (hasSelection) {
                    if (playerNumber.get() > Constants.GameTrackerMaxPlayerCount) {
                        choosenIndex.set(0)
                    } else {
                        setCharacter(selected!!)
                    }
                } else {
                    removePlayer()
                }
            }
        })
    }

    private fun choosePartnerOnChange() {
        this.choosenPartner.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val selected = choosenPartner.get()
                if (selected != null && selected !== CardViewModel.None) {
                    setPartner(selected)
                } else {
                    removePartner()
                }
            }
        })
    }

    private fun ensurePartnerConsistency() {
        this.partnerEnabled.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val enabled = partnerEnabled.get()
                if (!enabled) {
                    // SelectedItem is OneWay, so use Index to clear
                    choosenPartnerIndex.set(0)
                }
            }
        })
    }

    private fun setCharacter(choosen: CardViewModel) {
        val choosenPlayer = Character(choosen.id.get(), choosen.name.get() ?: "", choosen.health.get() ?: 0, choosen.health.get() ?: 0, 0, intArrayOf(0, 0, 0))
        this.gameTrackerService.setCharacter(this.playerNumber.get(), choosenPlayer, false)
    }

    private fun removePlayer() {
        this.gameTrackerService.setCharacter(this.playerNumber.get(), null, false)
    }

    private fun setPartner(choosen: CardViewModel) {
        val choosenPartner = Character(choosen.id.get(), choosen.name.get() ?: "", choosen.health.get() ?: 0, choosen.health.get() ?: 0, 0, intArrayOf(0, 0, 0))
        this.gameTrackerService.setCharacter(this.playerNumber.get(), choosenPartner, true)
    }

    private fun removePartner() {
        this.gameTrackerService.setCharacter(this.playerNumber.get(), null, true)
    }
}