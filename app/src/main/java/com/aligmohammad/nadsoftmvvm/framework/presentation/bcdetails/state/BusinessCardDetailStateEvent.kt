package com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.StateEvent
import com.aligmohammad.nadsoftmvvm.business.domain.state.StateMessage


sealed class BusinessCardDetailStateEvent: StateEvent {


    class UpdateBusinessCardEvent: BusinessCardDetailStateEvent(){

        override fun errorInfo(): String {
            return "Error updating businesscard."
        }

        override fun eventName(): String {
            return "UpdateBusinessCardEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteBusinessCardEvent(
        val businesscard: BusinessCard
    ): BusinessCardDetailStateEvent(){

        override fun errorInfo(): String {
            return "Error deleting businesscard."
        }

        override fun eventName(): String {
            return "DeleteBusinessCardEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class CreateStateMessageEvent(
        val stateMessage: StateMessage
    ): BusinessCardDetailStateEvent(){

        override fun errorInfo(): String {
            return "Error creating a new state message."
        }

        override fun eventName(): String {
            return "CreateStateMessageEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

}




















