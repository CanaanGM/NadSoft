package com.aligmohammad.nadsoftmvvm.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BusinessCardDatabase
import com.aligmohammad.nadsoftmvvm.framework.datasource.preferences.PreferenceKeys
import com.aligmohammad.nadsoftmvvm.framework.presentation.BaseApplication
import com.aligmohammad.nadsoftmvvm.util.AndroidTestUtils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Module
object ProductionModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideAndroidTestUtils(): AndroidTestUtils {
        return AndroidTestUtils(false)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(
        application: BaseApplication
    ): SharedPreferences {
        return application
            .getSharedPreferences(
                PreferenceKeys.CARD_PREFERENCES,
                Context.MODE_PRIVATE
            )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardDb(app: BaseApplication): BusinessCardDatabase {
        return Room
            .databaseBuilder(
                app,
                BusinessCardDatabase::class.java,
                BusinessCardDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


}












