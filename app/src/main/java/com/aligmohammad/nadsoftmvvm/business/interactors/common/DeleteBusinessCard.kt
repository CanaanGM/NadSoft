package com.aligmohammad.nadsoftmvvm.business.interactors.common

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteBusinessCard<ViewState>(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource
) {

    fun deleteBusinessCard(
        businesscard: BusinessCard,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            businesscardCacheDataSource.deleteBusinessCard(businesscard.id)
        }

        val response = object : CacheResponseHandler<ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<ViewState>? {
                return if (resultObj > 0) {
                    DataState.data(
                        response = Response(
                            message = DELETE_BUSINESS_CARD_SUCCESS,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            message = DELETE_BUSINESS_CARD_FAILED,
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

        // update network
        if (response?.stateMessage?.response?.message.equals(DELETE_BUSINESS_CARD_SUCCESS)) {

            // delete from 'businesscards' node
            safeApiCall(IO) {
                businesscardNetworkDataSource.deleteBusinessCard(businesscard.id)
            }

            // insert into 'deletes' node
            safeApiCall(IO) {
                businesscardNetworkDataSource.insertDeletedBusinessCard(businesscard)
            }

        }
    }

    companion object {
        val DELETE_BUSINESS_CARD_SUCCESS = "Successfully deleted businesscard."
        val DELETE_BUSINESS_CARD_PENDING = "Delete pending..."
        val DELETE_BUSINESS_CARD_FAILED = "Failed to delete businesscard."
        val DELETE_ARE_YOU_SURE = "Are you sure you want to delete this?"
    }
}













