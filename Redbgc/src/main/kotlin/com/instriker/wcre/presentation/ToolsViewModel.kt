package com.instriker.wcre.presentation

import com.instriker.wcre.R
import java.util.HashMap

import com.instriker.wcre.framework.RedbgcViewModel

class ToolsViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    fun showAllCards() {
        val titles = HashMap<Int, String>()
        titles.put(0, this.resourceServices.getString(R.string.allCards))
        this.activityService.navigateToGeneratedCardList(this.gameContentService.cards, false, titles, true)
    }

    fun showBggSite() {
        val webSite = this.resourceServices.getString(R.string.bggSiteLink)
        this.activityService.showBrowser(webSite)
    }

    fun showWebSiteCompanion() {
        val webSite = this.resourceServices.getString(R.string.companionFacebookSiteLink)
        this.activityService.showBrowser(webSite)
    }

    fun showFAQ() {
        val faqs = this.resourceServices.getString(R.string.gameFaqLink)
        this.activityService.showBrowser(faqs)
    }

    private fun showRules(extensionId: Int) {
        val rules = this.resourceServices.getStringArray(R.array.gameRulesLinks)
        this.activityService.showBrowser(rules[extensionId - 1])
    }

    fun premierRules() {
        showRules(1)
    }

    fun allianceRules() {
        showRules(2)
    }

    fun outbreakRules() {
        showRules(3)
    }

    fun nightmareRules() {
        showRules(4)
    }

    fun mercenariesRules() {
        showRules(5)
    }
}
