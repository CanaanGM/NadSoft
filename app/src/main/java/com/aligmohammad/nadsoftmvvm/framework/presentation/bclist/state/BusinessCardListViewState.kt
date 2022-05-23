package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state

import android.os.Parcelable
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusinessCardListViewState(

    var businesscardList: ArrayList<BusinessCard>? = null,
    var newBusinessCard: BusinessCard? = null, // businesscard that can be created with fab
    var businesscardPendingDelete: BusinessCardPendingDelete? = null, // set when delete is pending (can be undone)
    var searchQuery: String? = null,
    var page: Int? = null,
    var isQueryExhausted: Boolean? = null,
    var filter: String? = null,
    var order: String? = null,
    var layoutManagerState: Parcelable? = null,
    var numBusinessCardsInCache: Int? = null

) : Parcelable, ViewState {

    @Parcelize
    data class BusinessCardPendingDelete(
        var businesscard: BusinessCard? = null,
        var listPosition: Int? = null
    ) : Parcelable
}
























