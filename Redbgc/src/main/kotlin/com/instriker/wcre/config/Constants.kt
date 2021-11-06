package com.instriker.wcre.config

import java.net.URL

object Constants {
    const val DEBUG = false
    // Dont' forget to update the admobdefault.xml for manual testing
    // The dynamic set of adUnitId do not work at the moment.
    private const val TestAdsEnabled = true

    // NOTE: ads Ids are also defined in
    // - admobdefault.xml
    // - AndroidManifest.xml
    private const val INTERSTITIAL_TEST_AD_UNIT_ID = "ca-app-pub-0000000000000000/0000000000"
    private const val BANNER_TEST_AD_UNIT_ID = "ca-app-pub-0000000000000000/0000000000"

    val DEBUG_FORCE_ADS = if (DEBUG) Constants.TestAdsEnabled else false

    const val PUBLISHER_ID = "pub-0000000000000000"
    val BANNER_AD_UNIT_ID = if (DEBUG_FORCE_ADS)
        BANNER_TEST_AD_UNIT_ID
    else
        "ca-app-pub-0000000000000000/0000000000"

    val INTERSTITIAL_AD_UNIT_ID = if (DEBUG_FORCE_ADS)
        INTERSTITIAL_TEST_AD_UNIT_ID
    else
        "ca-app-pub-0000000000000000/0000000000"

    val PrivacyUrl = URL("http://www.wisecompanion.com/privacy")

    const val RELEASE = !DEBUG
    val InterstitialAdsDelayInMs = (if (DEBUG_FORCE_ADS)
        1 * 60 * 1000
    else
        2 * 60 * 60 * 1000).toLong()
    const val ScenariosMaxPileCount = 30
    const val ScenariosMaxAllowedCardsByType = ScenariosMaxPileCount

    const val GameTrackerMaxPlayerCount = 4

    const val PackageName = "com.instriker.wcre"
}
