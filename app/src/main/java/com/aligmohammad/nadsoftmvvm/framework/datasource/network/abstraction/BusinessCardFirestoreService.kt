package com.aligmohammad.nadsoftmvvm.framework.datasource.network.abstraction

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard

interface BusinessCardFirestoreService {

    suspend fun insertOrUpdateBusinessCard(businesscard: BusinessCard)

    suspend fun insertOrUpdateBusinessCards(businesscards: List<BusinessCard>)

    suspend fun deleteBusinessCard(primaryKey: String)

    suspend fun insertDeletedBusinessCard(businesscard: BusinessCard)

    suspend fun insertDeletedBusinessCards(businesscards: List<BusinessCard>)

    suspend fun deleteDeletedBusinessCard(businesscard: BusinessCard)

    suspend fun deleteAllBusinessCards()

    suspend fun getDeletedBusinessCards(): List<BusinessCard>

    suspend fun searchBusinessCard(businesscard: BusinessCard): BusinessCard?

    suspend fun getAllBusinessCards(): List<BusinessCard>


}