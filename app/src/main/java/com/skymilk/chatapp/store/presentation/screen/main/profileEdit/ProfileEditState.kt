package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import kotlinx.coroutines.launch

sealed interface ProfileEditState {
    
    data object Initial : ProfileEditState // 기본 상태
    
    data object Loading : ProfileEditState // 프로필 업데이트 중
    
    data object Success : ProfileEditState // 프로필 업데이트 완료
    
    data object Error : ProfileEditState // 프로필 업데이트 에러
}
