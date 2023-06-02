package com.example.trelloclone.carddetail

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.trelloclone.boards.Board
import com.example.trelloclone.boards.BoardCard
import com.example.trelloclone.boards.Checklist
import com.example.trelloclone.data.User
import com.example.trelloclone.ui.profile.FirebaseClient
import com.example.trelloclone.ui.theme.LightBlue
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
   val firebaseClient: FirebaseClient
) : ViewModel(){

    var selectedCardCover  = mutableStateOf(Color.Black)

    var listOfUsers = mutableStateOf<List<User>>(emptyList())
    var boardDetail = mutableStateOf(Board())
    @SuppressLint("NewApi")
    var boardCard = mutableStateOf(BoardCard())
    var checklists = mutableStateOf<List<Checklist>>(emptyList())

    fun addToCheckList(checklist: Checklist){
       checklists.value += checklist
        boardCard.value.checkLists = checklists.value
    }

    init {
        getUser()
    }

    fun getBoardByName(name: String){

        firebaseClient.getBoardByName(name,{ boardDetail.value = it })

    }

    fun updateDateAndTime(date: LocalDate,time: LocalTime){
        boardCard.value.dueDate = date
    }


    fun getUser(){
        firebaseClient.getUsersList {
            listOfUsers.value = it
        }
    }

}