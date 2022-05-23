package com.aligmohammad.nadsoftmvvm.di

import BusinessCardCacheDataSource
import BusinessCardCacheDataSourceImpl
import BusinessCardNetworkDataSource
import android.content.SharedPreferences
import com.aligmohammad.nadsoftmvvm.business.data.network.implementation.BusinessCardNetworkDataSourceImpl
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCardFactory
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.BusinessCardDetailInteractors
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.UpdateBusinessCard
import com.aligmohammad.nadsoftmvvm.business.interactors.cardlist.*
import com.aligmohammad.nadsoftmvvm.business.interactors.common.DeleteBusinessCard
import com.aligmohammad.nadsoftmvvm.business.interactors.splash.SyncBusinessCards
import com.aligmohammad.nadsoftmvvm.business.interactors.splash.SyncDeletedBusinessCards
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.abstraction.BusinessCardDaoService
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BusinessCardDao
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BusinessCardDatabase
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.implementation.BusinessCardDaoServiceImpl
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.mappers.CacheMapper
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.abstraction.BusinessCardFirestoreService
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.implementation.BusinessCardFirestoreServiceImpl
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.mappers.NetworkMapper
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.BusinessCardNetworkSyncManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC-7")
        return sdf
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDateUtil(dateFormat: SimpleDateFormat): DateUtil {
        return DateUtil(
            dateFormat
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPrefsEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardFactory(dateUtil: DateUtil): BusinessCardFactory {
        return BusinessCardFactory(
            dateUtil
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardDAO(businessCardDatabase: BusinessCardDatabase): BusinessCardDao {
        return businessCardDatabase.businessCardDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardCacheMapper(dateUtil: DateUtil): CacheMapper {
        return CacheMapper(dateUtil)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardNetworkMapper(dateUtil: DateUtil): NetworkMapper {
        return NetworkMapper(dateUtil)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardDaoService(
        businessCardDao: BusinessCardDao,
        businessCardEntityMapper: CacheMapper,
        dateUtil: DateUtil
    ): BusinessCardDaoService {
        return BusinessCardDaoServiceImpl(businessCardDao, businessCardEntityMapper, dateUtil)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardCacheDataSource(
        businessCardDaoService: BusinessCardDaoService
    ): BusinessCardCacheDataSource {
        return BusinessCardCacheDataSourceImpl(businessCardDaoService)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirestoreService(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        networkMapper: NetworkMapper
    ): BusinessCardFirestoreService {
        return BusinessCardFirestoreServiceImpl(
            firebaseAuth,
            firebaseFirestore,
            networkMapper
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardNetworkDataSource(
        firestoreService: BusinessCardFirestoreServiceImpl
    ): BusinessCardNetworkDataSource {
        return BusinessCardNetworkDataSourceImpl(
            firestoreService
        )
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardDetailInteracts(
        businessCardCacheDataSource: BusinessCardCacheDataSource,
        businessCardNetworkDataSource: BusinessCardNetworkDataSource
    ): BusinessCardDetailInteractors {
        return BusinessCardDetailInteractors(
            DeleteBusinessCard(businessCardCacheDataSource, businessCardNetworkDataSource),
            UpdateBusinessCard(businessCardCacheDataSource, businessCardNetworkDataSource)
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardListInteractors(
        businessCardCacheDataSource: BusinessCardCacheDataSource,
        businessCardNetworkDataSource: BusinessCardNetworkDataSource,
        businessCardFactory: BusinessCardFactory
    ): BusinessCardListInteracts {
        return BusinessCardListInteracts(
            InsertNewBusinessCard(
                businessCardCacheDataSource,
                businessCardNetworkDataSource,
                businessCardFactory
            ),
            DeleteBusinessCard(businessCardCacheDataSource, businessCardNetworkDataSource),
            SearchBusinessCards(businessCardCacheDataSource),
            GetNumBusinessCards(businessCardCacheDataSource),
            RestoreDeletedBusinessCard(businessCardCacheDataSource, businessCardNetworkDataSource),
            DeleteMultipleBusinessCards(businessCardCacheDataSource, businessCardNetworkDataSource),
            InsertMultipleBusinessCards(businessCardCacheDataSource, businessCardNetworkDataSource)
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSyncBusinessCards(
        businessCardCacheDataSource: BusinessCardCacheDataSource,
        businessCardNetworkDataSource: BusinessCardNetworkDataSource,
        dateUtil: DateUtil
    ): SyncBusinessCards {
        return SyncBusinessCards(
            businessCardCacheDataSource,
            businessCardNetworkDataSource,
            dateUtil

        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSyncDeletedBusinessCards(
        businessCardCacheDataSource: BusinessCardCacheDataSource,
        businessCardNetworkDataSource: BusinessCardNetworkDataSource
    ): SyncDeletedBusinessCards {
        return SyncDeletedBusinessCards(
            businessCardCacheDataSource,
            businessCardNetworkDataSource
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardNetworkSyncManager(
        syncBusinessCards: SyncBusinessCards,
        deletedBusinessCards: SyncDeletedBusinessCards
    ): BusinessCardNetworkSyncManager {
        return BusinessCardNetworkSyncManager(
            syncBusinessCards,
            deletedBusinessCards
        )
    }

}






















