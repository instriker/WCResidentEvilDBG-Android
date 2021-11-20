package com.instriker.wcre.presentation

import android.app.Activity
import android.widget.ViewFlipper

import com.instriker.wcre.R
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel

class GameTrackerViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val useTurnCounter = BindingsFactory.bindBoolean(false)
    val isMultiPlayer = BindingsFactory.bindBoolean(false)
    private val displayPlayerDetailOnStart = BindingsFactory.bindBoolean(false)

    val turnCounter = BindingsFactory.bind<TurnCounterViewModel>()
    val player1 = BindingsFactory.bind<PlayerStatusViewModel>()
    val player2 = BindingsFactory.bind<PlayerStatusViewModel>()
    val player3 = BindingsFactory.bind<PlayerStatusViewModel>()
    val player4 = BindingsFactory.bind<PlayerStatusViewModel>()
    val player1Partner = BindingsFactory.bind<PlayerStatusViewModel>()
    val player2Partner = BindingsFactory.bind<PlayerStatusViewModel>()
    val player3Partner = BindingsFactory.bind<PlayerStatusViewModel>()
    val player4Partner = BindingsFactory.bind<PlayerStatusViewModel>()

    init {
        useTurnCounter.set(gameTrackerService.useTurnCounter)
        turnCounter.set(TurnCounterViewModel(gameTrackerService))

        val players = Array(4) { i ->
            PlayerStatusViewModel(i + 1, false, serviceLocator)
        }

        val partners =  Array(4) { i ->
            PlayerStatusViewModel(i + 1, true, serviceLocator)
        }

        player1.set(players[0])
        player2.set(players[1])
        player3.set(players[2])
        player4.set(players[3])
        player1Partner.set(partners[0])
        player2Partner.set(partners[1])
        player3Partner.set(partners[2])
        player4Partner.set(partners[3])
        
        val playerCount = players.sumBy{ IsPlaying(it) }

        isMultiPlayer.set(playerCount > 1)
    }

    fun onBinded(): Any {
        displayPlayerDetailOnStart.set(true)
        return Unit
    }

    override fun onStart() {
        if (displayPlayerDetailOnStart.get()) {
            if (IsPlaying(player1.get()!!) > 0) {
                showPlayerDetail(1, false)
            } else if (IsPlaying(player2.get()!!) > 0) {
                showPlayerDetail(2, false)
            } else if (IsPlaying(player3.get()!!) > 0) {
                showPlayerDetail(3, false)
            } else if (IsPlaying(player4.get()!!) > 0) {
                showPlayerDetail(4, false)
            }
        }
    }

    private fun IsPlaying(vm: PlayerStatusViewModel): Int {
        return if (vm.hasPlayer.get()) 1 else 0
    }

    fun showPlayerDetail(playerNumber: Int, isPartner: Boolean) {
        // TODO : Refactor : controls inside VM
        // binding:displayedChild (but custom...?, because it's indexed base)
        // We might be able to do better now we are using the native DataBinding library...
        val viewFlipper = viewFlipper
        viewFlipper.displayedChild = getControlIndex(viewFlipper, getHealthControlId(playerNumber, isPartner))
    }

    private fun getControlIndex(viewFlipper: ViewFlipper, id: Int): Int {
        return viewFlipper.indexOfChild(viewFlipper.findViewById(id))
    }

    private fun getHealthControlId(playerNumber: Int, isPartner: Boolean): Int {
        if (isPartner) {
            when (playerNumber) {
                1 -> return R.id.ctlPlayer1PartnerHealthTracker
                2 -> return R.id.ctlPlayer2PartnerHealthTracker
                3 -> return R.id.ctlPlayer3PartnerHealthTracker
                4 -> return R.id.ctlPlayer4PartnerHealthTracker
            }
        } else {
            when (playerNumber) {
                1 -> return R.id.ctlPlayer1HealthTracker
                2 -> return R.id.ctlPlayer2HealthTracker
                3 -> return R.id.ctlPlayer3HealthTracker
                4 -> return R.id.ctlPlayer4HealthTracker
            }
        }
        return 0
    }

    override fun onBackPressed(): Boolean {
        val viewFlipper = viewFlipper
        val ended = viewFlipper.displayedChild == 0 || getControlIndex(viewFlipper, getHealthControlId(1, false)) == 0    // If no summaries (tablet)
        if (!ended) {
            viewFlipper.displayedChild = 0
        }
        return ended
    }

    private val viewFlipper: ViewFlipper
        get() {
            val cheat = serviceLocator.context as Activity
            return cheat.findViewById(R.id.gameTrackerCurrentView) as ViewFlipper
        }
}
