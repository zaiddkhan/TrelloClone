package com.example.trelloclone.boards

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.trelloclone.data.User
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class Board(
    @DocumentId
    val documentId : String = "",
    val name : String = "",
    val color : Int = 0,
    val madeBy : String = "",
    var assignedTo : MutableList<String>  = mutableListOf<String>(),
    val privacy : String = "",
    var lists: MutableList<BoardList> = mutableListOf<BoardList>()
)

data class BoardList(
    var isEdited : Boolean = false,
    val listName : String = "",
    var cards : MutableList<BoardCard> = mutableListOf<BoardCard>()
)

@RequiresApi(Build.VERSION_CODES.O)
data class BoardCard (
    val name : String = "",
    var color: Int = 0,
    var dueDate : LocalDate = LocalDate.now(),
    val members : MutableList<User> = mutableListOf<User>(),
    var checkLists : List<Checklist> = mutableListOf()

)

data class Checklist(
    var isComplete : Boolean = false,
    val title : String = ""
)