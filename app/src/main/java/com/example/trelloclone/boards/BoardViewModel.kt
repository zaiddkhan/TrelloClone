package com.example.trelloclone.boards

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trelloclone.ui.googlelogin.GoogleAuthClient
import com.example.trelloclone.ui.profile.FirebaseClient
import com.example.trelloclone.ui.theme.LightBlue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
  val  firebaseClient: FirebaseClient,
   val  googleAuthClient: GoogleAuthClient
): ViewModel(){

    var cardName = mutableStateOf("")
    var selectedBoardColor  = mutableStateOf(LightBlue)
    var boardName = mutableStateOf("")
    var boardPrivacy = mutableStateOf("")
    var listName = mutableStateOf("")
    var boardDetail = mutableStateOf(Board())
    var boardMadeBy = mutableStateOf("")

//    init {
//        if(googleAuthClient.auth.currentUser != null){
//            if(googleAuthClient.auth.currentUser!!.email != null) {
//                boardMadeBy.value = googleAuthClient.auth.currentUser!!.email!!
//            }
//        }
//    }

    fun resetData(){
        boardPrivacy.value = ""
        boardPrivacy.value = ""
    }

    fun addListToDatabase(list: BoardList){
        viewModelScope.launch {
            boardDetail.value.lists.add(list)
            firebaseClient.updateBoard(boardDetail.value)
        }
    }
    fun addCardsToList(card: BoardCard,list: BoardList){
        viewModelScope.launch {
            val index = boardDetail.value.lists.indexOf(list)
            boardDetail.value.lists[index].cards.add(card)
            firebaseClient.updateBoard(boardDetail.value)
        }
    }


    fun getBoardByName(name: String){



        firebaseClient.getBoardByName(name,{ boardDetail.value = it })

    }




    suspend fun addBoard() : Boolean{

        var success = false

            val board = Board(
                name = boardName.value,
                privacy = boardPrivacy.value,
                color = selectedBoardColor.value.toArgb(),
                madeBy = boardMadeBy.value
            )

             success  = withContext(Dispatchers.Main){
                 async {
                     firebaseClient.addBoardToDatabase(board)
                 }.await()
             }



        Log.d("value",success.toString())
        return success
    }
}