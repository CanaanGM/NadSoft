package com.aligmohammad.nadsoftmvvm.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCardFactory
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.BusinessCardDetailInteractors
import com.aligmohammad.nadsoftmvvm.business.interactors.cardlist.BusinessCardListInteracts
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.BusinessCardViewModelFactory
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.BusinessCardNetworkSyncManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object BusinessCardViewModelModule {

    @Singleton
    @JvmStatic
    @Provides
    fun provideBusinessCardViewModelFactory(
        businessCardListInteracts: BusinessCardListInteracts,
        businessCardDetailInteractors: BusinessCardDetailInteractors,
        businessCardNetworkSyncManager: BusinessCardNetworkSyncManager,
        businessCardFactory: BusinessCardFactory,
        editor: SharedPreferences.Editor,
        sharedPreferences: SharedPreferences
    ): ViewModelProvider.Factory{
        return BusinessCardViewModelFactory(
            businessCardListInteracts = businessCardListInteracts,
            businessCardDetailInteractors = businessCardDetailInteractors,
            businessCardNetworkSyncManager = businessCardNetworkSyncManager,
            businessCardFactory = businessCardFactory,
            editor = editor,
            sharedPreferences = sharedPreferences
        )
    }

}

















