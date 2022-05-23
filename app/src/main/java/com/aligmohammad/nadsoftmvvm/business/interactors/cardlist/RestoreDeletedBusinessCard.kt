package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState.BusinessCardPendingDelete
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RestoreDeletedBusinessCard(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource
) {

    fun restoreDeletedBusinessCard(
        businesscard: BusinessCard,
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardListViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            businesscardCacheDataSource.insertBusinessCard(businesscard)
        }

        val response = object : CacheResponseHandler<BusinessCardListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Long): DataState<BusinessCardListViewState>? {
                return if (resultObj > 0) {
                    val viewState =
                        BusinessCardListViewState(
                            businesscardPendingDelete = BusinessCardPendingDelete(
                                businesscard = businesscard
                            )
                        )
                    DataState.data(
                        response = Response(
                            message = RESTORE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            message = RESTORE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(response)

        updateNetwork(response?.stateMessage?.response?.message, businesscard)
    }

    private suspend fun updateNetwork(response: String?, businesscard: BusinessCard) {
        if (response.equals(RESTORE_NOTE_SUCCESS)) {

            // insert into "businesscards" node
            safeApiCall(IO) {
                businesscardNetworkDataSource.insertOrUpdateBusinessCard(businesscard)
            }

            // remove from "deleted" node
            safeApiCall(IO) {
                businesscardNetworkDataSource.deleteDeletedBusinessCard(businesscard)
            }
        }
    }

    companion object {

        val RESTORE_NOTE_SUCCESS = "Successfully restored the deleted businesscard."
        val RESTORE_NOTE_FAILED = "Failed to restore the deleted businesscard."

    }
}













