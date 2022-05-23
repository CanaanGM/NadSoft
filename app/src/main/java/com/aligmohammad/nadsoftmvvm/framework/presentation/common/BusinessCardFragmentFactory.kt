package com.aligmohammad.nadsoftmvvm.framework.presentation.common

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.BusinessCardDetailFragment
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.BusinessCardListFragment
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.SplashFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class BusinessCardFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when(className){

            BusinessCardListFragment::class.java.name -> {
                val fragment = BusinessCardListFragment(viewModelFactory, dateUtil)
                fragment
            }

            BusinessCardDetailFragment::class.java.name -> {
                val fragment = BusinessCardDetailFragment(viewModelFactory)
                fragment
            }

            SplashFragment::class.java.name -> {
                val fragment = SplashFragment(viewModelFactory)
                fragment
            }

            else -> {
                super.instantiate(classLoader, className)
            }
        }
}