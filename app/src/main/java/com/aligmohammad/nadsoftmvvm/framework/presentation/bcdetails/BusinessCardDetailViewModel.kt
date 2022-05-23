package com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails

import androidx.lifecycle.LiveData
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.BusinessCardDetailInteractors
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.UpdateBusinessCard.Companion.UPDATE_FAILED
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.model.BusinessCardCacheEntity
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardDetailStateEvent.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardDetailViewState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardInteractionManager
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardInteractionState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.CollapsingToolbarState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.CollapsingToolbarState.Collapsed
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.CollapsingToolbarState.Expanded
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


const val BUSINESS_DETAIL_ERROR_RETRIEVEING_SELECTED_BC =
    "Error retrieving selected businesscard from bundle."
const val BUSINESS_DETAIL_SELECTED_BC_BUNDLE_KEY = "selectedBusinessCard"
const val BUSINESS_TITLE_CANNOT_BE_EMPTY = "BusinessCard title can not be empty."

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class BusinessCardDetailViewModel
@Inject
constructor(
    private val businesscardInteractors: BusinessCardDetailInteractors
) : BaseViewModel<BusinessCardDetailViewState>() {

    private val businesscardInteractionManager: BusinessCardInteractionManager =
        BusinessCardInteractionManager()
    val businesscardTitleInteractionState: LiveData<BusinessCardInteractionState>
        get() = businesscardInteractionManager.businesscardTitleState
    val businesscardBodyInteractionState: LiveData<BusinessCardInteractionState>
        get() = businesscardInteractionManager.businesscardBodyState
    val collapsingToolbarState: LiveData<CollapsingToolbarState>
        get() = businesscardInteractionManager.collapsingToolbarState

    override fun handleNewData(data: BusinessCardDetailViewState) {
        // no data coming in from requests...
    }

    override fun setStateEvent(stateEvent: StateEvent) {

//        if(canExecuteNewStateEvent(stateEvent)){
        val job: Flow<DataState<BusinessCardDetailViewState>?> = when (stateEvent) {

            is UpdateBusinessCardEvent -> {
                val pk = getBusinessCard()?.id
                if (!isBusinessCardTitleNull() && pk != null) {
                    businesscardInteractors.updateBusinessCard.updateBusinessCard(
                        businesscard = getBusinessCard()!!,
                        stateEvent = stateEvent
                    )
                } else {
                    emitStateMessageEvent(
                        stateMessage = StateMessage(
                            response = Response(
                                message = UPDATE_FAILED,
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

            is DeleteBusinessCardEvent -> {
                businesscardInteractors.deleteBusinessCard.deleteBusinessCard(
                    businesscard = stateEvent.businesscard,
                    stateEvent = stateEvent
                )
            }

            is CreateStateMessageEvent -> {
                emitStateMessageEvent(
                    stateMessage = stateEvent.stateMessage,
                    stateEvent = stateEvent
                )
            }

            else -> {
                emitInvalidStateEvent(stateEvent)
            }
        }
        launchJob(stateEvent, job)
//        }
    }

    fun beginPendingDelete(businesscard: BusinessCard) {
        setStateEvent(
            DeleteBusinessCardEvent(
                businesscard = businesscard
            )
        )
    }

    private fun isBusinessCardTitleNull(): Boolean {
        val title = getBusinessCard()?.title
        if (title.isNullOrBlank()) {
            setStateEvent(
                CreateStateMessageEvent(
                    stateMessage = StateMessage(
                        response = Response(
                            message = BUSINESS_TITLE_CANNOT_BE_EMPTY,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Info()
                        )
                    )
                )
            )
            return true
        } else {
            return false
        }
    }

    fun getBusinessCard(): BusinessCard? {
        return getCurrentViewStateOrNew().businesscard
    }

    override fun initNewViewState(): BusinessCardDetailViewState {
        return BusinessCardDetailViewState()
    }

    fun setBusinessCard(businesscard: BusinessCard?) {
        val update = getCurrentViewStateOrNew()
        update.businesscard = businesscard
        setViewState(update)
    }

    fun setCollapsingToolbarState(
        state: CollapsingToolbarState
    ) = businesscardInteractionManager.setCollapsingToolbarState(state)

    fun updateBusinessCard(title: String?, body: String?) {
        updateBusinessCardTitle(title)
        updateBusinessCardBody(body)
    }

    fun updateBusinessCardTitle(title: String?) {
        if (title == null) {
            setStateEvent(
                CreateStateMessageEvent(
                    stateMessage = StateMessage(
                        response = Response(
                            message = BusinessCardCacheEntity.nullTitleError(),
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                )
            )
        } else {
            val update = getCurrentViewStateOrNew()
            val updatedBusinessCard = update.businesscard?.copy(
                title = title
            )
            update.businesscard = updatedBusinessCard
            setViewState(update)
        }
    }

    fun updateBusinessCardBody(body: String?) {
        val update = getCurrentViewStateOrNew()
        val updatedBusinessCard = update.businesscard?.copy(
            body = body ?: ""
        )
        update.businesscard = updatedBusinessCard
        setViewState(update)
    }

    fun setBusinessCardInteractionTitleState(state: BusinessCardInteractionState) {
        businesscardInteractionManager.setNewBusinessCardTitleState(state)
    }

    fun setBusinessCardInteractionBodyState(state: BusinessCardInteractionState) {
        businesscardInteractionManager.setNewBusinessCardBodyState(state)
    }

    fun isToolbarCollapsed() = collapsingToolbarState.toString()
        .equals(Collapsed().toString())

    fun setIsUpdatePending(isPending: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isUpdatePending = isPending
        setViewState(update)
    }

    fun getIsUpdatePending(): Boolean {
        return getCurrentViewStateOrNew().isUpdatePending ?: false
    }

    fun isToolbarExpanded() = collapsingToolbarState.toString()
        .equals(Expanded().toString())

    // return true if in EditState
    fun checkEditState() = businesscardInteractionManager.checkEditState()

    fun exitEditState() = businesscardInteractionManager.exitEditState()

    fun isEditingTitle() = businesscardInteractionManager.isEditingTitle()

    fun isEditingBody() = businesscardInteractionManager.isEditingBody()

    // force observers to refresh
    fun triggerBusinessCardObservers() {
        getCurrentViewStateOrNew().businesscard?.let { businesscard ->
            setBusinessCard(businesscard)
        }
    }
}












































