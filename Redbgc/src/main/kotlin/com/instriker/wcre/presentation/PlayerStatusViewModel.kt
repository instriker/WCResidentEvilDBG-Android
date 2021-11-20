package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableList
import com.instriker.wcre.R
import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.gametracking.Character
import java.util.*

class PlayerStatusViewModel(playerNumber: Int, isPartner: Boolean, serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val playerNumber = BindingsFactory.bindInteger(1)
    val name = BindingsFactory.bindString()
    val maxHealth = BindingsFactory.bindInteger(0)
    val health = BindingsFactory.bindInteger(0)
    val hasPlayer = BindingsFactory.bindBoolean(false)
    val isPartner = BindingsFactory.bindBoolean(false)
    val isPartnerMode = BindingsFactory.bindBoolean(false)
    val healthPourcentage = BindingsFactory.bindInteger(0)

    val showXP = BindingsFactory.bindBoolean(false)
    val canLoseHealth = BindingsFactory.bindBoolean(false)
    val canGainHealth = BindingsFactory.bindBoolean(false)
    val canLoseMaxHealth = BindingsFactory.bindBoolean(false)
    val canGainMaxHealth = BindingsFactory.bindBoolean(false)
    val canRevive = BindingsFactory.bindBoolean(false)
    val healthProgressDrawable = BindingsFactory.bindInteger(R.drawable.progress_horizontal_full)

    val healthColor = BindingsFactory.bindInteger()
    val characterXP = BindingsFactory.bindInteger(0)
    val skillXP = BindingsFactory.bindCollection<Int>()
    val canLoseCharacterXP = BindingsFactory.bindBoolean(false)
    val canGainCharacterXP = BindingsFactory.bindBoolean(false)
    val canLoseSkillXP = BindingsFactory.bindCollection<Boolean>()
    val canGainSkillXP = BindingsFactory.bindCollection<Boolean>()

    private val _player: Character? = gameTrackerService.getCharacter(playerNumber, isPartner)

    init {

        this.isPartner.set(isPartner)
        this.isPartnerMode.set(gameTrackerService.partnerMode)

        val healthChanged = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val mHealth = maxHealth.get().toFloat()
                val cHealth = health.get().toFloat()
                var healthPct = if (mHealth == 0f) 0.0.toFloat() else cHealth / mHealth
                if (healthPct < 0) {
                    healthPct = 0f
                }
                if (healthPct > 1) {
                    healthPct = 1f
                }
                healthPourcentage.set((healthPct * 100).toInt())

                canLoseHealth.set(cHealth > 0)
                canGainHealth.set(cHealth < mHealth)

                canLoseMaxHealth.set(mHealth > 0)
                canGainMaxHealth.set(mHealth < MaxPlayerHealthLimit)

                canRevive.set(cHealth == 0f && mHealth > 0)

                if (cHealth <= 40) {
                    healthColor.set(getColor(R.color.healthDanger))
                    healthProgressDrawable.set(R.drawable.progress_horizontal_danger)
                } else if (cHealth <= 70) {
                    healthColor.set(getColor(R.color.healthWarning))
                    healthProgressDrawable.set(R.drawable.progress_horizontal_warning)
                } else {
                    healthColor.set(getColor(R.color.health_full))
                    healthProgressDrawable.set(R.drawable.progress_horizontal_full)
                }
            }
        }

        this.health.addOnPropertyChangedCallback(healthChanged)
        this.maxHealth.addOnPropertyChangedCallback(healthChanged)

        this.characterXP.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                updateXPCanChange()
            }
        })

        this.skillXP.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<Int>>() {
            override fun onChanged(integers: ObservableList<Int>) {
                updateXPCanChange()
            }

            override fun onItemRangeChanged(integers: ObservableList<Int>, i: Int, i1: Int) {
                updateXPCanChange()
            }

            override fun onItemRangeInserted(integers: ObservableList<Int>, i: Int, i1: Int) {
                updateXPCanChange()
            }

            override fun onItemRangeMoved(integers: ObservableList<Int>, i: Int, i1: Int, i2: Int) {
                updateXPCanChange()
            }

            override fun onItemRangeRemoved(integers: ObservableList<Int>, i: Int, i1: Int) {
                updateXPCanChange()
            }
        })

        this.showXP.set(gameTrackerService.trackSkills)

        hasPlayer.set(_player != null)
        if (hasPlayer.get()) {
            this.playerNumber.set(playerNumber)
            this.name.set(_player!!.name)
            this.maxHealth.set(_player.maxHealth)
            this.health.set(_player.health)

            this.characterXP.set(_player.characterXP)
            Bindings.addToCollection(this.skillXP, _player.skillsXP.toList())
        }
    }

    private fun updateXPCanChange() {
        val characterXP = this@PlayerStatusViewModel.characterXP.get()
        canLoseCharacterXP.set(characterXP > 0)
        canGainCharacterXP.set(characterXP < PlayerXPLimit)

        val skillCount = Bindings.getSize(skillXP)
        val canLoseSkills = ArrayList<Boolean>()
        val canGainSkills = ArrayList<Boolean>()

        for (i in 0..skillCount - 1) {
            val curSkillXP = Bindings.getItemAt(skillXP, i)
            canLoseSkills.add(curSkillXP > 0)
            canGainSkills.add(curSkillXP < PlayerXPLimit)
        }

        Bindings.setCollection(canLoseSkillXP, canLoseSkills)
        Bindings.setCollection(canGainSkillXP, canGainSkills)
    }

    private fun getColor(resourceId: Int): Int {
        return resourceServices.getColor(resourceId)
    }

    fun updateHealth(amount: Int) {
        var newHealth = health.get() + amount
        val currentMaxHealth = maxHealth.get()
        if (newHealth < 0) {
            newHealth = 0
        }
        if (newHealth > currentMaxHealth) {
            newHealth = currentMaxHealth
        }

        health.set(newHealth)
        persistHealth()
    }

    fun updateMaxHealth(amount: Int) {
        var newMaxHealth = maxHealth.get() + amount
        var currentHealth = health.get()

        if (newMaxHealth < 0) {
            newMaxHealth = 0
        }
        if (newMaxHealth > MaxPlayerHealthLimit) {
            newMaxHealth = MaxPlayerHealthLimit
        }

        if (currentHealth > newMaxHealth) {
            currentHealth = newMaxHealth
            health.set(currentHealth)
        }

        maxHealth.set(newMaxHealth)
        persistHealth()
    }

    fun revive() {
        var newMaxHealth = maxHealth.get() - 20

        if (newMaxHealth < 0) {
            newMaxHealth = 0
        }

        health.set(newMaxHealth)
        maxHealth.set(newMaxHealth)
        persistHealth()
    }

    fun updateCharacterXp(amount: Int) {
        var newCharacterXP = characterXP.get() + amount

        if (newCharacterXP < 0) {
            newCharacterXP = 0
        }

        if (newCharacterXP > PlayerXPLimit) {
            newCharacterXP = PlayerXPLimit
        }

        characterXP.set(newCharacterXP)
        persistXP()
    }

    fun updateSkillXp(skillIndex: Int, amount: Int) {
        var newSkillXP = Bindings.getItemAt(skillXP, skillIndex) + amount

        if (newSkillXP < 0) {
            newSkillXP = 0
        }
        if (newSkillXP > PlayerXPLimit) {
            newSkillXP = PlayerXPLimit
        }

        Bindings.replaceItemAt(skillXP, skillIndex, newSkillXP)
        persistXP()
    }

    private fun persistHealth() {
        _player!!.health = health.get()
        _player.maxHealth = maxHealth.get()
        gameTrackerService.setCharacter(this.playerNumber.get(), _player, isPartner.get())
    }

    private fun persistXP() {
        _player!!.characterXP = characterXP.get()
        _player.skillsXP = Bindings.toArray(skillXP).toIntArray()
        gameTrackerService.setCharacter(this.playerNumber.get(), _player, isPartner.get())
    }

    companion object {
        private val MaxPlayerHealthLimit = 300
        private val PlayerXPLimit = 200
    }
}