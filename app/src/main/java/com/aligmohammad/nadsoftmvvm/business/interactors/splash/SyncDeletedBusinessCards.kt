package com.aligmohammad.nadsoftmvvm.business.interactors.splash

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.network.ApiResponseHandler
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.DataState
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.util.printLogD
import kotlinx.coroutines.Dispatchers.IO

class SyncDeletedBusinessCards(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource
){

    suspend fun syncDeletedBusinessCards(){

        val apiResult = safeApiCall(IO){
            businesscardNetworkDataSource.getDeletedBusinessCards()
        }
        val response = object: ApiResponseHandler<List<BusinessCard>, List<BusinessCard>>(
            response = apiResult,
            stateEvent = null
        ){
            override suspend fun handleSuccess(resultObj: List<BusinessCard>): DataState<List<BusinessCard>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }

        val businesscards = response.getResult()?.data?: ArrayList()

        val cacheResult = safeCacheCall(IO){
            businesscardCacheDataSource.deleteBusinessCards(businesscards)
        }

        object: CacheResponseHandler<Int, Int>(
            response = cacheResult,
            stateEvent = null
        ){
            override suspend fun handleSuccess(resultObj: Int): DataState<Int>? {
                printLogD("SyncBusinessCards",
                    "num deleted businesscards: ${resultObj}")
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

    }


}
























