package com.skymilk.chatapp

sealed interface MainEvent {
    data object GetTheme : MainEvent
}