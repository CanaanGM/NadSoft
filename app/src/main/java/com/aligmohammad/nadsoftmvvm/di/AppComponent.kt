package com.aligmohammad.nadsoftmvvm.di

import com.aligmohammad.nadsoftmvvm.framework.presentation.BaseApplication
import com.aligmohammad.nadsoftmvvm.framework.presentation.MainActivity
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.BusinessCardNetworkSyncManager
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.BusinessCardDetailFragment
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.BusinessCardListFragment
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.SplashFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        ProductionModule::class,
        AppModule::class,
        BusinessCardViewModelModule::class,
        BusinessCardFragmentFactoryModule::class
    ]
)
interface AppComponent {

    val businesscardNetworkSync: BusinessCardNetworkSyncManager

    @Component.Factory
    interface Factory{

        fun create(@BindsInstance app: BaseApplication): AppComponent
    }

    fun inject(mainActivity: MainActivity)

    fun inject(splashFragment: SplashFragment)

    fun inject(businesscardListFragment: BusinessCardListFragment)

    fun inject(businesscardDetailFragment: BusinessCardDetailFragment)
}












