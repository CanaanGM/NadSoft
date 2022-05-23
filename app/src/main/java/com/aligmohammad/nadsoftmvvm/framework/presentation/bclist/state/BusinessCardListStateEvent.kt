package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state

import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.StateEvent
import com.aligmohammad.nadsoftmvvm.business.domain.state.StateMessage


sealed class BusinessCardListStateEvent: StateEvent {

    class InsertNewBusinessCardEvent(
        val title: String
    ): BusinessCardListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting new businesscard."
        }

        override fun eventName(): String {
            return "InsertNewBusinessCardEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    // for testing
    class InsertMultipleBusinessCardsEvent(
        val numBusinessCards: Int
    ): BusinessCardListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting the businesscards."
        }

        override fun eventName(): String {
            return "InsertMultipleBusinessCardsEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteBusinessCardEvent(
        val businesscard: BusinessCard
    ): BusinessCardListStateEvent(){

        override fun errorInfo(): String {
            return "Error deleting businesscard."
        }

        override fun eventName(): String {
            return "DeleteBusinessCardEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteMultipleBusinessCardsEvent(
        val businesscards: List<BusinessCard>
    ): BusinessCardListStateEvent(){

        override fun errorInfo(): String {
            return "Error deleting the selected businesscards."
        }

        override fun eventName(): String {
            return "DeleteMultipleBusinessCardsEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class RestoreDeletedBusinessCardEvent(
        val businesscard: BusinessCard
    ): BusinessCardListStateEvent() {

        override fun errorInfo(): String {
            return "Error restoring the businesscard that was deleted."
        }

        override fun eventName(): String {
            return "RestoreDeletedBusinessCardEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

    class SearchBusinessCardsEvent(
        val clearLayoutManagerState: Boolean = true
    ): BusinessCardListStateEvent(){

        override fun errorInfo(): String {
            return "Error getting list of businesscards."
        }

        override fun eventName(): String {
            return "SearchBusinessCardsEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class GetNumBusinessCardsInCacheEvent: BusinessCardListStateEvent(){

        override fun errorInfo(): String {
            return "Error getting the number of businesscards from the cache."
        }

        override fun eventName(): String {
            return "GetNumBusinessCardsInCacheEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class CreateStateMessageEvent(
        val stateMessage: StateMessage
    ): BusinessCardListStateEvent(){

        override fun errorInfo(): String {
            return "Error creating a new state message."
        }

        override fun eventName(): String {
            return "CreateStateMessageEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

}




















