package com.aligmohammad.nadsoftmvvm.framework.presentation

import com.aligmohammad.nadsoftmvvm.business.domain.state.DialogInputCaptureCallback
import com.aligmohammad.nadsoftmvvm.business.domain.state.Response
import com.aligmohammad.nadsoftmvvm.business.domain.state.StateMessageCallback


interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}


















