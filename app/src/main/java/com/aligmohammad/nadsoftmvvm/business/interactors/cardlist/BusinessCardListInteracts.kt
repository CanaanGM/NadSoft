package com.aligmohammad.nadsoftmvvm.business.interactors.cardlist

import com.aligmohammad.nadsoftmvvm.business.interactors.common.DeleteBusinessCard
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState

class BusinessCardListInteracts(
    val insertNewBusinessCard: InsertNewBusinessCard,
    val deleteBusinessCard: DeleteBusinessCard<BusinessCardListViewState>,
    val searchBusinessCards: SearchBusinessCards,
    val getNumBusinessCards: GetNumBusinessCards,
    val restoreDeletedBusinessCard: RestoreDeletedBusinessCard,
    val deleteMultipleBusinessCards: DeleteMultipleBusinessCards,
    val insertMultipleBusinessCards: InsertMultipleBusinessCards
)














