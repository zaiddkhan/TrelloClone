package com.example.trelloclone.boards

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.GridLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapters.CardMemberListItemsAdapter
import com.example.trelloclone.adapters.MembersActivityAdapter
import com.example.trelloclone.databinding.ActivityCardDetailsBinding
import com.example.trelloclone.dialog.LabelColorListDialog
import com.example.trelloclone.dialog.MembersListDialog
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.models.*
import com.example.trelloclone.utils.BaseActivity
import com.example.trelloclone.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board
    private  var mTaskListPosition = -1
    private var mCardListPosition = -1
    private lateinit var binding:ActivityCardDetailsBinding
    private var mSelectedColor = ""
    private lateinit var mMembersDetailsList :ArrayList<User>
    private var mSelectedDueDateMillis:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        setUpActionBar()

        binding.etNameCardDetails.setText( mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardListPosition].name)

        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }
        binding.btnUpdateCardDetails.setOnClickListener { 
            if(binding.etNameCardDetails.text.toString().isNotEmpty())
                updateCardDetails()
            else
                Toast.makeText(this, "Enter a card name", Toast.LENGTH_SHORT).show()
        }
        binding.tvSelectLabelColor.setOnClickListener {
            labelColorListDialog()
        }
        mSelectedDueDateMillis = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].dueDate

        if(mSelectedDueDateMillis > 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMillis))
            binding.tvSelectDueDate.text = selectedDate
        }

        binding.tvSelectDueDate.setOnClickListener {
            showDatePicker()
        }
        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }
        setUpSelectedMembers()
    }

    private fun deleteCard(){
        val cardList:ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardList.removeAt(mCardListPosition)

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardList


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)

    }

  private  fun alertDialogForDelete(cardName:String) {
       val builder = AlertDialog.Builder(this)
       builder.setTitle("Alert")
       builder.setMessage("Are you sure you want to delete $cardName")
    builder.setIcon(R.drawable.ic_delete)
    builder.setPositiveButton("Yes") { dialogInterface, _ ->
        dialogInterface.dismiss()
        deleteCard()
    }
    builder.setNegativeButton("No") { dialogInterface, _ ->
        dialogInterface.dismiss()
    }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

    private fun colorsList():ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor(){
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card -> {
                alertDialogForDelete(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateCardDetails(){
        val card = Card(binding.etNameCardDetails.text.toString(),
        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo,
        mSelectedColor,mSelectedDueDateMillis)

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card
        
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)

            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name
        }

        binding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo

        if(cardAssignedMembersList.size > 0){
            for(i in mMembersDetailsList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailsList[i].id == j){
                        mMembersDetailsList[i].selected = true
                    }
                }
            }
        }else{
            for(i in mMembersDetailsList.indices){
                        mMembersDetailsList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(this,mMembersDetailsList,resources.getString(R.string.select_members)) {
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.add(user.id)
                    }
                }else{
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.remove(user.id)

                        for(i in mMembersDetailsList.indices){
                            if(mMembersDetailsList[i].id == user.id) {
                                mMembersDetailsList[i].selected = false
                            }
                        }
                }
                setUpSelectedMembers()
            }
        }
        listDialog.show()
    }

    private fun labelColorListDialog(){
        val colorsList:ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(this,colorsList,resources.getString(R.string.str_select_label_color),mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!

        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardListPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }

        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }


    private fun setUpSelectedMembers(){
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo

        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailsList.indices){
            for(j in cardAssignedMembersList){
                if(mMembersDetailsList[i].id == j){
                    val selectedMember = SelectedMembers(mMembersDetailsList[i].id,mMembersDetailsList[i].image)
                    selectedMembersList.add(selectedMember)

                }
            }
        }
        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE

            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this,6)

            val adapter = CardMemberListItemsAdapter(this,selectedMembersList,true)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(
                object:CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }

                }
            )
        }else{
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility  = View.GONE
        }

    }


    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener{
                view,year,monthOfYear,dayOfMonth ->
           val sDayOfMonth = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
           val sMonthOfYear =
               if((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear+1}"
          val selecteddate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding.tvSelectDueDate.text = selecteddate

                val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
                val theDate = sdf.parse(selecteddate)
                mSelectedDueDateMillis = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }

}