package com.instriker.wcre.services

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.instriker.wcre.*
import com.instriker.wcre.config.Constants
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.presentation.BuildMansionViewModel
import com.instriker.wcre.presentation.BuildResourceAreaViewModel
import com.instriker.wcre.repositories.Card
import java.util.*

class ActivityService(private val _currentActivity: Activity, private val _resourceService: ResourceServices, private val _settingsService: SettingsService, private val _packageService: PackageService) {

    private val _navigating = BindingsFactory.bind<Any>()

    fun observeNavigating(observer: () -> Unit) {
        _navigating.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                observer.invoke()
            }
        })
    }

    fun navigateToCreateScenario() {
        val showMansionIntent = Intent(_currentActivity, BuildResourceAreaActivity::class.java)
        showMansionIntent.putExtra("Mode", BuildResourceAreaViewModel.GenerateMode)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToPickRandomScenario() {
        val showMansionIntent = Intent(_currentActivity, BuildResourceAreaActivity::class.java)
        showMansionIntent.putExtra("Mode", BuildResourceAreaViewModel.RandomPickMode)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToViewAllScenarios() {
        val showMansionIntent = Intent(_currentActivity, ManageScenariosActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToViewAllMansions() {
        val showMansionIntent = Intent(_currentActivity, ManageMansionsActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToChooseCustomStartingInventoryCharacterActivity() {
        val showMansionIntent = Intent(_currentActivity, ChooseCustomStartingInventoryCharacterActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToCreateMansion() {
        val showMansionIntent = Intent(_currentActivity, BuildMansionActivity::class.java)
        showMansionIntent.putExtra("Mode", BuildMansionViewModel.GenerateMode)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToPickRandomMansion() {
        val showMansionIntent = Intent(_currentActivity, BuildMansionActivity::class.java)
        showMansionIntent.putExtra("Mode", BuildMansionViewModel.RandomPickMode)
        navigateToApplicationIntent(showMansionIntent)
    }

    private fun navigateToApplicationIntent(intent: Intent) {
        _navigating.set(null)
        _currentActivity.startActivity(intent)
    }

    private fun navigateToApplicationIntentForResult(intent: Intent, requestCode: Int) {
        _navigating.set(null)
        _currentActivity.startActivityForResult(intent, requestCode)
    }

    fun navigateToGameTracker() {
        val showMansionIntent = Intent(_currentActivity, GameTrackerActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToNewGameTrackerSetup() {
        val showMansionIntent = Intent(_currentActivity, NewGameTrackerSetupActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToResumeGameTracker() {
        val showMansionIntent = Intent(_currentActivity, ResumeGameActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToTools() {
        val showMansionIntent = Intent(_currentActivity, ToolsActivity::class.java)
        navigateToApplicationIntent(showMansionIntent)
    }

    fun navigateToAbout() {
        val intent = Intent(_currentActivity, AboutActivity::class.java)
        navigateToApplicationIntent(intent)
    }

    fun sendEmail(email: String, subject: String) {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        val title = _currentActivity.resources.getText(com.instriker.wcre.R.string.contactEmailIntentTitle).toString()

        try {
            _currentActivity.startActivity(Intent.createChooser(intent, title))
        } catch (ex: android.content.ActivityNotFoundException) {
            showMessage(_currentActivity.resources.getText(com.instriker.wcre.R.string.noEmailClientsInstalled).toString())
        }

    }

    fun showMessage(text: String) {
        Toast.makeText(_currentActivity, text, Toast.LENGTH_SHORT).show()
    }

    fun showDialog(title: String, text: String, closeButton: String, closeAction: (() -> Unit)?) {
        val builder = AlertDialog.Builder(_currentActivity)
        builder.setMessage(text).setTitle(title).setPositiveButton(closeButton) { _, _ ->
            closeAction?.invoke()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showConfirmation(title: String, text: String, confirmText: String, cancelText: String, confirmAction: () -> Unit) {
        val builder = AlertDialog.Builder(_currentActivity)
        builder.setMessage(text)
                .setTitle(title)
                .setPositiveButton(confirmText) { _, _ -> confirmAction.invoke() }
                .setNegativeButton(cancelText) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    fun navigateToSettings(showMansionSettings: Boolean, showResourceAreaSettings: Boolean) {
        val intent = Intent(_currentActivity, SettingsActivity::class.java)
        intent.putExtra("com.instriker.wcre.showMansionSettings", showMansionSettings)
        intent.putExtra("com.instriker.wcre.showResourceAreaSettings", showResourceAreaSettings)
        navigateToApplicationIntentForResult(intent, CHANGE_SETTINGS_REQUEST)

    }

    fun navigateToExcludedScenarioCardsSettings() {
        val intent = Intent(_currentActivity, ExcludedResourceAreaCardsActivity::class.java)
        navigateToApplicationIntentForResult(intent, CHANGE_SETTINGS_REQUEST)
    }

    fun navigateToExcludedMansionCardsSettings() {
        val intent = Intent(_currentActivity, ExcludedMansionCardsActivity::class.java)
        navigateToApplicationIntentForResult(intent, CHANGE_SETTINGS_REQUEST)
    }

    fun navigateToGeneratedCardList(mansionCards: Array<Card>, isOfficialPile: Boolean, useCardQuantity: Boolean) {
        navigateToGeneratedCardList(mansionCards, isOfficialPile, null, useCardQuantity)
    }

    fun navigateToGeneratedCardList(mansionCards: Array<Card>, isOfficialPile: Boolean, titles: HashMap<Int, String>?, useCardQuantity: Boolean) {
        val piles = mansionCards.map { card -> arrayOf(card) }.toTypedArray()

        navigateToGeneratedPileList(piles, isOfficialPile, titles, useCardQuantity)
    }

    fun navigateToGeneratedPileList(piles: Array<Array<Card>>, isOfficialPile: Boolean, useCardQuantity: Boolean) {
        navigateToGeneratedPileList(piles, isOfficialPile, null, useCardQuantity)
    }

    fun navigateToGeneratedPileList(piles: Array<Array<Card>>, isOfficialPile: Boolean, titles: HashMap<Int, String>?, useCardQuantity: Boolean) {
        val intent = Intent(_currentActivity, GeneratedPileListActivity::class.java)

        val cardIds = arrayOfNulls<IntArray>(piles.size)
        for (pileIdx in piles.indices) {
            val pile = piles[pileIdx]
            val currentPileIds = ArrayList<Int>()

            for (card in pile) {
                val maxQuantity = if (useCardQuantity)
                    card.quantity
                else
                    1

                val curCardId = card.id
                for (curQty in 0..maxQuantity - 1) {
                    currentPileIds.add(curCardId)
                }
            }

            cardIds[pileIdx] = currentPileIds.toIntArray()
        }

        intent.putExtra("com.instriker.wcre.Piles", cardIds)
        intent.putExtra("com.instriker.wcre.PilesTitles", titles)
        intent.putExtra("com.instriker.wcre.IsOfficialPiles", isOfficialPile)
        navigateToApplicationIntent(intent)
    }

    fun showBrowser(webSite: String) {
        val uri = Uri.parse(webSite)
        val intent = Intent(android.content.Intent.ACTION_VIEW, uri)

        try {
            _currentActivity.startActivity(intent)
        } catch (ex: android.content.ActivityNotFoundException) {
            showMessage(_currentActivity.resources.getText(com.instriker.wcre.R.string.noBrowserInstalled).toString())
        }

    }

    private val extras: Bundle?
        get() = this._currentActivity.intent.extras

    fun getParameterString(paramName: String, defaultValue: String): String {
        val bundle = extras ?: return defaultValue
        var value = bundle.getString(paramName)

        // defaultValue crash here... so do it manually
        if (value == null) {
            value = defaultValue
        }

        return value
    }

    fun getParameterBoolean(paramName: String, defaultValue: Boolean): Boolean {
        val bundle = extras ?: return defaultValue
        return bundle.getBoolean(paramName, defaultValue)
    }

    fun <T> getParameter(paramName: String, defaultValue: T): T {
        val bundle = extras ?: return defaultValue
        return bundle.get(paramName) as T ?: defaultValue
    }

    fun rateMyApplication() {
        // http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
        val packageName = this._currentActivity.applicationContext.packageName
        launchMarketPlace(packageName)
    }

    fun shareMyApplication() {
        var description = this._resourceService.getString(com.instriker.wcre.R.string.shareApplicationDescription)
        description = description.replace("{FacebookLink}", _resourceService.getString(com.instriker.wcre.R.string.companionFacebookSiteLink))

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, _resourceService.getString(com.instriker.wcre.R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, description)
        _currentActivity.startActivity(Intent.createChooser(intent, _resourceService.getString(com.instriker.wcre.R.string.shareApplicationTitle)))
    }

    private fun launchMarketPlace(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=" + packageName)
        try {
            _currentActivity.startActivity(intent)
        } catch (ex: Exception) {
            if (Constants.DEBUG) {
                Log.e("Intent", "Could not open Google Play.", ex)
            }
        }

    }

    fun setResult(resultCode: Int) {
        _currentActivity.setResult(resultCode)
    }

    fun setResultOK() {
        setResult(Activity.RESULT_OK)
    }

    fun setResultCanceled() {
        setResult(Activity.RESULT_CANCELED)
    }

    fun displayNewVersionInfoOnce() {
        val lastVersionSaw = _settingsService.lastVersionLaunched
        val currentVersion = _packageService.versionCode

        if (lastVersionSaw != currentVersion && currentVersion > 1) {
            val title = _resourceService.getString(com.instriker.wcre.R.string.newVersionTitle)
            val description = _resourceService.getString(com.instriker.wcre.R.string.newVersionDescription)
            val closeButton = _resourceService.getString(com.instriker.wcre.R.string.newVersionClose)
            showDialog(title, description, closeButton) {
                _settingsService.lastVersionLaunched = currentVersion
            }
        }
    }

    fun finish() {
        this._currentActivity.finish()
    }

    companion object {
        val CHANGE_SETTINGS_REQUEST = 1
    }

}