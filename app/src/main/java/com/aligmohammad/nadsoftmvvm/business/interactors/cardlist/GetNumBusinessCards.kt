package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import BusinessCardCacheDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNumBusinessCards(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource
){

    fun getNumBusinessCards(
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardListViewState>?> = flow {

        val cacheResult = safeCacheCall(IO){
            businesscardCacheDataSource.getNumBusinessCards()
        }
        val response =  object: CacheResponseHandler<BusinessCardListViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override suspend fun handleSuccess(resultObj: Int): DataState<BusinessCardListViewState>? {
                val viewState = BusinessCardListViewState(
                    numBusinessCardsInCache = resultObj
                )
                return DataState.data(
                    response = Response(
                        message = GET_NUM_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(response)
    }

    companion object{
        val GET_NUM_NOTES_SUCCESS = "Successfully retrieved the number of businesscards from the cache."
        val GET_NUM_NOTES_FAILED = "Failed to get the number of businesscards from the cache."
    }
}