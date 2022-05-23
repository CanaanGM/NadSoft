package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteMultipleBusinessCards(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource
){

    // set true if an error occurs when deleting any of the businesscards from cache
    private var onDeleteError: Boolean = false

    /**
     * Logic:
     * 1. execute all the deletes and save result into an ArrayList<DataState<BusinessCardListViewState>>
     * 2a. If one of the results is a failure, emit an "error" response
     * 2b. If all success, emit success response
     * 3. Update network with businesscards that were successfully deleted
     */
    fun deleteBusinessCards(
        businesscards: List<BusinessCard>,
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardListViewState>?> = flow {

        val successfulDeletes: ArrayList<BusinessCard> = ArrayList() // businesscards that were successfully deleted
        for(businesscard in businesscards){
            val cacheResult = safeCacheCall(IO){
                businesscardCacheDataSource.deleteBusinessCard(businesscard.id)
            }

            val response = object: CacheResponseHandler<BusinessCardListViewState, Int>(
                response = cacheResult,
                stateEvent = stateEvent
            ){
                override suspend fun handleSuccess(resultObj: Int): DataState<BusinessCardListViewState>? {
                    if(resultObj < 0){ // if error
                        onDeleteError = true
                    }
                    else{
                        successfulDeletes.add(businesscard)
                    }
                    return null
                }
            }.getResult()

            // check for random errors
            if(response?.stateMessage?.response?.message
                    ?.contains(stateEvent.errorInfo()) == true){
                onDeleteError = true
            }

        }

        if(onDeleteError){
            emit(
                DataState.data<BusinessCardListViewState>(
                    response = Response(
                        message = DELETE_BUSINESS_CARDS_ERRORS,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Success()
                    ),
                    data = null,
                    stateEvent = stateEvent
                )
            )
        }
        else{
            emit(
                DataState.data<BusinessCardListViewState>(
                    response = Response(
                        message = DELETE_BUSINESS_CARDS_SUCCESS,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                    data = null,
                    stateEvent = stateEvent
                )
            )
        }

        updateNetwork(successfulDeletes)
    }

    private suspend fun updateNetwork(successfulDeletes: ArrayList<BusinessCard>){
        for (businesscard in successfulDeletes){

            // delete from "businesscards" node
            safeApiCall(IO){
                businesscardNetworkDataSource.deleteBusinessCard(businesscard.id)
            }

            // insert into "deletes" node
            safeApiCall(IO){
                businesscardNetworkDataSource.insertDeletedBusinessCard(businesscard)
            }
        }
    }

    companion object{
        val DELETE_BUSINESS_CARDS_SUCCESS = "Successfully deleted businesscards."
        val DELETE_BUSINESS_CARDS_ERRORS = "Not all the businesscards you selected were deleted. There was some errors."
        val DELETE_BUSINESS_CARDS_YOU_MUST_SELECT = "You haven't selected any businesscards to delete."
        val DELETE_BUSINESS_CARDS_ARE_YOU_SURE = "Are you sure you want to delete these?"
    }
}













