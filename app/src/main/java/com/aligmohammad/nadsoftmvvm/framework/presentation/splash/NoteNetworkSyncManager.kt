package com.aligmohammad.nadsoftmvvm.framework.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aligmohammad.nadsoftmvvm.business.interactors.splash.SyncDeletedBusinessCards
import com.aligmohammad.nadsoftmvvm.business.interactors.splash.SyncBusinessCards
import com.aligmohammad.nadsoftmvvm.util.printLogD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessCardNetworkSyncManager
@Inject
constructor(
    private val syncBusinessCards: SyncBusinessCards,
    private val syncDeletedBusinessCards: SyncDeletedBusinessCards
){

    private val _hasSyncBeenExecuted: MutableLiveData<Boolean> = MutableLiveData(false)

    val hasSyncBeenExecuted: LiveData<Boolean>
            get() = _hasSyncBeenExecuted

    fun executeDataSync(coroutineScope: CoroutineScope){
        if(_hasSyncBeenExecuted.value!!){
            return
        }

        val syncJob = coroutineScope.launch {
            val deletesJob = launch {
                printLogD("SyncBusinessCards",
                    "syncing deleted businesscards.")
                syncDeletedBusinessCards.syncDeletedBusinessCards()
            }
            deletesJob.join()

            launch {
                printLogD("SyncBusinessCards",
                    "syncing businesscards.")
                syncBusinessCards.syncBusinessCards()
            }
        }
        syncJob.invokeOnCompletion {
            CoroutineScope(Main).launch{
                _hasSyncBeenExecuted.value = true
            }
        }
    }

}





















