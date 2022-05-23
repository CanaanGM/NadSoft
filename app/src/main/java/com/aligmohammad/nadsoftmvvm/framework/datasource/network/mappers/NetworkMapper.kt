package com.aligmohammad.nadsoftmvvm.framework.datasource.network.mappers

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.business.domain.util.EntityMapper
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.model.BusinessCardNetworkEntity
import javax.inject.Inject


class NetworkMapper
@Inject
constructor(
    private val dateUtil: DateUtil
): EntityMapper<BusinessCardNetworkEntity, BusinessCard>
{

    fun entityListToBusinessCardList(entities: List<BusinessCardNetworkEntity>): List<BusinessCard>{
        val list: ArrayList<BusinessCard> = ArrayList()
        for(entity in entities){
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun businesscardListToEntityList(businesscards: List<BusinessCard>): List<BusinessCardNetworkEntity>{
        val entities: ArrayList<BusinessCardNetworkEntity> = ArrayList()
        for(businesscard in businesscards){
            entities.add(mapToEntity(businesscard))
        }
        return entities
    }

    override fun mapFromEntity(entity: BusinessCardNetworkEntity): BusinessCard {
        return BusinessCard(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            updated_at = dateUtil.convertFirebaseTimestampToStringData(entity.updated_at),
            created_at = dateUtil.convertFirebaseTimestampToStringData(entity.created_at)
        )
    }

    override fun mapToEntity(domainModel: BusinessCard): BusinessCardNetworkEntity {
        return BusinessCardNetworkEntity(
            id = domainModel.id,
            title = domainModel.title,
            body = domainModel.body,
            updated_at = dateUtil.convertStringDateToFirebaseTimestamp(domainModel.updated_at),
            created_at = dateUtil.convertStringDateToFirebaseTimestamp(domainModel.created_at)
        )
    }


}







