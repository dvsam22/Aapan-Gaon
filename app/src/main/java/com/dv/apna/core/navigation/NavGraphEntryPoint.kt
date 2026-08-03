package com.dv.apna.core.navigation

import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.config.RemoteConfigManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavGraphEntryPoint {
    fun remoteConfigManager(): RemoteConfigManager
    fun adManager(): AdManager
}
