package com.aligmohammad.nadsoftmvvm.framework.datasource.network.implementation

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.abstraction.BusinessCardFirestoreService
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.mappers.NetworkMapper
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.model.BusinessCardNetworkEntity
import com.aligmohammad.nadsoftmvvm.util.cLog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessCardFirestoreServiceImpl
@Inject
constructor(
    private val firestore: FirebaseFirestore,
    private val networkMapper: NetworkMapper
) : BusinessCardFirestoreService {

    override suspend fun insertOrUpdateBusinessCard(businesscard: BusinessCard) {
        val entity = networkMapper.mapToEntity(businesscard)
        entity.updated_at = Timestamp.now()
        firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun deleteBusinessCard(primaryKey: String) {
        firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(primaryKey)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun insertDeletedBusinessCard(businesscard: BusinessCard) {
        val entity = networkMapper.mapToEntity(businesscard)
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun insertDeletedBusinessCards(businesscards: List<BusinessCard>) {
        if (businesscards.size > 500) {
            throw Exception("Cannot delete more than 500 businesscards at a time in firestore.")
        }

        val collectionRef = firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)

        firestore.runBatch { batch ->
            for (businesscard in businesscards) {
                val documentRef = collectionRef.document(businesscard.id)
                batch.set(documentRef, networkMapper.mapToEntity(businesscard))
            }
        }.addOnFailureListener {
            cLog(it.message)
        }.await()
    }

    override suspend fun deleteDeletedBusinessCard(businesscard: BusinessCard) {
        val entity = networkMapper.mapToEntity(businesscard)
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun deleteAllBusinessCards() {
        firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .delete()
            .await()
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .delete()
            .await()
    }

    override suspend fun getDeletedBusinessCards(): List<BusinessCard> {
        return networkMapper.entityListToBusinessCardList(
            firestore
                .collection(DELETES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await().toObjects(BusinessCardNetworkEntity::class.java)
        )
    }

    override suspend fun searchBusinessCard(businesscard: BusinessCard): BusinessCard? {
        return firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(businesscard.id)
            .get()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
            .toObject(BusinessCardNetworkEntity::class.java)?.let {
                networkMapper.mapFromEntity(it)
            }
    }

    override suspend fun getAllBusinessCards(): List<BusinessCard> {
        return networkMapper.entityListToBusinessCardList(
            firestore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(BusinessCardNetworkEntity::class.java)
        )
    }

    override suspend fun insertOrUpdateBusinessCards(businesscards: List<BusinessCard>) {

        if (businesscards.size > 500) {
            throw Exception("Cannot insert more than 500 businesscards at a time into firestore.")
        }

        val collectionRef = firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)

        firestore.runBatch { batch ->
            for (businesscard in businesscards) {
                val entity = networkMapper.mapToEntity(businesscard)
                entity.updated_at = Timestamp.now()
                val documentRef = collectionRef.document(businesscard.id)
                batch.set(documentRef, entity)
            }
        }.addOnFailureListener {
            cLog(it.message)
        }.await()

    }

    companion object {
        const val NOTES_COLLECTION = "businesscards"
        const val USERS_COLLECTION = "users"
        const val DELETES_COLLECTION = "deletes"
        const val USER_ID = "12321fwfwerisfmSDFDEwLoew"
        const val EMAIL = "recticode@gmail.com"
    }


}












