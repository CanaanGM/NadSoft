package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.aligmohammad.nadsoftmvvm.R
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.interactors.common.DeleteBusinessCard.Companion.DELETE_BUSINESS_CARD_PENDING
import com.aligmohammad.nadsoftmvvm.business.interactors.common.DeleteBusinessCard.Companion.DELETE_BUSINESS_CARD_SUCCESS
import com.aligmohammad.nadsoftmvvm.business.interactors.cardlist.DeleteMultipleBusinessCards.Companion.DELETE_BUSINESS_CARDS_ARE_YOU_SURE
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_FILTER_DATE_CREATED
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_FILTER_TITLE
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_ORDER_ASC
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database.BUSINESS_CARD_ORDER_DESC
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.BaseBusinessCardFragment
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.TopSpacingItemDecoration
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.hideKeyboard
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.BUSINESS_DETAIL_SELECTED_BC_BUNDLE_KEY
import com.aligmohammad.nadsoftmvvm.framework.presentation.businesscardlist.*;
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListStateEvent.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListToolbarState.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListViewState
import com.aligmohammad.nadsoftmvvm.framework.presentation.businesscardlist.BusinessCardListViewModel
import com.aligmohammad.nadsoftmvvm.framework.presentation.businesscardlist.BUSINESS_CARD_PENDING_DELETE_BUNDLE_KEY
import com.aligmohammad.nadsoftmvvm.util.AndroidTestUtils
import com.aligmohammad.nadsoftmvvm.util.TodoCallback
import com.aligmohammad.nadsoftmvvm.util.printLogD
import kotlinx.android.synthetic.main.fragment_business_card_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


const val BC_LIST_STATE_BUNDLE_KEY = "com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state"


