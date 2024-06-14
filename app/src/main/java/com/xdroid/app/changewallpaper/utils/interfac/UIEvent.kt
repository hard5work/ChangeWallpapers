package com.xdroid.app.changewallpaper.utils.interfac

sealed interface UiEvent {
    object InteractionOne : UiEvent
    data class InteractionTwo(val value: String) : UiEvent
}