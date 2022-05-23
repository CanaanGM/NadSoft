package com.aligmohammad.nadsoftmvvm.framework.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aligmohammad.nadsoftmvvm.R
import com.aligmohammad.nadsoftmvvm.business.domain.state.DialogInputCaptureCallback
import com.aligmohammad.nadsoftmvvm.framework.datasource.network.implementation.BusinessCardFirestoreServiceImpl.Companion.EMAIL
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.BaseBusinessCardFragment
import com.aligmohammad.nadsoftmvvm.util.printLogD
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class SplashFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseBusinessCardFragment(R.layout.fragment_splash) {

    val viewModel: SplashViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkFirebaseAuth()
    }

    private fun checkFirebaseAuth(){
        if(FirebaseAuth.getInstance().currentUser == null){
            displayCapturePassword()
        }
        else{
            subscribeObservers()
        }
    }

    // add password input b/c someone used my firestore and deleted the data
    private fun displayCapturePassword(){
        uiController.displayInputCaptureDialog(
            getString(R.string.text_enter_password),
            object: DialogInputCaptureCallback {
                override fun onTextCaptured(text: String) {
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(EMAIL, text)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                printLogD("MainActivity",
                                    "Signing in to Firebase: ${it.result}")
                                subscribeObservers()
                            }
                            else{
                                printLogD("MainActivity", "cannot log in")
                            }
                        }
                }
            }
        )
    }

    private fun subscribeObservers(){
        viewModel.hasSyncBeenExecuted().observe(viewLifecycleOwner, Observer { hasSyncBeenExecuted ->

            if(hasSyncBeenExecuted){
                navBusinessCardListFragment()
            }
        })
    }

    private fun navBusinessCardListFragment(){
        findNavController().navigate(R.id.action_splashFragment_to_businesscardListFragment)
    }

    override fun inject() {
        getAppComponent().inject(this)
    }

}




