@FlowPreview
@ExperimentalCoroutinesApi
class BusinessCardListFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil
): BaseBusinessCardFragment(R.layout.fragment_business_card_list),
    BusinessCardListAdapter.Interaction,
    ItemTouchHelperAdapter
{

    @Inject
    lateinit var androidTestUtils: AndroidTestUtils

    val viewModel: BusinessCardListViewModel by viewModels {
        viewModelFactory
    }

    private var listAdapter: BusinessCardListAdapter? = null
    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
        arguments?.let { args ->
            args.getParcelable<BusinessCard>(BUSINESS_CARD_PENDING_DELETE_BUNDLE_KEY)?.let { businesscard ->
                viewModel.setBusinessCardPendingDelete(businesscard)
                showUndoSnackbar_deleteBusinessCard()
                clearArgs()
            }
        }
    }

    private fun clearArgs(){
        arguments?.clear()
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFAB()
        subscribeObservers()

        restoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.retrieveNumBusinessCardsInCache()
        viewModel.clearList()
        viewModel.refreshSearchQuery()

    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState()
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?){
        savedInstanceState?.let { inState ->
            (inState[BC_LIST_STATE_BUNDLE_KEY] as BusinessCardListViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    // Why didn't I use the "SavedStateHandle" here?
    // It sucks and doesn't work for testing
    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.businesscardList =  ArrayList()

        outState.putParcelable(
            BC_LIST_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }

    override fun restoreListPosition() {
        viewModel.getLayoutManagerState()?.let { lmState ->
            recycler_view?.layoutManager?.onRestoreInstanceState(lmState)
        }
    }

    private fun saveLayoutManagerState(){
        recycler_view.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.setLayoutManagerState(lmState)
        }
    }

    private fun setupRecyclerView(){
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecorator = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecorator)
            itemTouchHelper = ItemTouchHelper(
                BusinessCardItemTouchHelperCallback(
                    this@BusinessCardListFragment,
                    viewModel.businesscardListInteractionManager
                )
            )
            listAdapter = BusinessCardListAdapter(
                this@BusinessCardListFragment,
                viewLifecycleOwner,
                viewModel.businesscardListInteractionManager.selectedBusinessCards,
                dateUtil
            )
            itemTouchHelper?.attachToRecyclerView(this)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == listAdapter?.itemCount?.minus(1)) {
                        viewModel.nextPage()
                    }
                }
            })
            adapter = listAdapter
        }
    }

    private fun enableMultiSelectToolbarState(){
        view?.let { v ->
            val view = View.inflate(
                v.context,
                R.layout.layout_multiselection_toolbar,
                null
            )
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            toolbar_content_container.addView(view)
            setupMultiSelectionToolbar(view)
        }
    }

    private fun setupMultiSelectionToolbar(parentView: View){
        parentView
                .findViewById<ImageView>(R.id.action_exit_multiselect_state)
            .setOnClickListener {
                viewModel.setToolbarState(SearchViewState())
            }

        parentView
            .findViewById<ImageView>(R.id.action_delete_businesscards)
            .setOnClickListener {
                deleteBusinessCards()
            }
    }

    private fun enableSearchViewToolbarState(){
        view?.let { v ->
            val view = View.inflate(
                v.context,
                R.layout.layout_searchview_toolbar,
                null
            )
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            toolbar_content_container.addView(view)
            setupSearchView()
            setupFilterButton()
        }
    }

    private fun disableMultiSelectToolbarState(){
        view?.let {
            val view = toolbar_content_container
                .findViewById<Toolbar>(R.id.multiselect_toolbar)
            toolbar_content_container.removeView(view)
            viewModel.clearSelectedBusinessCards()
        }
    }

    private fun disableSearchViewToolbarState(){
        view?.let {
            val view = toolbar_content_container
                .findViewById<Toolbar>(R.id.searchview_toolbar)
            toolbar_content_container.removeView(view)
        }
    }

    override fun isMultiSelectionModeEnabled()
            = viewModel.isMultiSelectionStateActive()

    override fun activateMultiSelectionMode()
            = viewModel.setToolbarState(MultiSelectionState())

    private fun subscribeObservers(){

        viewModel.toolbarState.observe(viewLifecycleOwner, Observer{ toolbarState ->

            when(toolbarState){

                is MultiSelectionState -> {
                    enableMultiSelectToolbarState()
                    disableSearchViewToolbarState()
                }

                is SearchViewState -> {
                    enableSearchViewToolbarState()
                    disableMultiSelectToolbarState()
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->

            if(viewState != null){
                viewState.businesscardList?.let { businesscardList ->
                    if(viewModel.isPaginationExhausted()
                        && !viewModel.isQueryExhausted()){
                        viewModel.setQueryExhausted(true)
                    }
                    listAdapter?.submitList(businesscardList)
                    listAdapter?.notifyDataSetChanged()
                }

                // a businesscard been inserted or selected
                viewState.newBusinessCard?.let { newBusinessCard ->
                    navigateToDetailFragment(newBusinessCard)
                }

            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            printActiveJobs()
            uiController.displayProgressBar(it)
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            stateMessage?.let { message ->
                if(message.response.message?.equals(DELETE_BUSINESS_CARD_SUCCESS) == true){
                    showUndoSnackbar_deleteBusinessCard()
                }
                else{
                    uiController.onResponseReceived(
                        response = message.response,
                        stateMessageCallback = object: StateMessageCallback {
                            override fun removeMessageFromStack() {
                                viewModel.clearStateMessage()
                            }
                        }
                    )
                }
            }
        })
    }

    private fun showUndoSnackbar_deleteBusinessCard(){
        uiController.onResponseReceived(
            response = Response(
                message = DELETE_BUSINESS_CARD_PENDING,
                uiComponentType = UIComponentType.SnackBar(
                    undoCallback = object : SnackbarUndoCallback {
                        override fun undo() {
                            viewModel.undoDelete()
                        }
                    },
                    onDismissCallback = object: TodoCallback {
                        override fun execute() {
                            // if the businesscard is not restored, clear pending businesscard
                            viewModel.setBusinessCardPendingDelete(null)
                        }
                    }
                ),
                messageType = MessageType.Info()
            ),
            stateMessageCallback = object: StateMessageCallback{
                override fun removeMessageFromStack() {
                    viewModel.clearStateMessage()
                }
            }
        )
    }

    // for debugging
    private fun printActiveJobs(){

        for((index, job) in viewModel.getActiveJobs().withIndex()){
            printLogD("BusinessCardList",
                "${index}: ${job}")
        }
    }

    private fun navigateToDetailFragment(selectedBusinessCard: BusinessCard){
        val bundle = bundleOf(BUSINESS_DETAIL_SELECTED_BC_BUNDLE_KEY to selectedBusinessCard)
        findNavController().navigate(
            R.id.action_businesscard_list_fragment_to_businesscardDetailFragment,
            bundle
        )
        viewModel.setBusinessCard(null)
    }

    private fun setupUI(){
        view?.hideKeyboard()
    }

    override fun inject() {
        getAppComponent().inject(this)
    }

    override fun onItemSelected(position: Int, item: BusinessCard) {
        if(isMultiSelectionModeEnabled()){
            viewModel.addOrRemoveBusinessCardFromSelectedList(item)
        }
        else{
            viewModel.setBusinessCard(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter = null // can leak memory
        itemTouchHelper = null // can leak memory
    }

    override fun isBusinessCardSelected(businesscard: BusinessCard): Boolean {
        return viewModel.isBusinessCardSelected(businesscard)
    }

    override fun onItemSwiped(position: Int) {
        if(!viewModel.isDeletePending()){
            listAdapter?.getBusinessCard(position)?.let { businesscard ->
                viewModel.beginPendingDelete(businesscard)
            }
        }
        else{
            listAdapter?.notifyDataSetChanged()
        }
    }

    private fun deleteBusinessCards(){
        viewModel.setStateEvent(
            CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = DELETE_BUSINESS_CARDS_ARE_YOU_SURE,
                        uiComponentType = UIComponentType.AreYouSureDialog(
                            object : AreYouSureCallback {
                                override fun proceed() {
                                    viewModel.deleteBusinessCards()
                                }

                                override fun cancel() {
                                    // do nothing
                                }
                            }
                        ),
                        messageType = MessageType.Info()
                    )
                )
            )
        )
    }

    private fun setupSearchView(){

        val searchViewToolbar: Toolbar? = toolbar_content_container
            .findViewById<Toolbar>(R.id.searchview_toolbar)

        searchViewToolbar?.let { toolbar ->

            val searchView = toolbar.findViewById<SearchView>(R.id.search_view)

            val searchPlate: SearchView.SearchAutoComplete?
                    = searchView.findViewById(androidx.appcompat.R.id.search_src_text)

            // can't use QueryTextListener in production b/c can't submit an empty string
            when{
                androidTestUtils.isTest() -> {
                    searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            viewModel.setQuery(query)
                            startNewSearch()
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return true
                        }

                    })
                }

                else ->{
                    searchPlate?.setOnEditorActionListener { v, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                            || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                            val searchQuery = v.text.toString()
                            viewModel.setQuery(searchQuery)
                            startNewSearch()
                        }
                        true
                    }
                }
            }
        }
    }

    private fun setupFAB(){
        add_new_businesscard_fab.setOnClickListener {
            uiController.displayInputCaptureDialog(
                getString(com.aligmohammad.nadsoftmvvm.R.string.text_enter_a_title),
                object: DialogInputCaptureCallback{
                    override fun onTextCaptured(text: String) {
                        val newBusinessCard = viewModel.createNewBusinessCard(title = text)
                        viewModel.setStateEvent(
                            InsertNewBusinessCardEvent(
                                title = newBusinessCard.title
                            )
                        )
                    }
                }
            )
        }
    }

    private fun startNewSearch(){
        printLogD("DCM", "start new search")
        viewModel.clearList()
        viewModel.loadFirstPage()
    }

    private fun setupSwipeRefresh(){
        swipe_refresh.setOnRefreshListener {
            startNewSearch()
            swipe_refresh.isRefreshing = false
        }
    }

    private fun setupFilterButton(){
        val searchViewToolbar: Toolbar? = toolbar_content_container
            .findViewById<Toolbar>(R.id.searchview_toolbar)
        searchViewToolbar?.findViewById<ImageView>(R.id.action_filter)?.setOnClickListener {
            showFilterDialog()
        }
    }

    fun showFilterDialog(){

        activity?.let {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_filter)

            val view = dialog.getCustomView()

            val filter = viewModel.getFilter()
            val order = viewModel.getOrder()

            view.findViewById<RadioGroup>(R.id.filter_group).apply {
                when (filter) {
                    BUSINESS_CARD_FILTER_DATE_CREATED -> check(R.id.filter_date)
                    BUSINESS_CARD_FILTER_TITLE -> check(R.id.filter_title)
                }
            }

            view.findViewById<RadioGroup>(R.id.order_group).apply {
                when (order) {
                    BUSINESS_CARD_ORDER_ASC -> check(R.id.filter_asc)
                    BUSINESS_CARD_ORDER_DESC -> check(R.id.filter_desc)
                }
            }

            view.findViewById<TextView>(R.id.positive_button).setOnClickListener {

                val newFilter =
                    when (view.findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId) {
                        R.id.filter_title -> BUSINESS_CARD_FILTER_TITLE
                        R.id.filter_date -> BUSINESS_CARD_FILTER_DATE_CREATED
                        else -> BUSINESS_CARD_FILTER_DATE_CREATED
                    }

                val newOrder =
                    when (view.findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId) {
                        R.id.filter_desc -> "-"
                        else -> ""
                    }

                viewModel.apply {
                    saveFilterOptions(newFilter, newOrder)
                    setBusinessCardFilter(newFilter)
                    setBusinessCardOrder(newOrder)
                }

                startNewSearch()

                dialog.dismiss()
            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}










































