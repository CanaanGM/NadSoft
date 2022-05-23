package com.aligmohammad.nadsoftmvvm.framework.presentation

import android.app.Application
import com.aligmohammad.nadsoftmvvm.di.AppComponent
import com.aligmohammad.nadsoftmvvm.di.DaggerAppComponent
import kotlinx.coroutines.*

@FlowPreview
@ExperimentalCoroutinesApi
open class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    open fun initAppComponent(){
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
    }


}