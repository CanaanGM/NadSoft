package com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardDetailViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateBusinessCard(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource
) {

    fun updateBusinessCard(
        businesscard: BusinessCard,
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardDetailViewState>?> = flow {

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            businesscardCacheDataSource.updateBusinessCard(
                primaryKey = businesscard.id,
                newTitle = businesscard.title,
                newBody = businesscard.body,
                timestamp = null // generate new timestamp
            )
        }

        val response = object : CacheResponseHandler<BusinessCardDetailViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<BusinessCardDetailViewState>? {
                return if (resultObj > 0) {
                    DataState.data(
                        response = Response(
                            message = UPDATE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            message = UPDATE_FAILED,
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
        if (response.equals(UPDATE_SUCCESS)) {

            safeApiCall(Dispatchers.IO) {
                businesscardNetworkDataSource.insertOrUpdateBusinessCard(businesscard)
            }
        }
    }

    companion object {
        val UPDATE_SUCCESS = "Successfully updated businesscard."
        val UPDATE_FAILED = "Failed to update businesscard."
        val UPDATE_FAILED_PK = "Update failed. BusinessCard is missing primary key."

    }
}