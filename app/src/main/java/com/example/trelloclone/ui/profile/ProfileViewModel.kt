package com.example.trelloclone.ui.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trelloclone.boards.Board
import com.example.trelloclone.data.User
import com.example.trelloclone.ui.googlelogin.GoogleAuthClient
import com.example.trelloclone.utils.toTrelloUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val googleAuthClient: GoogleAuthClient,
    val firestoreClient: FirebaseClient
) :ViewModel() {

    var currentUser = mutableStateOf<User?>(null)
    val userName = mutableStateOf("")
    var imageUrl = mutableStateOf("")
    val userEmail = mutableStateOf("")
    var isUploading = mutableStateOf(false)

    private val _state = MutableStateFlow(FirebaseUserState())
    val state = _state.asStateFlow()

    private val _boardsList = mutableStateOf<List<Board>>(emptyList())
    val boardsList: State<List<Board>> = _boardsList



    fun fetchBoardsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = firestoreClient.getBoardsList()
            result.collect{
                _boardsList.value = it
            }
        }
    }

    fun getUserData() {

            if (googleAuthClient.getSignedInUser()?.imageUrl != null) {
                currentUser.value = googleAuthClient.getSignedInUser()!!.toTrelloUser()
            } else {
                firestoreClient.checkIfUserExists {

                    if (it != null) {
                        currentUser.value = it
                    } else {
                    }
                }

            }

    }


    init {
        viewModelScope.launch {

            if (googleAuthClient.getSignedInUser()?.imageUrl != null) {

                currentUser.value = googleAuthClient.getSignedInUser()!!.toTrelloUser()
            } else {

                firestoreClient.checkIfUserExists {

                    if (it != null) {
                        currentUser.value = it
                        Log.d("currentuser", "notnull")
                    } else {
                        Log.d("currentuser", "null")

                    }
                }

            }
        }
    }
     suspend fun saveUserData():Boolean {
              var success = false

              _state.update {
                  it.copy(
                      true
                  )
              }
              userEmail.value = googleAuthClient.auth.currentUser!!.email.toString()
              val user = User(userName = userName.value, imageUrl = imageUrl.value, userEmail.value)

              withContext(Dispatchers.Main) {
                  async {
                      success = firestoreClient.addDataToFirebase(user)
                  }.await()
              }
          Log.d("success",success.toString())
         getUserData()
          return success
      }
}





