package com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state

import android.os.Parcelable
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusinessCardDetailViewState(

    var businesscard: BusinessCard? = null,

    var isUpdatePending: Boolean? = null

) : Parcelable, ViewState




















