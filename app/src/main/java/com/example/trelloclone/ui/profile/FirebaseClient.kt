package com.example.trelloclone.ui.profile

import android.net.Uri
import android.util.Log
import com.example.trelloclone.boards.Board
import com.example.trelloclone.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await


class FirebaseClient() {

    private val auth = Firebase.auth
    private val currentUserEmail = if(auth.currentUser != null) auth.currentUser!!.email else ""
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference("images/$currentUserEmail")

    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("Users")
    val boardsRef = db.collection("Boards")

    suspend fun addBoardToDatabase(board: Board) : Boolean{
        try {
            boardsRef.document().set(board).addOnCompleteListener {

            }.await()
            return true
        }catch (e : Exception){
            return false
        }
    }

    fun getUsersList(result : (List<User>) -> Unit){
        userRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                var list = mutableListOf<User>()
                for(document in it.result){
                    val user = document.toObject(User::class.java)
                    list.add(user)
                }
                result(list)
            }
        }
    }



      suspend fun addDataToFirebase(user: User) :Boolean {
          try {
                  storageRef.putFile(Uri.parse(user.imageUrl)).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener {
                            val user = user.copy(imageUrl = it.toString())
                            userRef.document().set(user)
                      }
                  }.await()
                  return true

          }catch (e : Exception){
              Log.d("error",e.message!!)
              return false
          }
      }


     fun checkIfUserExists(result : (User?) -> Unit) {
        var user : User? = null
        userRef.get().addOnSuccessListener {

            if(it.documents.size > 0) {
                val email = if (auth.currentUser != null) auth.currentUser!!.email else ""

                userRef.whereEqualTo("email", email).get().addOnSuccessListener { document ->
                        if (!document.documents.isEmpty()) {
                            val username = document.documents.get(0).getString("userName")!!
                            val email = document.documents.get(0).getString("email")!!
                            val imgUrl = document.documents.get(0).getString("imageUrl")!!
                            user = User(username,email = email, imageUrl = imgUrl)
                            result(user)
                        }

                }
            }
        }
    }
    suspend fun getBoardsList(): Flow<List<Board>> = flow {

        val querySnapshot = boardsRef.whereEqualTo("madeBy", auth.currentUser!!.email).get().await()

        val boardsList = querySnapshot.documents.mapNotNull { document ->
            document.toObject(Board::class.java)
        }

        emit(boardsList)
    }

     fun getBoardByName(name : String,onComplete:(Board) -> Unit)  {
        val query = boardsRef.whereEqualTo("name",name).get().addOnCompleteListener {
            if(it.isSuccessful){
               onComplete(it.result.documents.get(0).toObject(Board::class.java)!!)
            }
        }
    }

    suspend fun updateBoard(board: Board) : Boolean{
        try {
            boardsRef.document(board.documentId).set(board).await()
            return true
        }catch (e : Exception){
            return false
        }
    }
}

