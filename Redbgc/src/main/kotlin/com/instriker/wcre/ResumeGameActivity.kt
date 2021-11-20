package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.ResumeGameViewModel
import com.instriker.wcre.presentation.ServiceLocator

class ResumeGameActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_resumegame

    override fun createViewModel(): RedbgcViewModel {
        return ResumeGameViewModel(ServiceLocator(this))
    }
}
