package com.instriker.wcre.presentation

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType

class MansionSummaryViewModel {
    var totalCount = BindingsFactory.bindInteger()
    var hasBoss = BindingsFactory.bindBoolean(true)
    var bossName = BindingsFactory.bindString()
    var eventCount = BindingsFactory.bindInteger()
    var itemCount = BindingsFactory.bindInteger()
    var tokenCount = BindingsFactory.bindInteger()
    var infectedCount = BindingsFactory.bindInteger()
    var avgInfectedDecorations = BindingsFactory.bindFloat()
    var avgInfectedDamage = BindingsFactory.bindFloat()
    var avgInfectedHealth = BindingsFactory.bindFloat()
    var infecteds = BindingsFactory.bindCollection<InfectedSummaryViewModel>()

    fun generateSummary(mansionCards: Array<Card>) {
        generateTotalCount(mansionCards)
        generateBossSummary(mansionCards)
        generateEventSummary(mansionCards)
        generateItemSummary(mansionCards)
        generateTokenSummary(mansionCards)
        generateInfectedsSummary(mansionCards)
        generateInfectedAverages(mansionCards)
    }

    private fun generateTotalCount(mansionCards: Array<Card>) {
        var count = 0
        for (current in mansionCards) {
            count += current.quantity
        }
        this.totalCount.set(count)
    }

    private fun generateInfectedAverages(mansionCards: Array<Card>) {
        var count = 0
        var totalDecorations = 0
        var totalDamage = 0
        var totalHealth = 0
        for (current in mansionCards) {
            if (current.cardType == CardType.Infected || current.cardType == CardType.InfectedBoss) {
                count += current.quantity
                totalDecorations += current.decorations!! * current.quantity
                totalDamage += current.damage!! * current.quantity
                totalHealth += current.health!! * current.quantity
            }
        }

        val avgDecorations = if (count == 0) 0f else totalDecorations.toFloat() / count
        val avgDamage = if (count == 0) 0f else totalDamage.toFloat() / count
        val avgHealth = if (count == 0) 0f else totalHealth.toFloat() / count

        this.avgInfectedDecorations.set(avgDecorations)
        this.avgInfectedDamage.set(avgDamage)
        this.avgInfectedHealth.set(avgHealth)
    }

    private fun generateInfectedsSummary(mansionCards: Array<Card>) {
        var count = 0
        for (current in mansionCards) {
            if (current.cardType == CardType.Infected) {
                count += current.quantity
            }
        }
        this.infectedCount.set(count)
    }

    private fun generateTokenSummary(mansionCards: Array<Card>) {
        var count = 0
        for (current in mansionCards) {
            if (current.cardType == CardType.Token) {
                count += current.quantity
            }
        }
        this.tokenCount.set(count)
    }

    private fun generateItemSummary(mansionCards: Array<Card>) {
        var count = 0
        for (current in mansionCards) {
            if (current.cardType == CardType.MansionItem) {
                count += current.quantity
            }
        }
        this.itemCount.set(count)
    }

    private fun generateEventSummary(mansionCards: Array<Card>) {
        var count = 0
        for (current in mansionCards) {
            if (current.cardType == CardType.Event) {
                count += current.quantity
            }
        }
        this.eventCount.set(count)
    }

    private fun generateBossSummary(mansionCards: Array<Card>) {
        var bossName = ""
        for (current in mansionCards) {
            if (current.cardType == CardType.InfectedBoss) {
                bossName = current.name
                break
            }
        }
        this.bossName.set(bossName)
        this.hasBoss.set(bossName.length > 0)
    }
}
