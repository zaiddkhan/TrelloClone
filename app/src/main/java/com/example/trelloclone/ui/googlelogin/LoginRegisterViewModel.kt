package com.example.trelloclone.ui.googlelogin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginRegisterViewModel @Inject constructor(
    val googleAuthClient: GoogleAuthClient
) : ViewModel(){

    var isCreatingAccount = mutableStateOf(false)
    var password = mutableStateOf("")
    var email = mutableStateOf("")
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update {
            it.copy(
            isSignInSuccessful = true,
            signInError = result.errorMessage
        )
        }
    }

    fun createUserWithEmailAndPassword(){
        if(googleAuthClient.registerUsingEmailAndPassword(email.value, password.value)) {
            _state.update {
                it.copy(
                    isSignInSuccessful = true,
                    signInError = ""
                )
            }
            isCreatingAccount.value = false
        }
    }

    fun resetEmailAndPass(){
        email.value = ""
        password.value = ""
    }
    fun signInUserWithEmailAndPassword(){
       if( googleAuthClient.signInUserWithEmailAndPassword(email.value, password.value)) {
           _state.update {
               it.copy(
                   isSignInSuccessful = true,
                   signInError = ""
               )
           }
           isCreatingAccount.value = false

       }
    }

    fun getCurrentUser() = googleAuthClient.auth.currentUser

    fun resetState(){
        _state.update { SignInState() }
    }
}