package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.ExcludedMansionCardsViewModel
import com.instriker.wcre.presentation.ServiceLocator

class ExcludedMansionCardsActivity : RedbgcActivity() {
    override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_excludedcards

    override fun createViewModel(): RedbgcViewModel {
        return ExcludedMansionCardsViewModel(ServiceLocator(this))
    }

}
