package com.jskaleel.ocr_tamil.model

sealed interface LoaderState {
    object INIT : LoaderState
    object VERIFY : LoaderState
    object DOWNLOAD : LoaderState
    object READY : LoaderState
    object FAILURE: LoaderState
}
