package com.skymilk.chatapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.memoryCacheSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.skymilk.chatapp.BuildConfig
import com.skymilk.chatapp.store.data.remote.FcmApi
import com.skymilk.chatapp.store.data.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //파이어베이스 인증 초기화
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    //파이어베이스 데이터베이스 초기화
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val firebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        firebaseDatabase.setPersistenceEnabled(true)
        return firebaseDatabase
    }

    //파이어베이스 파이어스토어 초기화
    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore {
        val fireStore = Firebase.firestore
        fireStore.firestoreSettings = firestoreSettings {
            // 메모시 캐시 사용
            setLocalCacheSettings(memoryCacheSettings {})

            // 지속성 디스크 캐시 사용
            setLocalCacheSettings(persistentCacheSettings {})
        }

        //인덱스 자동 생성
        fireStore.persistentCacheIndexManager?.apply {
            enableIndexAutoCreation()
        }
        return fireStore
    }

    //파이어베이스 스토리지 초기화
    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance().reference

    //파이어베이스 메시지 초기화
    @Provides
    @Singleton
    fun provideFirebaseMessaging() = FirebaseMessaging.getInstance()

    // Retrofit 객체 생성
    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        //디버그 모드일 경우에만 로그를 남긴다
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideFcmApi(
        okHttpClient: OkHttpClient
    ): FcmApi {
        return Retrofit.Builder().baseUrl(Constants.FCM_SERVER_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(FcmApi::class.java)
    }


//    @Provides
//    @Singleton
//    fun provideRetrofit(): FcmApi {
//        return Retrofit.Builder()
//            .baseUrl(Constants.FCM_SERVER_URL)
//            .build()
//            .create(FcmApi::class.java)
//    }
}