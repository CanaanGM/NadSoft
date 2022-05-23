package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import BusinessCardCacheDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchBusinessCards(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource
) {

    fun searchBusinessCards(
        query: String,
        filterAndOrder: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardListViewState>?> = flow {
        var updatedPage = page
        if (page <= 0) {
            updatedPage = 1
        }
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            businesscardCacheDataSource.searchBusinessCards(
                query = query,
                filterAndOrder = filterAndOrder,
                page = updatedPage
            )
        }

        val response = object : CacheResponseHandler<BusinessCardListViewState, List<BusinessCard>>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: List<BusinessCard>): DataState<BusinessCardListViewState>? {
                var message: String? =
                    SEARCH_NOTES_SUCCESS
                var uiComponentType: UIComponentType? = UIComponentType.None()
                if (resultObj.size == 0) {
                    message =
                        SEARCH_NOTES_NO_MATCHING_RESULTS
                    uiComponentType = UIComponentType.Toast()
                }
                return DataState.data(
                    response = Response(
                        message = message,
                        uiComponentType = uiComponentType as UIComponentType,
                        messageType = MessageType.Success()
                    ),
                    data = BusinessCardListViewState(
                        businesscardList = ArrayList(resultObj)
                    ),
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(response)
    }

    companion object {
        const val SEARCH_NOTES_SUCCESS = "Successfully retrieved list of businesscards."
        const val SEARCH_NOTES_NO_MATCHING_RESULTS = "There are no businesscards that match that query."
        val SEARCH_NOTES_FAILED = "Failed to retrieve the list of businesscards."

    }
}







