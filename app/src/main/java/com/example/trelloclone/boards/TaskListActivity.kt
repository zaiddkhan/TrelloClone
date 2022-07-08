package com.example.trelloclone.boards

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapters.TaskListItemAdapter
import com.example.trelloclone.databinding.ActivityTaskListBinding
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.Card
import com.example.trelloclone.models.Task
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.BaseActivity
import com.example.trelloclone.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var binding:ActivityTaskListBinding
    private lateinit var mBoardDocumentId:String
    lateinit var mAssignedMemberDetailsList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra(Constants.DOCUMENT_iD)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_iD) as String
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)
    }



    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title = mBoardDetails.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this,mBoardDocumentId)
        }

       super.onActivityResult(requestCode, resultCode, data)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_member -> {

                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun boardDetails(board:Board){

        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar()


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedUsers(this,mBoardDetails.assignedTo)

    }


    fun createTaskList(taskListName:String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    fun updateTaskList(position:Int,listName:String,model:Task){
        val task = Task(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        val cardAssignedUsersList : ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards

        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)


    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)

    }

    fun cardDetails(taskListPosition:Int,cardPosition:Int){
        val intent =Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailsList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailsList(list:ArrayList<User>){
        mAssignedMemberDetailsList = list
        hideProgressDialog()


        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this,mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter
    }

    fun updateCardInTaskList(taskListPosition:Int,cards:ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        mBoardDetails.taskList[taskListPosition].cards = cards
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE:Int=13
        const val CARD_DETAILS_REQUEST_CODE:Int=14
    }
}