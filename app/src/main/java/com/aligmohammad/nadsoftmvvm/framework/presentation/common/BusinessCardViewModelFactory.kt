package com.aligmohammad.nadsoftmvvm.framework.presentation.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCardFactory
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.BusinessCardDetailInteractors
import com.aligmohammad.nadsoftmvvm.business.interactors.cardlist.BusinessCardListInteracts
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.BusinessCardDetailViewModel
import com.aligmohammad.nadsoftmvvm.framework.presentation.businesscardlist.BusinessCardListViewModel
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.BusinessCardNetworkSyncManager
import com.aligmohammad.nadsoftmvvm.framework.presentation.splash.SplashViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
class BusinessCardViewModelFactory
constructor(
    private val businessCardListInteracts: BusinessCardListInteracts,
    private val businessCardDetailInteractors: BusinessCardDetailInteractors,
    private val businessCardNetworkSyncManager: BusinessCardNetworkSyncManager,
    private val businessCardFactory: BusinessCardFactory,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){

            BusinessCardListViewModel::class.java -> {
                BusinessCardListViewModel(
                    businesscardInteracts = businessCardListInteracts,
                    businesscardFactory = businessCardFactory,
                    editor = editor,
                    sharedPreferences = sharedPreferences
                ) as T
            }

            BusinessCardDetailViewModel::class.java -> {
                BusinessCardDetailViewModel(
                    businesscardInteractors = businessCardDetailInteractors
                ) as T
            }

            SplashViewModel::class.java -> {
                SplashViewModel(
                    businesscardNetworkSyncManager = businessCardNetworkSyncManager
                ) as T
            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}




















