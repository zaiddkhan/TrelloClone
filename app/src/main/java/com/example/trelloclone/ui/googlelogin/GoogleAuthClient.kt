package com.example.trelloclone.ui.googlelogin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.trelloclone.utils.Constants
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.concurrent.CancellationException

class GoogleAuthClient(
    private val context : Context,
    private val oneTapClient: SignInClient
) {

     val auth = Firebase.auth

    suspend fun signIn():IntentSender? {
        val result = try {
          oneTapClient.beginSignIn(beginSignInRequest()).await()
        }catch (e : Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent) : SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken

        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)
        return try{
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    GoogleUserData(
                        userId = uid,
                        username = displayName,
                        imageUrl = photoUrl?.toString(),
                        email
                    )
                },
                errorMessage = null
            )
        }catch (e : Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                null,
                e.message
            )
        }
    }

    private fun beginSignInRequest(): BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(Constants.WEB_API_KEY)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun getSignedInUser(): GoogleUserData? = auth.currentUser?.run {
        GoogleUserData(
            userId = uid,
            username = displayName,
            imageUrl = photoUrl?.toString(),
            email = email
        )
    }

    fun registerUsingEmailAndPassword(email:String,password:String):Boolean {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                return@addOnCompleteListener
            }
        }
        return true
     }

    fun signInUserWithEmailAndPassword(email: String,password: String) : Boolean{
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                return@addOnCompleteListener
            }
        }
        return true
    }
}

