package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListToolbarState.*

class BusinessCardListInteractionManager {

    private val _selectedBusinessCards: MutableLiveData<ArrayList<BusinessCard>> = MutableLiveData()

    private val _toolbarState: MutableLiveData<BusinessCardListToolbarState>
            = MutableLiveData(SearchViewState())

    val selectedBusinessCards: LiveData<ArrayList<BusinessCard>>
            get() = _selectedBusinessCards

    val toolbarState: LiveData<BusinessCardListToolbarState>
            get() = _toolbarState

    fun setToolbarState(state: BusinessCardListToolbarState){
        _toolbarState.value = state
    }

    fun getSelectedBusinessCards():ArrayList<BusinessCard> = _selectedBusinessCards.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean{
        return _toolbarState.value.toString() == MultiSelectionState().toString()
    }

    fun addOrRemoveBusinessCardFromSelectedList(businesscard: BusinessCard){
        var list = _selectedBusinessCards.value
        if(list == null){
            list = ArrayList()
        }
        if (list.contains(businesscard)){
            list.remove(businesscard)
        }
        else{
            list.add(businesscard)
        }
        _selectedBusinessCards.value = list
    }

    fun isBusinessCardSelected(businesscard: BusinessCard): Boolean{
        return _selectedBusinessCards.value?.contains(businesscard)?: false
    }

    fun clearSelectedBusinessCards(){
        _selectedBusinessCards.value = null
    }

}















