package com.example.trelloclone.firestore

import android.app.Activity
import android.widget.Toast
import com.example.trelloclone.MainActivity
import com.example.trelloclone.MyProfileActivity
import com.example.trelloclone.boards.CardDetailsActivity
import com.example.trelloclone.boards.CreateBoardActivity
import com.example.trelloclone.boards.MembersActivity
import com.example.trelloclone.boards.TaskListActivity
import com.example.trelloclone.logins.SignInActivity
import com.example.trelloclone.logins.SignUpActivity
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.Task
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity,userInfo: User){

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }

    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                when(activity){
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                e ->
                when(activity){
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun createBoard(activity:CreateBoardActivity ,board:Board){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener{
                exception ->
                Toast.makeText(activity, "Board creation failed", Toast.LENGTH_SHORT).show()
            }

    }

   fun loadUserData(activity: Activity,readBoardsList:Boolean = false){

       mFirestore.collection(Constants.USERS)
           .document(getCurrentUserId())
           .get()
           .addOnSuccessListener {document ->
               val loggedInUser = document.toObject(User::class.java)!!
               when(activity){
                   is SignInActivity -> {
                       activity.signInSuccess(loggedInUser)

                   }
                   is MainActivity -> {
                       activity.updateNavigationDetails(loggedInUser,readBoardsList)
                   }
                   is MyProfileActivity -> {
                       activity.setUserDataINUI(loggedInUser)
                   }

               }

           }
           .addOnFailureListener {
               when(activity){
                   is SignInActivity -> {
                       activity.hideProgressDialog()

                   }
                   is MainActivity -> {
                       activity.hideProgressDialog()
                   }

               }

               Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
           }

   }

    fun getBoardsList(activity:MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                val boardList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }

    }

    fun getBoardDetails(activity:TaskListActivity,documentId:String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->

                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)

            }
    }

    fun addUpdateTaskList(activity: Activity,board:Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                when(activity){
                    is TaskListActivity -> {
                        activity.addUpdateTaskListSuccess()
                    }
                    is CardDetailsActivity -> {
                        activity.addUpdateTaskListSuccess()
                    }
                }

            }
            .addOnFailureListener {
                exception ->
                when(activity)
                {
                    is TaskListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CardDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

            }
    }

    fun getAssignedUsers(activity:Activity,assignedTo:ArrayList<String>){
        mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                val usersList : ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                when(activity){
                    is MembersActivity -> {
                        activity.setUpMembersList(usersList)
                    }
                    is TaskListActivity -> {
                        activity.boardMembersDetailsList(usersList)
                    }
                }
            }

    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity,"error", Toast.LENGTH_SHORT).show()
            }
    }


    fun assignMemberToBoard(activity: MembersActivity,board:Board,user:User){
        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
            }

    }



    fun getCurrentUserId():String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null)
            currentUserId = currentUser.uid
        return currentUserId
    }


}