package com.instriker.wcre

import androidx.viewpager.widget.ViewPager
import android.view.MenuItem

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.R
import com.instriker.wcre.presentation.ManageScenariosViewModel
import com.instriker.wcre.presentation.ServiceLocator
import com.instriker.wcre.repositories.GameMode

class ManageScenariosActivity : RedbgcActivity() {
    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_managescenarios

    protected override val menu: Int?
        get() = com.instriker.wcre.R.menu.managescenariosmenu

    override fun createViewModel(): RedbgcViewModel {
        return ManageScenariosViewModel(ServiceLocator(this))
    }

    override fun onInflated() {
        super.onInflated()

        val pager = this.findViewById(R.id.ctlScenariosDetails) as ViewPager
        pager.offscreenPageLimit = 2
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val vm = this.viewModel as ManageScenariosViewModel

        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            handled = true
            when (item.itemId) {
                com.instriker.wcre.R.id.menuSearchScenarios -> {
                    this.onSearchRequested()
                }

                com.instriker.wcre.R.id.menuFilterGameModeAny -> {
                    vm.filterGameMode()
                }

                com.instriker.wcre.R.id.menuFilterGameModeStory -> vm.filterGameMode(GameMode.Story)

                com.instriker.wcre.R.id.menuFilterGameModeMercenary -> vm.filterGameMode(GameMode.Mercenary)

                com.instriker.wcre.R.id.menuFilterGameModeOutbreak -> vm.filterGameMode(GameMode.Outbreak)

                com.instriker.wcre.R.id.menuFilterGameModeVs -> vm.filterGameMode(GameMode.Versus)

                com.instriker.wcre.R.id.menuFilterGameModeSkillSystem -> vm.filterSkillSystem()

                else -> handled = false
            }
        }

        return handled
    }

}
