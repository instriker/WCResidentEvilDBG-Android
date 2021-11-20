package com.instriker.wcre.services

import java.text.ParseException
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import android.content.*
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus

import com.instriker.wcre.config.Constants
import com.instriker.wcre.gametracking.Character
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.generators.Options.MansionDeckOptions
import com.instriker.wcre.generators.Options.ResourceAreaOptions


class SettingsService(private val _currentActivity: Context) {

    var extensionOptions: ExtensionOptions
        get() {

            val settings = this._currentActivity.getSharedPreferences(ExtensionsOptionsPreferences, Context.MODE_PRIVATE)
            val count = settings.getInt("count", 0)
            val extensions = (0..count - 1).mapTo(ArrayList<Int>()) {
                settings.getInt("ex" + Integer.toString(it), 1)
            }

            if (extensions.isEmpty()) {
                extensions.add(1)
            }

            val extOptions = ExtensionOptions()
            extOptions.setFromExtensions(extensions)
            return extOptions
        }
        set(extensionOptions) {
            val settings = this._currentActivity.getSharedPreferences(ExtensionsOptionsPreferences, Context.MODE_PRIVATE)
            val editor = settings.edit()
            editor.putInt("count", extensionOptions.fromExtensions.size)

            for ((idx, current) in extensionOptions.fromExtensions.withIndex()) {
                editor.putInt("ex$idx", current)
            }

            editor.commit()
        }

