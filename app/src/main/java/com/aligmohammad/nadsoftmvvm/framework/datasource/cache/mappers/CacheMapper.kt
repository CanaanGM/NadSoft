package com.aligmohammad.nadsoftmvvm.framework.datasource.cache.mappers

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.business.domain.util.EntityMapper
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.model.BusinessCardCacheEntity
import javax.inject.Inject


class CacheMapper
@Inject
constructor(
    private val dateUtil: DateUtil
): EntityMapper<BusinessCardCacheEntity, BusinessCard>
{

    fun entityListToBusinessCardList(entities: List<BusinessCardCacheEntity>): List<BusinessCard>{
        val list: ArrayList<BusinessCard> = ArrayList()
        for(entity in entities){
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun businesscardListToEntityList(businesscards: List<BusinessCard>): List<BusinessCardCacheEntity>{
        val entities: ArrayList<BusinessCardCacheEntity> = ArrayList()
        for(businesscard in businesscards){
            entities.add(mapToEntity(businesscard))
        }
        return entities
    }

    override fun mapFromEntity(entity: BusinessCardCacheEntity): BusinessCard {
        return BusinessCard(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            updated_at = entity.updated_at,
            created_at = entity.created_at
        )
    }

    override fun mapToEntity(domainModel: BusinessCard): BusinessCardCacheEntity {
        return BusinessCardCacheEntity(
            id = domainModel.id,
            title = domainModel.title,
            body = domainModel.body,
            updated_at = domainModel.updated_at,
            created_at = domainModel.created_at
        )
    }
}







