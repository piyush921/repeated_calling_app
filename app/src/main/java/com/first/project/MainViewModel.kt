package com.first.project

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    companion object {
        const val LIGHT_THEME = 0
        const val DARK_THEME = 1
    }

    val themeState: MutableState<Int> = mutableStateOf(LIGHT_THEME)
    val contact: MutableState<String> = mutableStateOf("")

}