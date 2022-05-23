package com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state



sealed class BusinessCardInteractionState {

    class EditState: BusinessCardInteractionState() {

        override fun toString(): String {
            return "EditState"
        }
    }

    class DefaultState: BusinessCardInteractionState(){

        override fun toString(): String {
            return "DefaultState"
        }
    }
}