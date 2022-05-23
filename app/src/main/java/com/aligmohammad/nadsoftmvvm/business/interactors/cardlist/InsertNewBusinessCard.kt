package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCardFactory
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class InsertNewBusinessCard(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource,
    private val businesscardFactory: BusinessCardFactory
){

    fun insertNewBusinessCard(
        id: String? = null,
        title: String,
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardListViewState>?> = flow {

        val newBusinessCard = businesscardFactory.createSingleBusinessCard(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            body = ""
        )
        val cacheResult = safeCacheCall(IO){
            businesscardCacheDataSource.insertBusinessCard(newBusinessCard)
        }

        val cacheResponse = object: CacheResponseHandler<BusinessCardListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override suspend fun handleSuccess(resultObj: Long): DataState<BusinessCardListViewState>? {
                return if(resultObj > 0){
                    val viewState =
                        BusinessCardListViewState(
                            newBusinessCard = newBusinessCard
                        )
                    DataState.data(
                        response = Response(
                            message = INSERT_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }
                else{
                    DataState.data(
                        response = Response(
                            message = INSERT_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

        updateNetwork(cacheResponse?.stateMessage?.response?.message, newBusinessCard)
    }

    private suspend fun updateNetwork(cacheResponse: String?, newBusinessCard: BusinessCard ){
        if(cacheResponse.equals(INSERT_NOTE_SUCCESS)){

            safeApiCall(IO){
                businesscardNetworkDataSource.insertOrUpdateBusinessCard(newBusinessCard)
            }
        }
    }

    companion object{
        val INSERT_NOTE_SUCCESS = "Successfully inserted new businesscard."
        val INSERT_NOTE_FAILED = "Failed to insert new businesscard."
    }
}