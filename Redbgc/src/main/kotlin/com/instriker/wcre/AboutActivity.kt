package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.AboutViewModel
import com.instriker.wcre.presentation.ServiceLocator

class AboutActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_about

    override fun createViewModel(): RedbgcViewModel {
        return AboutViewModel(ServiceLocator(this))
    }

    protected override val menu: Int?
        get() = com.instriker.wcre.R.menu.aboutmenu
}
