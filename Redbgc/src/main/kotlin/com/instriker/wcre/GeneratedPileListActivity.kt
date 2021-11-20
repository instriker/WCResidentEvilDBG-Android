package com.instriker.wcre

import androidx.viewpager.widget.ViewPager
import android.view.MenuItem

import com.instriker.wcre.framework.*
import com.instriker.wcre.R
import com.instriker.wcre.presentation.*

class GeneratedPileListActivity : RedbgcActivity() {
    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_generatedpilelist

    override fun createViewModel(): RedbgcViewModel {
        return GeneratedPileListViewModel(ServiceLocator(this))
    }

    override fun onInflated() {
        super.onInflated()

        val pager = this.findViewById(R.id.ctlCardDetails) as ViewPager
        pager.offscreenPageLimit = 2
    }

    protected override val menu: Int?
        get() {
            val vm = this.viewModel as GeneratedPileListViewModel

            if (vm.isOfficialCards) {
                return com.instriker.wcre.R.menu.cardlistmenu
            } else {
                return com.instriker.wcre.R.menu.generatedcardlistmenu
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val vm = this.viewModel as GeneratedPileListViewModel

        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            handled = true
            when (item.itemId) {
                com.instriker.wcre.R.id.menuSortCardsOfficial -> {
                    vm.sort(CardSortOrder.AsRulebook)
                }

                com.instriker.wcre.R.id.menuSortCardsType -> vm.sort(CardSortOrder.ByType)

                com.instriker.wcre.R.id.menuSortCardsName -> vm.sort(CardSortOrder.ByName)

                com.instriker.wcre.R.id.menuSortCardsNumber -> vm.sort(CardSortOrder.ByNumber)

                else -> handled = false
            }
        }

        return handled
    }
}