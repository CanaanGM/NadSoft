package com.aligmohammad.nadsoftmvvm.framework.presentation.businesscardlist

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.LiveData
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCardFactory
import com.aligmohammad.nadsoftmvvm.business.interactors.cardlist.DeleteMultipleBusinessCards.Companion.DELETE_BUSINESS_CARDS_YOU_MUST_SELECT
import com.aligmohammad.nadsoftmvvm.business.interactors.cardlist.BusinessCardListInteracts
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_FILTER_DATE_CREATED
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_ORDER_DESC
import com.aligmohammad.nadsoftmvvm.framework.datasource.preferences.PreferenceKeys.Companion.BC_FILTER
import com.aligmohammad.nadsoftmvvm.framework.datasource.preferences.PreferenceKeys.Companion.BC_ORDER
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.BaseViewModel
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListInteractionManager
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListStateEvent.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListToolbarState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState.*
import com.aligmohammad.nadsoftmvvm.util.printLogD
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


const val DELETE_PENDING_ERROR = "There is already a pending delete operation."
const val BUSINESS_CARD_PENDING_DELETE_BUNDLE_KEY = "pending_delete"

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class BusinessCardListViewModel
@Inject
constructor(
    private val businesscardInteracts: BusinessCardListInteracts,
    private val businesscardFactory: BusinessCardFactory,
    private val editor: SharedPreferences.Editor,
    sharedPreferences: SharedPreferences
): BaseViewModel<BusinessCardListViewState>(){

    val businesscardListInteractionManager =
        BusinessCardListInteractionManager()

    val toolbarState: LiveData<BusinessCardListToolbarState>
            get() = businesscardListInteractionManager.toolbarState

    init {
        setBusinessCardFilter(
            sharedPreferences.getString(
                BC_FILTER,
                BUSINESS_CARD_FILTER_DATE_CREATED
            )
        )
        setBusinessCardOrder(
            sharedPreferences.getString(
                BC_ORDER,
                BUSINESS_CARD_ORDER_DESC
            )
        )
    }

    override fun handleNewData(data: BusinessCardListViewState) {

        data.let { viewState ->
            viewState.businesscardList?.let { businesscardList ->
                setBusinessCardListData(businesscardList)
            }

            viewState.numBusinessCardsInCache?.let { numBusinessCards ->
                setNumBusinessCardsInCache(numBusinessCards)
            }

            viewState.newBusinessCard?.let { businesscard ->
                setBusinessCard(businesscard)
            }

            viewState.businesscardPendingDelete?.let { restoredBusinessCard ->
                restoredBusinessCard.businesscard?.let { businesscard ->
                    setRestoredBusinessCardId(businesscard)
                }
                setBusinessCardPendingDelete(null)
            }
        }

    }

    override fun setStateEvent(stateEvent: StateEvent) {

        val job: Flow<DataState<BusinessCardListViewState>?> = when(stateEvent){

            is InsertNewBusinessCardEvent -> {
                businesscardInteracts.insertNewBusinessCard.insertNewBusinessCard(
                    title = stateEvent.title,
                    stateEvent = stateEvent
                )
            }

            is InsertMultipleBusinessCardsEvent -> {
                businesscardInteracts.insertMultipleBusinessCards.insertBusinessCards(
                    numBusinessCards = stateEvent.numBusinessCards,
                    stateEvent = stateEvent
                )
            }

            is DeleteBusinessCardEvent -> {
                businesscardInteracts.deleteBusinessCard.deleteBusinessCard(
                    businesscard = stateEvent.businesscard,
                    stateEvent = stateEvent
                )
            }

            is DeleteMultipleBusinessCardsEvent -> {
                businesscardInteracts.deleteMultipleBusinessCards.deleteBusinessCards(
                    businesscards = stateEvent.businesscards,
                    stateEvent = stateEvent
                )
            }

            is RestoreDeletedBusinessCardEvent -> {
                businesscardInteracts.restoreDeletedBusinessCard.restoreDeletedBusinessCard(
                    businesscard = stateEvent.businesscard,
                    stateEvent = stateEvent
                )
            }

            is SearchBusinessCardsEvent -> {
                if(stateEvent.clearLayoutManagerState){
                    clearLayoutManagerState()
                }
                businesscardInteracts.searchBusinessCards.searchBusinessCards(
                    query = getSearchQuery(),
                    filterAndOrder = getOrder() + getFilter(),
                    page = getPage(),
                    stateEvent = stateEvent
                )
            }

            is GetNumBusinessCardsInCacheEvent -> {
                businesscardInteracts.getNumBusinessCards.getNumBusinessCards(
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
    }

    private fun removeSelectedBusinessCardsFromList(){
        val update = getCurrentViewStateOrNew()
        update.businesscardList?.removeAll(getSelectedBusinessCards())
        setViewState(update)
        clearSelectedBusinessCards()
    }

    fun deleteBusinessCards(){
        if(getSelectedBusinessCards().size > 0){
            setStateEvent(DeleteMultipleBusinessCardsEvent(getSelectedBusinessCards()))
            removeSelectedBusinessCardsFromList()
        }
        else{
            setStateEvent(
                CreateStateMessageEvent(
                    stateMessage = StateMessage(
                        response = Response(
                            message = DELETE_BUSINESS_CARDS_YOU_MUST_SELECT,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Info()
                        )
                    )
                )
            )
        }
    }

    fun getSelectedBusinessCards() = businesscardListInteractionManager.getSelectedBusinessCards()

    fun setToolbarState(state: BusinessCardListToolbarState)
            = businesscardListInteractionManager.setToolbarState(state)

    fun isMultiSelectionStateActive()
            = businesscardListInteractionManager.isMultiSelectionStateActive()

    override fun initNewViewState(): BusinessCardListViewState {
        return BusinessCardListViewState()
    }

    fun getFilter(): String {
        return getCurrentViewStateOrNew().filter
            ?: BUSINESS_CARD_FILTER_DATE_CREATED
    }

    fun getOrder(): String {
        return getCurrentViewStateOrNew().order
            ?: BUSINESS_CARD_ORDER_DESC
    }

    fun getSearchQuery(): String {
        return getCurrentViewStateOrNew().searchQuery
            ?: return ""
    }

    private fun getPage(): Int{
        return getCurrentViewStateOrNew().page
            ?: return 1
    }

    private fun setBusinessCardListData(businesscardsList: ArrayList<BusinessCard>){
        val update = getCurrentViewStateOrNew()
        update.businesscardList = businesscardsList
        setViewState(update)
    }

    fun setQueryExhausted(isExhausted: Boolean){
        val update = getCurrentViewStateOrNew()
        update.isQueryExhausted = isExhausted
        setViewState(update)
    }

    // can be selected from Recyclerview or created new from dialog
    fun setBusinessCard(businesscard: BusinessCard?){
        val update = getCurrentViewStateOrNew()
        update.newBusinessCard = businesscard
        setViewState(update)
    }

    fun setQuery(query: String?){
        val update =  getCurrentViewStateOrNew()
        update.searchQuery = query
        setViewState(update)
    }


    // if a businesscard is deleted and then restored, the id will be incorrect.
    // So need to reset it here.
    private fun setRestoredBusinessCardId(restoredBusinessCard: BusinessCard){
        val update = getCurrentViewStateOrNew()
        update.businesscardList?.let { businesscardList ->
            for((index, businesscard) in businesscardList.withIndex()){
                if(businesscard.title.equals(restoredBusinessCard.title)){
                    businesscardList.remove(businesscard)
                    businesscardList.add(index, restoredBusinessCard)
                    update.businesscardList = businesscardList
                    break
                }
            }
        }
        setViewState(update)
    }

    fun isDeletePending(): Boolean{
        val pendingBusinessCard = getCurrentViewStateOrNew().businesscardPendingDelete
        if(pendingBusinessCard != null){
            setStateEvent(
                CreateStateMessageEvent(
                    stateMessage = StateMessage(
                        response = Response(
                            message = DELETE_PENDING_ERROR,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Info()
                        )
                    )
                )
            )
            return true
        }
        else{
            return false
        }
    }

    fun beginPendingDelete(businesscard: BusinessCard){
        setBusinessCardPendingDelete(businesscard)
        removePendingBusinessCardFromList(businesscard)
        setStateEvent(
            DeleteBusinessCardEvent(
                businesscard = businesscard
            )
        )
    }

    private fun removePendingBusinessCardFromList(businesscard: BusinessCard?){
        val update = getCurrentViewStateOrNew()
        val list = update.businesscardList
        if(list?.contains(businesscard) == true){
            list.remove(businesscard)
            update.businesscardList = list
            setViewState(update)
        }
    }

    fun undoDelete(){
        // replace businesscard in viewstate
        val update = getCurrentViewStateOrNew()
        update.businesscardPendingDelete?.let { businesscard ->
            if(businesscard.listPosition != null && businesscard.businesscard != null){
                update.businesscardList?.add(
                    businesscard.listPosition as Int,
                    businesscard.businesscard as BusinessCard
                )
                setStateEvent(RestoreDeletedBusinessCardEvent(businesscard.businesscard as BusinessCard))
            }
        }
        setViewState(update)
    }


    fun setBusinessCardPendingDelete(businesscard: BusinessCard?){
        val update = getCurrentViewStateOrNew()
        if(businesscard != null){
            update.businesscardPendingDelete = BusinessCardPendingDelete(
                businesscard = businesscard,
                listPosition = findListPositionOfBusinessCard(businesscard)
            )
        }
        else{
            update.businesscardPendingDelete = null
        }
        setViewState(update)
    }

    private fun findListPositionOfBusinessCard(businesscard: BusinessCard?): Int {
        val viewState = getCurrentViewStateOrNew()
        viewState.businesscardList?.let { businesscardList ->
            for((index, item) in businesscardList.withIndex()){
                if(item.id == businesscard?.id){
                    return index
                }
            }
        }
        return 0
    }

    private fun setNumBusinessCardsInCache(numBusinessCards: Int){
        val update = getCurrentViewStateOrNew()
        update.numBusinessCardsInCache = numBusinessCards
        setViewState(update)
    }

    fun createNewBusinessCard(
        id: String? = null,
        title: String,
        body: String? = null
    ) = businesscardFactory.createSingleBusinessCard(id, title, body)

    fun getBusinessCardListSize() = getCurrentViewStateOrNew().businesscardList?.size?: 0

    private fun getNumBusinessCardsInCache() = getCurrentViewStateOrNew().numBusinessCardsInCache?: 0

    fun isPaginationExhausted() = getBusinessCardListSize() >= getNumBusinessCardsInCache()

    private fun resetPage(){
        val update = getCurrentViewStateOrNew()
        update.page = 1
        setViewState(update)
    }

    // for debugging
    fun getActiveJobs() = dataChannelManager.getActiveJobs()

    fun isQueryExhausted(): Boolean{
        printLogD("BusinessCardListViewModel",
            "is query exhasuted? ${getCurrentViewStateOrNew().isQueryExhausted?: true}")
        return getCurrentViewStateOrNew().isQueryExhausted?: true
    }

    fun clearList(){
        printLogD("ListViewModel", "clearList")
        val update = getCurrentViewStateOrNew()
        update.businesscardList = ArrayList()
        setViewState(update)
    }

    // workaround for tests
    // can't submit an empty string because SearchViews SUCK
    fun clearSearchQuery(){
        setQuery("")
        clearList()
        loadFirstPage()
    }

    fun loadFirstPage() {
        setQueryExhausted(false)
        resetPage()
        setStateEvent(SearchBusinessCardsEvent())
        printLogD("BusinessCardListViewModel",
            "loadFirstPage: ${getCurrentViewStateOrNew().searchQuery}")
    }

    fun nextPage(){
        if(!isQueryExhausted()){
            printLogD("BusinessCardListViewModel", "attempting to load next page...")
            clearLayoutManagerState()
            incrementPageNumber()
            setStateEvent(SearchBusinessCardsEvent())
        }
    }

    private fun incrementPageNumber(){
        val update = getCurrentViewStateOrNew()
        val page = update.copy().page ?: 1
        update.page = page.plus(1)
        setViewState(update)
    }

    fun retrieveNumBusinessCardsInCache(){
        setStateEvent(GetNumBusinessCardsInCacheEvent())
    }

    fun refreshSearchQuery(){
        setQueryExhausted(false)
        setStateEvent(SearchBusinessCardsEvent(false))
    }

    fun getLayoutManagerState(): Parcelable? {
        return getCurrentViewStateOrNew().layoutManagerState
    }

    fun setLayoutManagerState(layoutManagerState: Parcelable){
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = layoutManagerState
        setViewState(update)
    }

    fun clearLayoutManagerState(){
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = null
        setViewState(update)
    }

    fun addOrRemoveBusinessCardFromSelectedList(businesscard: BusinessCard)
            = businesscardListInteractionManager.addOrRemoveBusinessCardFromSelectedList(businesscard)

    fun isBusinessCardSelected(businesscard: BusinessCard): Boolean
            = businesscardListInteractionManager.isBusinessCardSelected(businesscard)

    fun clearSelectedBusinessCards() = businesscardListInteractionManager.clearSelectedBusinessCards()

    fun setBusinessCardFilter(filter: String?){
        filter?.let{
            val update = getCurrentViewStateOrNew()
            update.filter = filter
            setViewState(update)
        }
    }

    fun setBusinessCardOrder(order: String?){
        val update = getCurrentViewStateOrNew()
        update.order = order
        setViewState(update)
    }

    fun saveFilterOptions(filter: String, order: String){
        editor.putString(BC_FILTER, filter)
        editor.apply()

        editor.putString(BC_ORDER, order)
        editor.apply()
    }
}












































