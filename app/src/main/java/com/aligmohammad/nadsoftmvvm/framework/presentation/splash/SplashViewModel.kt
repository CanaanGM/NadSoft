package com.aligmohammad.nadsoftmvvm.framework.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashViewModel
@Inject
constructor(
    private val businessCardNetworkSyncManager: BusinessCardNetworkSyncManager
): ViewModel(){

    init {
        syncCacheWithNetwork()
    }

    fun hasSyncBeenExecuted() = businessCardNetworkSyncManager.hasSyncBeenExecuted

    private fun syncCacheWithNetwork(){
        businessCardNetworkSyncManager.executeDataSync(viewModelScope)
    }

}
















