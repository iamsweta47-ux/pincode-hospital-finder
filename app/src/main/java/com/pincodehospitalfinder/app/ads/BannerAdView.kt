package com.pincodehospitalfinder.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

@Composable
fun BannerAdView() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            BannerView(
                context as android.app.Activity,
                AdConfig.BANNER_PLACEMENT_ID,
                UnityBannerSize(320, 50)
            ).apply {
                load()
            }
        }
    )
}
