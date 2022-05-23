package com.aligmohammad.nadsoftmvvm.framework.datasource.cache.implementation

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.abstraction.BusinessCardDaoService
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BusinessCardDao
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.returnOrderedQuery
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.mappers.CacheMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessCardDaoServiceImpl
@Inject
constructor(
    private val businesscardDao: BusinessCardDao,
    private val businesscardMapper: CacheMapper,
    private val dateUtil: DateUtil
): BusinessCardDaoService {

    override suspend fun insertBusinessCard(businesscard: BusinessCard): Long {
        return businesscardDao.insertBusinessCard(businesscardMapper.mapToEntity(businesscard))
    }

    override suspend fun insertBusinessCards(businesscards: List<BusinessCard>): LongArray {
        return businesscardDao.insertBusinessCards(
            businesscardMapper.businesscardListToEntityList(businesscards)
        )
    }

    override suspend fun searchBusinessCardById(id: String): BusinessCard? {
        return businesscardDao.searchBusinessCardById(id)?.let { businesscard ->
            businesscardMapper.mapFromEntity(businesscard)
        }
    }

    override suspend fun updateBusinessCard(
        primaryKey: String,
        title: String,
        body: String?,
        timestamp: String?
    ): Int {
        return if(timestamp != null){
            businesscardDao.updateBusinessCard(
                primaryKey = primaryKey,
                title = title,
                body = body,
                updated_at = timestamp
            )
        }else{
            businesscardDao.updateBusinessCard(
                primaryKey = primaryKey,
                title = title,
                body = body,
                updated_at = dateUtil.getCurrentTimestamp()
            )
        }

    }

    override suspend fun deleteBusinessCard(primaryKey: String): Int {
        return businesscardDao.deleteBusinessCard(primaryKey)
    }

    override suspend fun deleteBusinessCards(businesscards: List<BusinessCard>): Int {
        val ids = businesscards.mapIndexed {index, value -> value.id}
        return businesscardDao.deleteBusinessCards(ids)
    }

    override suspend fun searchBusinessCards(): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(
            businesscardDao.searchBusinessCards()
        )
    }

    override suspend fun getAllBusinessCards(): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(businesscardDao.getAllBusinessCards())
    }

    override suspend fun searchBusinessCardsOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(
            businesscardDao.searchBusinessCardsOrderByDateDESC(
                query = query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchBusinessCardsOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(
            businesscardDao.searchBusinessCardsOrderByDateASC(
                query = query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchBusinessCardsOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(
            businesscardDao.searchBusinessCardsOrderByTitleDESC(
                query = query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchBusinessCardsOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(
            businesscardDao.searchBusinessCardsOrderByTitleASC(
                query = query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun getNumBusinessCards(): Int {
        return businesscardDao.getNumBusinessCards()
    }

    override suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<BusinessCard> {
        return businesscardMapper.entityListToBusinessCardList(
            businesscardDao.returnOrderedQuery(
                query = query,
                page = page,
                filterAndOrder = filterAndOrder
            )
        )
    }
}













