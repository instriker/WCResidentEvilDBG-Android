package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.ExcludedResourceAreaCardsViewModel
import com.instriker.wcre.presentation.ServiceLocator

class ExcludedResourceAreaCardsActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_excludedcards

    override fun createViewModel(): RedbgcViewModel {
        return ExcludedResourceAreaCardsViewModel(ServiceLocator(this))
    }

}
