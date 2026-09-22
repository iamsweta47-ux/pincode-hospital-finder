package com.pincodehospitalfinder.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions

object AdConfig {
    const val GAME_ID = "800378834"
    const val BANNER_PLACEMENT_ID = "BP_Banner_Android"
    const val INTERSTITIAL_PLACEMENT_ID = "BP_Interstitial_Android"
    const val TEST_MODE = true // set to false before publishing
}

object AdManager {

    private var isInitialized = false
    private var isInterstitialLoaded = false

    fun initialize(context: Context) {
        if (isInitialized) return

        UnityAds.initialize(context, AdConfig.GAME_ID, AdConfig.TEST_MODE, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {
                isInitialized = true
                Log.d("AdManager", "Unity Ads initialized")
                loadInterstitial()
            }

            override fun onInitializationFailed(
                error: UnityAds.UnityAdsInitializationError?,
                message: String?
            ) {
                Log.e("AdManager", "Unity Ads init failed: $message")
            }
        })
    }

    fun loadInterstitial() {
        if (!isInitialized) return

        UnityAds.load(AdConfig.INTERSTITIAL_PLACEMENT_ID, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String?) {
                isInterstitialLoaded = true
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String?,
                error: UnityAds.UnityAdsLoadError?,
                message: String?
            ) {
                isInterstitialLoaded = false
                Log.e("AdManager", "Interstitial failed to load: $message")
            }
        })
    }

    fun showInterstitial(activity: Activity, onClosed: () -> Unit = {}) {
        if (!isInterstitialLoaded) {
            onClosed()
            return
        }

        UnityAds.show(activity, AdConfig.INTERSTITIAL_PLACEMENT_ID, UnityAdsShowOptions(), object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String?,
                error: UnityAds.UnityAdsShowError?,
                message: String?
            ) {
                onClosed()
            }

            override fun onUnityAdsShowStart(placementId: String?) {}

            override fun onUnityAdsShowClick(placementId: String?) {}

            override fun onUnityAdsShowComplete(
                placementId: String?,
                state: UnityAds.UnityAdsShowCompletionState?
            ) {
                isInterstitialLoaded = false
                loadInterstitial() // preload next one
                onClosed()
            }
        })
    }
}
