package com.example.trelloclone.di

import android.content.Context
import com.example.trelloclone.ui.googlelogin.GoogleAuthClient
import com.example.trelloclone.ui.profile.FirebaseClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.core.FirestoreClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun providesGoogleAuthClient(@ApplicationContext context: Context) : GoogleAuthClient {
        return GoogleAuthClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    @Provides
    fun provideFirestore() = FirebaseClient()

}