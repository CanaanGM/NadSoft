package com.aligmohammad.nadsoftmvvm.framework.datasource.cache.abstraction

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_PAGINATION_PAGE_SIZE

interface BusinessCardDaoService {

    suspend fun insertBusinessCard(businesscard: BusinessCard): Long

    suspend fun insertBusinessCards(businesscards: List<BusinessCard>): LongArray

    suspend fun searchBusinessCardById(id: String): BusinessCard?

    suspend fun updateBusinessCard(
        primaryKey: String,
        title: String,
        body: String?,
        timestamp: String?
    ): Int

    suspend fun deleteBusinessCard(primaryKey: String): Int

    suspend fun deleteBusinessCards(businesscards: List<BusinessCard>): Int

    suspend fun searchBusinessCards(): List<BusinessCard>

    suspend fun getAllBusinessCards(): List<BusinessCard>

    suspend fun searchBusinessCardsOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCard>

    suspend fun searchBusinessCardsOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCard>

    suspend fun searchBusinessCardsOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCard>

    suspend fun searchBusinessCardsOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCard>

    suspend fun getNumBusinessCards(): Int

    suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<BusinessCard>
}












