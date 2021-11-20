package com.instriker.wcre.presentation

import com.instriker.wcre.R
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel

class ResumeGameViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val canResumeGame = BindingsFactory.bindBoolean(false)

    init {
        init()
    }

    private fun init() {
        canResumeGame.set(this.gameTrackerService.validGameExists()!!)
    }

    fun resumeGame() {
        activityService.navigateToGameTracker()
        activityService.finish()
    }

    fun newGame() {
        if (canResumeGame.get()) {
            activityService.showConfirmation(
                    resourceServices.getString(R.string.startNewGameTitle),
                    resourceServices.getString(R.string.startNewGameWarningText),
                    resourceServices.getString(R.string.startNewGameConfirm),
                    resourceServices.getString(R.string.startNewGameCancel)
            ) {
                startNewGame()
            }
        } else {
            startNewGame()
        }
    }

    private fun startNewGame() {
        gameTrackerService.clear()
        activityService.navigateToNewGameTrackerSetup()
        activityService.finish()
    }
}
