package com.aligmohammad.nadsoftmvvm.di

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.BusinessCardFragmentFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Module
object BusinessCardFragmentFactoryModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideBusinessCardFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        dateUtil: DateUtil
    ): FragmentFactory {
        return BusinessCardFragmentFactory(
            viewModelFactory,
            dateUtil
        )
    }
}