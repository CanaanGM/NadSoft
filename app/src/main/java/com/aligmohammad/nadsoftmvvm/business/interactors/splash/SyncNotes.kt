package com.aligmohammad.nadsoftmvvm.business.interactors.splash

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.data.cache.CacheResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.network.ApiResponseHandler
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.DataState
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.util.printLogD
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

@Suppress("IMPLICIT_CAST_TO_ANY")
class SyncBusinessCards(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource,
    private val dateUtil: DateUtil
) {

    suspend fun syncBusinessCards() {

        val cachedBusinessCardsList = getCachedBusinessCards()

        val networkBusinessCardsList = getNetworkBusinessCards()

        syncNetworkBusinessCardsWithCachedBusinessCards(
            ArrayList(cachedBusinessCardsList),
            networkBusinessCardsList
        )
    }

    private suspend fun getCachedBusinessCards(): List<BusinessCard> {
        val cacheResult = safeCacheCall(IO) {
            businesscardCacheDataSource.getAllBusinessCards()
        }

        val response = object : CacheResponseHandler<List<BusinessCard>, List<BusinessCard>>(
            response = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<BusinessCard>): DataState<List<BusinessCard>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }

        }.getResult()

        return response?.data ?: ArrayList()
    }

    private suspend fun getNetworkBusinessCards(): List<BusinessCard> {
        val networkResult = safeApiCall(IO) {
            businesscardNetworkDataSource.getAllBusinessCards()
        }

        val response = object : ApiResponseHandler<List<BusinessCard>, List<BusinessCard>>(
            response = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<BusinessCard>): DataState<List<BusinessCard>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()
    }

    // get all businesscards from network
    // if they do not exist in cache, insert them
    // if they do exist in cache, make sure they are up to date
    // while looping, remove businesscards from the cachedBusinessCards list. If any remain, it means they
    // should be in the network but aren't. So insert them.
    private suspend fun syncNetworkBusinessCardsWithCachedBusinessCards(
        cachedBusinessCards: ArrayList<BusinessCard>,
        networkBusinessCards: List<BusinessCard>
    ) = withContext(IO) {

        for (businesscard in networkBusinessCards) {
            businesscardCacheDataSource.searchBusinessCardById(businesscard.id)?.let { cachedBusinessCard ->
                cachedBusinessCards.remove(cachedBusinessCard)
                checkIfCachedBusinessCardRequiresUpdate(cachedBusinessCard, businesscard)
            } ?: businesscardCacheDataSource.insertBusinessCard(businesscard)
        }
        // insert remaining into network
        for (cachedBusinessCard in cachedBusinessCards) {
            businesscardNetworkDataSource.insertOrUpdateBusinessCard(cachedBusinessCard)
        }
    }

    private suspend fun checkIfCachedBusinessCardRequiresUpdate(
        cachedBusinessCard: BusinessCard,
        networkBusinessCard: BusinessCard
    ) {
        val cacheUpdatedAt = cachedBusinessCard.updated_at
        val networkUpdatedAt = networkBusinessCard.updated_at

        // update cache (network has newest data)
        if (networkUpdatedAt > cacheUpdatedAt) {
            printLogD(
                "SyncBusinessCards",
                "cacheUpdatedAt: ${cacheUpdatedAt}, " +
                        "networkUpdatedAt: ${networkUpdatedAt}, " +
                        "businesscard: ${cachedBusinessCard.title}"
            )
            safeCacheCall(IO) {
                businesscardCacheDataSource.updateBusinessCard(
                    networkBusinessCard.id,
                    networkBusinessCard.title,
                    networkBusinessCard.body,
                    networkBusinessCard.updated_at // retain network timestamp
                )
            }
        }
        // update network (cache has newest data)
        else if (networkUpdatedAt < cacheUpdatedAt) {
            safeApiCall(IO) {
                businesscardNetworkDataSource.insertOrUpdateBusinessCard(cachedBusinessCard)
            }
        }
    }

    // for debugging
//    private fun printCacheLongTimestamps(businesscards: List<BusinessCard>){
//        for(businesscard in businesscards){
//            printLogD("SyncBusinessCards",
//                "date: ${dateUtil.convertServerStringDateToLong(businesscard.updated_at)}")
//        }
//    }

}






