    var mansionDeckOptions: MansionDeckOptions
        get() {
            val settings = this._currentActivity.getSharedPreferences(MansionOptionsPreferences, Context.MODE_PRIVATE)

            val options = MansionDeckOptions()

            options.bossOptions.fromExtensionId = settings.getInt("bossFromExtId", 0)
            if (settings.getBoolean("bossIsRandom", true)) {
                options.bossOptions.setIsRandom()
            }
            options.tokensOptions.minimumItems = settings.getInt("minimumTokens", 1)
            options.tokensOptions.maximumItems = settings.getInt("maximumTokens", 2)
            options.itemsOptions.minimumItems = settings.getInt("minimumItems", 0)
            options.itemsOptions.maximumItems = settings.getInt("maximumItems", 3)
            options.eventsOptions.minimumItems = settings.getInt("minimumEvents", 0)
            options.eventsOptions.maximumItems = settings.getInt("maximumEvents", 3)
            options.infectedOptions.totalCount.minimumItems = settings.getInt("minimumInfected", 32)
            options.infectedOptions.totalCount.maximumItems = settings.getInt("maximumInfected", 32)
            options.excludedCards = getIntArray(settings, "excludedCards", IntArray(0))

            return options
        }
        set(options) {
            val settings = this._currentActivity.getSharedPreferences(MansionOptionsPreferences, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putInt("bossFromExtId", options.bossOptions.fromExtensionId)
            editor.putBoolean("bossIsRandom", options.bossOptions.isRandom)
            editor.putInt("minimumTokens", options.tokensOptions.minimumItems)
            editor.putInt("maximumTokens", options.tokensOptions.maximumItems)
            editor.putInt("minimumItems", options.itemsOptions.minimumItems)
            editor.putInt("maximumItems", options.itemsOptions.maximumItems)
            editor.putInt("minimumEvents", options.eventsOptions.minimumItems)
            editor.putInt("maximumEvents", options.eventsOptions.maximumItems)
            editor.putInt("minimumInfected", options.infectedOptions.totalCount.minimumItems)
            editor.putInt("maximumInfected", options.infectedOptions.totalCount.maximumItems)
            putIntArray(editor, "excludedCards", options.excludedCards)

            editor.commit()
        }

    var resourceAreaOptions: ResourceAreaOptions
        get() {
            val settings = this._currentActivity.getSharedPreferences(ResourceAreaOptionsPreferences, Context.MODE_PRIVATE)

            val options = ResourceAreaOptions()
            options.pileCount.minimumItems = settings.getInt("minimumPiles", 18)
            options.pileCount.maximumItems = settings.getInt("maximumPiles", 18)

            options.ammunitionsCount.minimumItems = settings.getInt("minimumAmmunitions", 0)
            options.ammunitionsCount.maximumItems = settings.getInt("maximumAmmunitions", Constants.ScenariosMaxAllowedCardsByType)
            options.weaponsCount.minimumItems = settings.getInt("minimumWeapons", 0)
            options.weaponsCount.maximumItems = settings.getInt("maximumWeapons", Constants.ScenariosMaxAllowedCardsByType)
            options.actionsCount.minimumItems = settings.getInt("minimumActions", 0)
            options.actionsCount.maximumItems = settings.getInt("maximumActions", Constants.ScenariosMaxAllowedCardsByType)
            options.itemsCount.minimumItems = settings.getInt("minimumItems", 0)
            options.itemsCount.maximumItems = settings.getInt("maximumItems", Constants.ScenariosMaxAllowedCardsByType)

            options.useBasicResources = settings.getBoolean("useBasicResources", true)
            options.useStandardPile = settings.getBoolean("useStandardPile", true)
            options.allowDuplicatePiles = settings.getBoolean("allowDuplicatePiles", false)
            options.allWeaponsByType = settings.getBoolean("allWeaponsByType", false)
            options.pileWeaponsByType = settings.getBoolean("pileWeaponsByType", false)
            options.excludedCards = getIntArray(settings, "excludedCards", IntArray(0))
            options.mustTrash = settings.getBoolean("mustTrash", true)
            options.mustExtraExplore = settings.getBoolean("mustExtraExplore", true)

            return options
        }
        set(options) {
            val settings = this._currentActivity.getSharedPreferences(ResourceAreaOptionsPreferences, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putInt("minimumPiles", options.pileCount.minimumItems)
            editor.putInt("maximumPiles", options.pileCount.maximumItems)
            editor.putInt("minimumAmmunitions", options.ammunitionsCount.minimumItems)
            editor.putInt("maximumAmmunitions", options.ammunitionsCount.maximumItems)
            editor.putInt("minimumWeapons", options.weaponsCount.minimumItems)
            editor.putInt("maximumWeapons", options.weaponsCount.maximumItems)
            editor.putInt("minimumActions", options.actionsCount.minimumItems)
            editor.putInt("maximumActions", options.actionsCount.maximumItems)
            editor.putInt("minimumItems", options.itemsCount.minimumItems)
            editor.putInt("maximumItems", options.itemsCount.maximumItems)

            editor.putBoolean("useBasicResources", options.useBasicResources)
            editor.putBoolean("useStandardPile", options.useStandardPile)
            editor.putBoolean("allowDuplicatePiles", options.allowDuplicatePiles)
            editor.putBoolean("allWeaponsByType", options.allWeaponsByType)
            editor.putBoolean("pileWeaponsByType", options.pileWeaponsByType)
            putIntArray(editor, "excludedCards", options.excludedCards)
            editor.putBoolean("mustTrash", options.mustTrash)
            editor.putBoolean("mustExtraExplore", options.mustExtraExplore)

            editor.commit()
        }

    private fun getIntArray(settings: SharedPreferences, key: String, defaultValue: IntArray): IntArray {
        val length = settings.getInt(key + "_count", -1)
        if (length == -1) {
            return defaultValue
        }

        val result = IntArray(length)
        for (idx in 0..length - 1) {
            val idxKey = key + "_" + Integer.toString(idx)
            if (!settings.contains(idxKey)) {
                return defaultValue
            }

            result[idx] = settings.getInt(idxKey, -1)
        }

        return result
    }

    private fun putIntArray(editor: SharedPreferences.Editor, key: String, values: IntArray) {
        editor.putInt(key + "_count", values.size)

        var idx = 0
        for (current in values) {
            editor.putInt(key + "_" + Integer.toString(idx++), current)
        }
    }

    var lastVersionLaunched: Int
        get() {
            val settings = this._currentActivity.getSharedPreferences(VersionPreferences, Context.MODE_PRIVATE)
            val version = settings.getInt("lastVersionLaunched", -1)
            return version
        }
        set(version) {
            val settings = this._currentActivity.getSharedPreferences(VersionPreferences, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putInt("lastVersionLaunched", version)

            editor.commit()
        }

    fun setInterstitialShown() {
        val settings = this._currentActivity.getSharedPreferences(LastInterstitialPreferences, Context.MODE_PRIVATE)
        val editor = settings.edit()

        val now = Calendar.getInstance().time
        val formatted = java.text.DateFormat.getDateTimeInstance().format(now)

        editor.putString("lastInterstitialShown", formatted)

        editor.commit()
    }

    //java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    // If it's the first install, wait a little bit for interstitial
    val lastInterstitialShown: Date
        get() {
            val settings = this._currentActivity.getSharedPreferences(LastInterstitialPreferences, Context.MODE_PRIVATE)
            val formatted = settings.getString("lastInterstitialShown", "")
            if (formatted != null && formatted.length > 0) {
                try {
                    return java.text.DateFormat.getDateTimeInstance().parse(formatted)
                } catch (e: ParseException) {
                }

            }
            setInterstitialShown()
            return lastInterstitialShown
        }

    fun setTrackedPlayer(playerNumber: Int, player: Character?) {
        setTrackedCharacter(playerNumber, player, false)
    }

    fun setTrackedPartner(playerNumber: Int, player: Character?) {
        setTrackedCharacter(playerNumber, player, true)
    }

    fun setTrackedCharacter(playerNumber: Int, player: Character?, isPartner: Boolean) {
        var player = player
        if (playerNumber > Constants.GameTrackerMaxPlayerCount) {
            player = null
        }

        val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
        val editor = settings.edit()

        if (player == null) {
            editor.putInt(toPlayerKey("id", playerNumber, isPartner), NULL_PLAYER_ID)
        } else {

            editor.putInt(toPlayerKey("id", playerNumber, isPartner), player.characterId)
            editor.putString(toPlayerKey("name", playerNumber, isPartner), player.name)
            editor.putInt(toPlayerKey("heatlh", playerNumber, isPartner), player.health)
            editor.putInt(toPlayerKey("maxHeatlh", playerNumber, isPartner), player.maxHealth)
            editor.putInt(toPlayerKey("characterXP", playerNumber, isPartner), player.characterXP)
            putIntArray(editor, toPlayerKey("skillsXP", playerNumber, isPartner), player.skillsXP)
        }

        editor.commit()
    }

    fun getTrackedPlayer(playerNumber: Int): Character? {
        return getTrackedCharacter(playerNumber, false)
    }

    fun getTrackedPartner(playerNumber: Int): Character? {
        return getTrackedCharacter(playerNumber, true)
    }

    private fun getTrackedCharacter(playerNumber: Int, isPartner: Boolean): Character? {
        if (playerNumber > Constants.GameTrackerMaxPlayerCount) {
            return null
        }

        val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)

        val id = settings.getInt(toPlayerKey("id", playerNumber, isPartner), NULL_PLAYER_ID)
        if (id == NULL_PLAYER_ID) {
            return null
        }
        return Character(id,
                settings.getString(toPlayerKey("name", playerNumber, isPartner), "anonymous")!!,
                settings.getInt(toPlayerKey("heatlh", playerNumber, isPartner), 100),
                settings.getInt(toPlayerKey("maxHeatlh", playerNumber, isPartner), 100),
                settings.getInt(toPlayerKey("characterXP", playerNumber, isPartner), 0),
                getIntArray(settings, toPlayerKey("skillsXP", playerNumber, isPartner), intArrayOf(0, 0, 0))
        )
    }

    private fun toPlayerKey(key: String, playerNumber: Int, isPartner: Boolean): String {
        return "pl_" + playerNumber.toString() + (if (isPartner) "_Parter" else "") + "_" + key
    }

    var trackerUseTurnCounter: Boolean
        get() {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            return settings.getBoolean("useTurnCounters", false)
        }
        set(useTurnCounters) {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putBoolean("useTurnCounters", useTurnCounters)

            editor.commit()
        }

    var turnLeft: Int
        get() {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            return settings.getInt("turnLeft", 15)
        }
        set(turnLeft) {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putInt("turnLeft", turnLeft)

            editor.commit()
        }

    var trackerTrackSkills: Boolean
        get() {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            return settings.getBoolean("trackSkills", true)
        }
        set(useTurnCounters) {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putBoolean("trackSkills", useTurnCounters)

            editor.commit()
        }

    var trackerPartnerMode: Boolean
        get() {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            return settings.getBoolean("partnerMode", false)
        }
        set(usePartnerMode) {
            val settings = this._currentActivity.getSharedPreferences(GameTrackerPlayer, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putBoolean("partnerMode", usePartnerMode)

            editor.commit()
        }

    var noAds: Boolean
        get() {
            if (Constants.DEBUG_FORCE_ADS) {
                return false
            }
            val settings = this._currentActivity.getSharedPreferences(NoAds, Context.MODE_PRIVATE)
            return settings.getBoolean("enabled", false)
        }
        set(noAds) {
            val settings = this._currentActivity.getSharedPreferences(NoAds, Context.MODE_PRIVATE)
            val editor = settings.edit()

            editor.putBoolean("enabled", if (Constants.DEBUG_FORCE_ADS) false else noAds)

            editor.commit()
        }

    var consentStatus: ConsentStatus
        get() {
            return ConsentInformation.getInstance(_currentActivity).consentStatus
        }
        set(consentStatus) {
            ConsentInformation.getInstance(_currentActivity).consentStatus = consentStatus
        }

    companion object {
        private const val NULL_PLAYER_ID = -1
        private const val ExtensionsOptionsPreferences = "Extensions"
        private const val MansionOptionsPreferences = "MansionOptions"
        private const val ResourceAreaOptionsPreferences = "ResourceAreaOptions"
        private const val VersionPreferences = "Version"
        private const val LastInterstitialPreferences = "LastInterstitial"
        private const val GameTrackerPlayer = "GameTrackerPlayer"
        private const val NoAds = "NoAds"
        private const val AdsConsentStatus = "AdsConsentStatus"
    }
}
