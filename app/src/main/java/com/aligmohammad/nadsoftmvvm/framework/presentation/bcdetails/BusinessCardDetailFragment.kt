package com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aligmohammad.nadsoftmvvm.R
import com.aligmohammad.nadsoftmvvm.R.drawable
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.state.*
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.UpdateBusinessCard.Companion.UPDATE_FAILED_PK
import com.aligmohammad.nadsoftmvvm.business.interactors.businesscarddetail.UpdateBusinessCard.Companion.UPDATE_SUCCESS
import com.aligmohammad.nadsoftmvvm.business.interactors.common.DeleteBusinessCard.Companion.DELETE_ARE_YOU_SURE
import com.aligmohammad.nadsoftmvvm.business.interactors.common.DeleteBusinessCard.Companion.DELETE_NOTE_SUCCESS
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardDetailStateEvent.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardDetailViewState
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.BusinessCardInteractionState.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state.CollapsingToolbarState.*
import com.aligmohammad.nadsoftmvvm.framework.presentation.businesscardlist.NOTE_PENDING_DELETE_BUNDLE_KEY
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_business_card_detail.*
import kotlinx.android.synthetic.main.layout_business_card_detail_toolbar.*
import kotlinx.coroutines.*

const val BC_DETAIL_STATE_BUNDLE_KEY =
    "com.aligmohammad.nadsoftmvvm.framework.presentation.bcdetails.state"


@FlowPreview
@ExperimentalCoroutinesApi
class BusinessCardDetailFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseBusinessCardFragment(R.layout.fragment_business_card_detail) {

    val viewModel: BusinessCardDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupOnBackPressDispatcher()
        subscribeObservers()

        container_due_date.setOnClickListener {
            // TODO("handle click of due date")
        }

        businesscard_title.setOnClickListener {
            onClick_businesscardTitle()
        }

        businesscard_body.setOnClickListener {
            onClick_businesscardBody()
        }

