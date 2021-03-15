package com.example.firebasechat.di

import android.content.Context
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideUserSharedPreferences(@ApplicationContext appContext: Context): UserSharedPreferenceLiveData =
        UserSharedPreferenceLiveData(appContext)

    @Singleton
    @Provides
    fun providePicasso(@ApplicationContext appContext: Context): Picasso =
        Picasso.Builder(appContext)
            .downloader(OkHttp3Downloader(appContext, Int.MAX_VALUE.toLong()))
            .build()
}