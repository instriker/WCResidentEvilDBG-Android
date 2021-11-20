package com.instriker.wcre

import androidx.viewpager.widget.ViewPager

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.R
import com.instriker.wcre.presentation.ManageMansionsViewModel
import com.instriker.wcre.presentation.ServiceLocator

class ManageMansionsActivity : RedbgcActivity() {
    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_managemansions

    override fun createViewModel(): RedbgcViewModel {
        return ManageMansionsViewModel(ServiceLocator(this))
    }

    override fun onInflated() {
        super.onInflated()

        val pager = this.findViewById(R.id.ctlMansionDetails) as ViewPager
        pager.offscreenPageLimit = 2
    }
}