        setupMarkdown()
        getSelectedBusinessCardFromPreviousFragment()
        restoreInstanceState()
    }

    private fun onErrorRetrievingBusinessCardFromPreviousFragment() {
        viewModel.setStateEvent(
            CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = BUSINESS_DETAIL_ERROR_RETRIEVEING_SELECTED_BC,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
        )
    }

    private fun setupMarkdown() {
        activity?.run {
            // IF YOU WANT TO PROCESS THE MARKDOWN TO BE BEAUTIFUL

        }
    }

    private fun onClick_businesscardTitle() {
        if (!viewModel.isEditingTitle()) {
            updateBodyInViewModel()
            updateBusinessCard()
            viewModel.setBusinessCardInteractionTitleState(EditState())
        }
    }

    private fun onClick_businesscardBody() {
        if (!viewModel.isEditingBody()) {
            updateTitleInViewModel()
            updateBusinessCard()
            viewModel.setBusinessCardInteractionBodyState(EditState())
        }
    }

    private fun onBackPressed() {
        view?.hideKeyboard()
        if (viewModel.checkEditState()) {
            updateBodyInViewModel()
            updateTitleInViewModel()
            updateBusinessCard()
            viewModel.exitEditState()
            displayDefaultToolbar()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()
        updateTitleInViewModel()
        updateBodyInViewModel()
        updateBusinessCard()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            if (viewState != null) {

                viewState.businesscard?.let { businesscard ->
                    setBusinessCardTitle(businesscard.title)
                    setBusinessCardBody(businesscard.body)
                }
            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            uiController.displayProgressBar(it)
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->

            stateMessage?.response?.let { response ->

                when (response.message) {

                    UPDATE_SUCCESS -> {
                        viewModel.setIsUpdatePending(false)
                        viewModel.clearStateMessage()
                    }

                    DELETE_NOTE_SUCCESS -> {
                        viewModel.clearStateMessage()
                        onDeleteSuccess()
                    }

                    else -> {
                        uiController.onResponseReceived(
                            response = stateMessage.response,
                            stateMessageCallback = object : StateMessageCallback {
                                override fun removeMessageFromStack() {
                                    viewModel.clearStateMessage()
                                }
                            }
                        )
                        when (response.message) {

                            UPDATE_FAILED_PK -> {
                                findNavController().popBackStack()
                            }

                            BUSINESS_DETAIL_ERROR_RETRIEVEING_SELECTED_BC -> {
                                findNavController().popBackStack()
                            }

                            else -> {
                                // do nothing
                            }
                        }
                    }
                }
            }

        })

        viewModel.collapsingToolbarState.observe(viewLifecycleOwner, Observer { state ->

            when (state) {

                is Expanded -> {
                    transitionToExpandedMode()
                }

                is Collapsed -> {
                    transitionToCollapsedMode()
                }
            }
        })

        viewModel.businesscardTitleInteractionState.observe(viewLifecycleOwner, Observer { state ->

            when (state) {

                is EditState -> {
                    businesscard_title.enableContentInteraction()
                    view?.showKeyboard()
                    displayEditStateToolbar()
                    viewModel.setIsUpdatePending(true)
                }

                is DefaultState -> {
                    businesscard_title.disableContentInteraction()
                }
            }
        })

        viewModel.businesscardBodyInteractionState.observe(viewLifecycleOwner, Observer { state ->

            when (state) {

                is EditState -> {
                    businesscard_body.enableContentInteraction()
                    view?.showKeyboard()
                    displayEditStateToolbar()
                    viewModel.setIsUpdatePending(true)
                }

                is DefaultState -> {
                    businesscard_body.disableContentInteraction()
                }
            }
        })
    }

    private fun displayDefaultToolbar() {
        activity?.let { a ->
            toolbar_primary_icon.setImageDrawable(
                resources.getDrawable(
                    drawable.ic_arrow_back_grey_24dp,
                    a.application.theme
                )
            )
            toolbar_secondary_icon.setImageDrawable(
                resources.getDrawable(
                    drawable.ic_delete,
                    a.application.theme
                )
            )
        }
    }

    private fun displayEditStateToolbar() {
        activity?.let { a ->
            toolbar_primary_icon.setImageDrawable(
                resources.getDrawable(
                    drawable.ic_close_grey_24dp,
                    a.application.theme
                )
            )
            toolbar_secondary_icon.setImageDrawable(
                resources.getDrawable(
                    drawable.ic_done_grey_24dp,
                    a.application.theme
                )
            )
        }
    }

    private fun setBusinessCardTitle(title: String) {
        businesscard_title.setText(title)
    }

    private fun getBusinessCardTitle(): String {
        return businesscard_title.text.toString()
    }

    private fun getBusinessCardBody(): String {
        return businesscard_body.text.toString()
    }

    private fun setBusinessCardBody(body: String?) {
        businesscard_body.setText(body)
    }

    private fun getSelectedBusinessCardFromPreviousFragment() {
        arguments?.let { args ->
            (args.getParcelable(BUSINESS_DETAIL_SELECTED_BC_BUNDLE_KEY) as BusinessCard?)?.let { selectedBusinessCard ->
                viewModel.setBusinessCard(selectedBusinessCard)
            } ?: onErrorRetrievingBusinessCardFromPreviousFragment()
        }

    }

    private fun restoreInstanceState() {
        arguments?.let { args ->
            (args.getParcelable(BC_DETAIL_STATE_BUNDLE_KEY) as BusinessCardDetailViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)

                // One-time check after rotation
                if (viewModel.isToolbarCollapsed()) {
                    app_bar.setExpanded(false)
                    transitionToCollapsedMode()
                } else {
                    app_bar.setExpanded(true)
                    transitionToExpandedMode()
                }
            }
        }
    }

    private fun updateTitleInViewModel() {
        if (viewModel.isEditingTitle()) {
            viewModel.updateBusinessCardTitle(getBusinessCardTitle())
        }
    }

    private fun updateBodyInViewModel() {
        if (viewModel.isEditingBody()) {
            viewModel.updateBusinessCardBody(getBusinessCardBody())
        }
    }

    private fun setupUI() {
        businesscard_title.disableContentInteraction()
        businesscard_body.disableContentInteraction()
        displayDefaultToolbar()
        transitionToExpandedMode()

        app_bar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, offset ->

                if (offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD) {
                    updateTitleInViewModel()
                    if (viewModel.isEditingTitle()) {
                        viewModel.exitEditState()
                        displayDefaultToolbar()
                        updateBusinessCard()
                    }
                    viewModel.setCollapsingToolbarState(Collapsed())
                } else {
                    viewModel.setCollapsingToolbarState(Expanded())
                }
            })

        toolbar_primary_icon.setOnClickListener {
            if (viewModel.checkEditState()) {
                view?.hideKeyboard()
                viewModel.triggerBusinessCardObservers()
                viewModel.exitEditState()
                displayDefaultToolbar()
            } else {
                onBackPressed()
            }
        }

        toolbar_secondary_icon.setOnClickListener {
            if (viewModel.checkEditState()) {
                view?.hideKeyboard()
                updateTitleInViewModel()
                updateBodyInViewModel()
                updateBusinessCard()
                viewModel.exitEditState()
                displayDefaultToolbar()
            } else {
                deleteBusinessCard()
            }
        }
    }

    private fun deleteBusinessCard() {
        viewModel.setStateEvent(
            CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = DELETE_ARE_YOU_SURE,
                        uiComponentType = UIComponentType.AreYouSureDialog(
                            object : AreYouSureCallback {
                                override fun proceed() {
                                    viewModel.getBusinessCard()?.let { businesscard ->
                                        initiateDeleteTransaction(businesscard)
                                    }
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

    private fun initiateDeleteTransaction(businesscard: BusinessCard) {
        viewModel.beginPendingDelete(businesscard)
    }

    private fun onDeleteSuccess() {
        val bundle = bundleOf(NOTE_PENDING_DELETE_BUNDLE_KEY to viewModel.getBusinessCard())
        viewModel.setBusinessCard(null) // clear businesscard from ViewState
        viewModel.setIsUpdatePending(false) // prevent update onPause
        findNavController().navigate(
            R.id.action_businesscard_detail_fragment_to_businesscardListFragment,
            bundle
        )
    }

    private fun setupOnBackPressDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun updateBusinessCard() {
        if (viewModel.getIsUpdatePending()) {
            viewModel.setStateEvent(
                UpdateBusinessCardEvent()
            )
        }
    }

    private fun transitionToCollapsedMode() {
        businesscard_title.fadeOut()
        displayToolbarTitle(tool_bar_title, getBusinessCardTitle(), true)
    }

    private fun transitionToExpandedMode() {
        businesscard_title.fadeIn()
        displayToolbarTitle(tool_bar_title, null, true)
    }

    override fun inject() {
        getAppComponent().inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.getCurrentViewStateOrNew()
        outState.putParcelable(BC_DETAIL_STATE_BUNDLE_KEY, viewState)
        super.onSaveInstanceState(outState)
    }


}














