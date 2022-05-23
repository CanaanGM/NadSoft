package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import BusinessCardCacheDataSource
import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.business.data.util.safeApiCall
import com.aligmohammad.nadsoftmvvm.business.data.util.safeCacheCall
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InsertMultipleBusinessCards(
    private val businesscardCacheDataSource: BusinessCardCacheDataSource,
    private val businesscardNetworkDataSource: BusinessCardNetworkDataSource
){

    fun insertBusinessCards(
        numBusinessCards: Int,
        stateEvent: StateEvent
    ): Flow<DataState<BusinessCardListViewState>?> = flow {

        val businesscardList = BusinessCardListTester.generateBusinessCardList(numBusinessCards)
        safeCacheCall(IO){
            businesscardCacheDataSource.insertBusinessCards(businesscardList)
        }

        emit(
            DataState.data<BusinessCardListViewState>(
                response = Response(
                    message = "success",
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.None()
                ),
                data = null,
                stateEvent = stateEvent
            )
        )

        updateNetwork(businesscardList)
    }

    private suspend fun updateNetwork(businesscardList: List<BusinessCard>){
        safeApiCall(IO){
            businesscardNetworkDataSource.insertOrUpdateBusinessCards(businesscardList)
        }
    }

}


private object BusinessCardListTester {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val dateUtil =
        DateUtil(dateFormat)

    fun generateBusinessCardList(numBusinessCards: Int): List<BusinessCard>{
        val list: ArrayList<BusinessCard> = ArrayList()
        for(id in 0..numBusinessCards){
            list.add(generateBusinessCard())
        }
        return list
    }

    fun generateBusinessCard(): BusinessCard {
        val businesscard = BusinessCard(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = dateUtil.getCurrentTimestamp(),
            updated_at = dateUtil.getCurrentTimestamp()
        )
        return businesscard
    }
}