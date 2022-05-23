package com.aligmohammad.nadsoftmvvm.business.data.network.implementation

import BusinessCardNetworkDataSource
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.abstraction.BusinessCardFirestoreService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BusinessCardNetworkDataSourceImpl
@Inject
constructor(
    private val firestoreService: BusinessCardFirestoreService
) : BusinessCardNetworkDataSource {

    override suspend fun insertOrUpdateBusinessCard(businesscard: BusinessCard) {
        return firestoreService.insertOrUpdateBusinessCard(businesscard)
    }

    override suspend fun deleteBusinessCard(primaryKey: String) {
        return firestoreService.deleteBusinessCard(primaryKey)
    }

    override suspend fun insertDeletedBusinessCard(businesscard: BusinessCard) {
        return firestoreService.insertDeletedBusinessCard(businesscard)
    }

    override suspend fun insertDeletedBusinessCards(businesscards: List<BusinessCard>) {
        return firestoreService.insertDeletedBusinessCards(businesscards)
    }

    override suspend fun deleteDeletedBusinessCard(businesscard: BusinessCard) {
        return firestoreService.deleteDeletedBusinessCard(businesscard)
    }

    override suspend fun getDeletedBusinessCards(): List<BusinessCard> {
        return firestoreService.getDeletedBusinessCards()
    }

    override suspend fun deleteAllBusinessCards() {
        firestoreService.deleteAllBusinessCards()
    }

    override suspend fun searchBusinessCard(businesscard: BusinessCard): BusinessCard? {
        return firestoreService.searchBusinessCard(businesscard)
    }

    override suspend fun getAllBusinessCards(): List<BusinessCard> {
        return firestoreService.getAllBusinessCards()
    }

    override suspend fun insertOrUpdateBusinessCards(businesscards: List<BusinessCard>) {
        return firestoreService.insertOrUpdateBusinessCards(businesscards)
    }


}





























