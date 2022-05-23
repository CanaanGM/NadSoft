package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state

sealed class BusinessCardListToolbarState {

    class MultiSelectionState: BusinessCardListToolbarState(){

        override fun toString(): String {
            return "MultiSelectionState"
        }
    }

    class SearchViewState: BusinessCardListToolbarState(){

        override fun toString(): String {
            return "SearchViewState"
        }
    }
}