package com.skymilk.chatapp

sealed interface MainEvent {
    data object GetTheme : MainEvent

    data object GetRefuseIgnoringOptimization : MainEvent

    data object SetRefuseIgnoringOptimization : MainEvent
}