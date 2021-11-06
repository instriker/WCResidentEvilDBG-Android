package com.instriker.wcre

import android.view.MenuItem

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.CardSortOrder
import com.instriker.wcre.presentation.ChooseCustomStartingInventoryCharacterViewModel
import com.instriker.wcre.presentation.ServiceLocator

class ChooseCustomStartingInventoryCharacterActivity : RedbgcActivity() {
    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_choosecustomstartinginventorycharacter

    override fun createViewModel(): RedbgcViewModel {
        return ChooseCustomStartingInventoryCharacterViewModel(ServiceLocator(this))
    }

    protected override val menu: Int?
        get() = com.instriker.wcre.R.menu.cardlistsingletypemenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val vm = this.viewModel as ChooseCustomStartingInventoryCharacterViewModel

        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            handled = true
            when (item.itemId) {
                com.instriker.wcre.R.id.menuSortCardsName -> vm.sort(CardSortOrder.ByName)

                com.instriker.wcre.R.id.menuSortCardsNumber -> vm.sort(CardSortOrder.ByNumber)

                else -> handled = false
            }
        }

        return handled
    }
}
