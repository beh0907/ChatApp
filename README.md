# <div align=center>ChatApp</div>
[Firebase](https://firebase.google.com/)를 활용한 kotlin 언어 기반의 채팅 안드로이드 어플리케이션입니다.

# 특징

* 파이어베이스의 기능들과 연계하여 기능 구현
* 기본 로그인과 소셜 로그인 지원
* 유저간의 실시간 채팅
* 그룹 채팅 지원

# 기술스택 및 라이브러리

* 최소 SDK 26 / 타겟 SDK 35
* kotlin 언어 기반, 비동기 처리를 위한 coroutine + Flow
* 종속성 주입을 위한 [Dagger Hilt](https://dagger.dev/hilt/)
* JetPack
  * Compose - Android의 현대적인 선언적 UI 툴킷으로, Kotlin 기반의 UI 개발을 통해 효율적이고 유연한 사용자 인터페이스 구축합니다.
  * LifeCycle - Android의 수명 주기를 관찰하고 수명 주기 변경에 따라 UI 상태를 처리합니다.
  * ViewModel - UI와 DATA 관련된 처리 로직을 분리합니다.
  * ViewBinding - View(Compose)와 코드(kotlin)간의 상호작용을 원활하게 처리합니다.
  * Navigation - [Compose Navigation](https://developer.android.com/develop/ui/compose/navigation?hl=ko)을 활용해 화면 전환시 Type-Safe한 인자를 전달하고 Notification과 연계하여 채팅방의 Deep Link를 구현합니다.
  * Permissions - [TedPermission](https://github.com/ParkSangGwon/TedPermission)을 활용해 알림과 저장장치에 관한 권한을 요청하고 처리합니다.
  * Notifications - 채팅 메시지 정보를 알리기 위해 FCM와 연동하여 알림을 표시합니다.
  * DataStore - SharedPreferences의 한계점을 개선한 라이브러리로 유저 및 채팅방 설정 정보를 관리합니다.
* Architecture
  * MVVM 패턴 적용 - Model + View + ViewModel
  * MVI 패턴 적용 - Model + View + Intent
  * Repository 패턴 적용 - Data + Domain + Presentation Layer 클린 아키텍처
* [Retrofit2](https://github.com/square/retrofit) - FCM HTTP v1 API를 통해 페이로드를 첨부하여 알림 메시지를 전송합니다.
* 소셜 로그인
  * [Google](https://github.com/googleapis/google-auth-library-java) - Google 로그인을 위한 OAuth 2.0 인증, 권한을 부여를 통해 Google 계정 로그인을 지원합니다.
  * [KaKao](https://developers.kakao.com/docs/latest/ko/kakaologin/common) - 커스텀 OIDC를 적용, Firebase Auth와 연동하여 카카오톡 및 계정 로그인을 지원합니다.
* Firebase
  * [Auth](https://firebase.google.com/docs/auth?hl=ko) - 유저의 인증을 관리하여 이메일/비밀번호 및 소셜 로그인과 연계하여 계정 관리
  * [FireStore](https://firebase.google.com/docs/firestore?hl=ko) - 구조화된 유저와 채팅방 정보를 관리하는 NoSQL 클라우드 데이터베이스
  * [Realtime Database](https://firebase.google.com/docs/database/android/start?hl=ko) - 실시간 채팅 정보와 상태 정보를 관리하는 NoSQL 클라우드 데이터베이스
  * [Storage](https://firebase.google.com/docs/storage/android/start?hl=ko) - 유저별 프로필, 채팅방 내 이미지를 저장하고 관리하는 스토리지 서비스
  * [Messaging](https://firebase.google.com/docs/cloud-messaging/android/client?hl=ko) - 현재 참여하지 않는 채팅방 내 실시간 채팅 정보를 제공하기 위한 알림 푸시 알림 서비스
* 이미지 관리
  * [Coil](https://coil-kt.github.io/coil/README-ko/) - Coroutine기반으로 효율적인 비동기 이미지를 로드하고 적용합니다.
  * [TedImagePicker](https://github.com/ParkSangGwon/TedImagePicker) - 갤러리로부터 복수의 이미지, 동영상을 선택하거나 카메라 기능과 연계하여 이미지를 업로드합니다.
  * [easycrop](https://github.com/mr0xf00/easycrop) - 프로필 이미지 업로드 시 자르기, 회전 등의 기능을 지원하고 적용합니다.
  * [Zoomable](https://github.com/usuiat/Zoomable) -  이미지 뷰어로서 이미지를 확대/축소하거나 복수의 이미지를 Pager로 제공합니다.
  * [Material Icon Extended](https://fonts.google.com/icons) - 기본으로부터 더욱 확장한 ImageVector 아이콘을 사용합니다.
* 로딩 상태
  * [Lottie Animation](https://github.com/airbnb/lottie-android) - 이미지 목록의 로딩 상태를 표시하기 위해 애니메이션 이미지를 적용합니다.

# 스크린샷


| 로딩 화면                                                                                     | 로그인 화면                                                                                     | 회원가입 화면                                                                                     |
| --------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| ![로딩 화면](https://github.com/user-attachments/assets/f14ce3e0-a509-45c3-ad14-c6ee96abc65f) | ![로그인 화면](https://github.com/user-attachments/assets/f335fe1e-7d27-462a-aa59-f0adf3569b65) | ![회원가입 화면](https://github.com/user-attachments/assets/5f7f1631-8e2c-4414-aaee-d1b71ec3f6a8) |


| 친구 목록 화면(기본 화면)                                                                         | 채팅방 목록 화면                                                                                     | 설정 화면                                                                                     |
| ------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| ![친구목록 화면](https://github.com/user-attachments/assets/2f618c2b-af85-4d86-bc4e-39206440e4b4) | ![채팅방 목록 화면](https://github.com/user-attachments/assets/545dba3f-3cba-413e-87f0-636b901fd8e9) | ![설정 화면](https://github.com/user-attachments/assets/9f89c0ab-0b77-47da-be7c-16b717f61dcd) |


| 나의 프로필 화면                                                                                     | 친구 프로필 화면                                                                                     | 프로필 편집 화면                                                                                     |
| ---------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| ![나의 프로필 화면](https://github.com/user-attachments/assets/48569a5e-bbe9-406f-a0df-092ca379704b) | ![친구 프로필 화면](https://github.com/user-attachments/assets/fe13d008-8a6a-4186-b616-9777d84f16c3) | ![프로필 편집 화면](https://github.com/user-attachments/assets/96b1506a-67bf-4dc1-acb9-60bb7c49c68e) |


| 채팅방 화면                                                                                     | 채팅방 드로어 화면                                                                                     | 이미지 페이저 화면                                                                                     |
| ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------ |
| ![채팅방 화면](https://github.com/user-attachments/assets/6dfb38e4-1cc5-4c3e-949a-03edafd60ea7) | ![채팅방 드로어 화면](https://github.com/user-attachments/assets/117dc811-83db-4eac-8be0-b17511a5f161) | ![이미지 페이저 화면](https://github.com/user-attachments/assets/d93155c1-df05-4e58-918b-a67938f4b190) |


| 유저 검색 화면                                                                                     | 대화상대 초대 화면                                                                                     |
| -------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| ![유저 검색 화면](https://github.com/user-attachments/assets/632c093f-f0eb-44d8-b96d-2f07f64a36d4) | ![대화상대 초대 화면](https://github.com/user-attachments/assets/9caf1e14-4163-4cec-81b6-ed45bd274831) |
