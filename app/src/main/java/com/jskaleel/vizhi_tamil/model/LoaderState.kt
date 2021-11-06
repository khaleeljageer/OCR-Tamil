package com.jskaleel.vizhi_tamil.model

sealed interface LoaderState {
    object INIT : LoaderState
    object VERIFY : LoaderState
    object DOWNLOAD : LoaderState
    object READY : LoaderState
    object FAILURE: LoaderState
    object NONETWORK: LoaderState
}
