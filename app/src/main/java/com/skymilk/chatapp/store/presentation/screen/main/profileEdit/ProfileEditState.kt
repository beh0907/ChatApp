package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import kotlinx.coroutines.launch

sealed class ProfileEditState {
    data object Initial : ProfileEditState()
    data object Loading : ProfileEditState()
    data object Success : ProfileEditState()
    data object Error : ProfileEditState()
}
