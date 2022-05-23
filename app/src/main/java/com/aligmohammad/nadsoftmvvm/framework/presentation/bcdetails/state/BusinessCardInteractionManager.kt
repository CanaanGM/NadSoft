package com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.CollapsingToolbarState.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardInteractionState.*

// Both can not be in 'EditState' at the same time.
class BusinessCardInteractionManager{

    private val _businesscardTitleState: MutableLiveData<BusinessCardInteractionState>
            = MutableLiveData(DefaultState())

    private val _businesscardBodyState: MutableLiveData<BusinessCardInteractionState>
            = MutableLiveData(DefaultState())

    private val _collapsingToolbarState: MutableLiveData<CollapsingToolbarState>
            = MutableLiveData(Expanded())

    val businesscardTitleState: LiveData<BusinessCardInteractionState>
            get() = _businesscardTitleState

    val businesscardBodyState: LiveData<BusinessCardInteractionState>
        get() = _businesscardBodyState

    val collapsingToolbarState: LiveData<CollapsingToolbarState>
        get() = _collapsingToolbarState

    fun setCollapsingToolbarState(state: CollapsingToolbarState){
        if(!state.toString().equals(_collapsingToolbarState.value.toString())){
            _collapsingToolbarState.value = state
        }
    }

    fun setNewBusinessCardTitleState(state: BusinessCardInteractionState){
        if(!businesscardTitleState.toString().equals(state.toString())){
            _businesscardTitleState.value = state
            when(state){

                is EditState -> {
                    _businesscardBodyState.value = DefaultState()
                }
            }
        }
    }

    fun setNewBusinessCardBodyState(state: BusinessCardInteractionState){
        if(!businesscardBodyState.toString().equals(state.toString())){
            _businesscardBodyState.value = state
            when(state){

                is EditState -> {
                    _businesscardTitleState.value = DefaultState()
                }
            }
        }
    }

    fun isEditingTitle() = businesscardTitleState.value.toString().equals(EditState().toString())

    fun isEditingBody() = businesscardBodyState.value.toString().equals(EditState().toString())

    fun exitEditState(){
        _businesscardTitleState.value = DefaultState()
        _businesscardBodyState.value = DefaultState()
    }

    // return true if either title or body are in EditState
    fun checkEditState() = businesscardTitleState.value.toString().equals(EditState().toString())
            || businesscardBodyState.value.toString().equals(EditState().toString())



}

















