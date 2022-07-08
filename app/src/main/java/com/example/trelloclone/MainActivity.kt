package com.example.trelloclone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloclone.adapters.BoardItemsAdapter
import com.example.trelloclone.boards.CreateBoardActivity
import com.example.trelloclone.boards.TaskListActivity
import com.example.trelloclone.databinding.ActivityMainBinding
import com.example.trelloclone.databinding.NavHeaderMainBinding
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.logins.IntroActivity
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.BaseActivity
import com.example.trelloclone.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {

   companion object{
       const val MY_PROFILE_REQUEST_CODE =1
       const val CREATE_BOARD_REQUEST = 12
   }

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var rvBoardsList :RecyclerView
    private lateinit var tvNoBoards :TextView

    private lateinit var mUserName :String
    private lateinit var fabButton: FloatingActionButton
    private lateinit var userNameText : TextView
    private lateinit var toolbar :androidx.appcompat.widget.Toolbar
    private lateinit var binding:ActivityMainBinding
    private lateinit var userImage : CircleImageView


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        toolbar = findViewById(R.id.toolbar_main_activity)

        setUpActionBar()
        binding.navView.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)

        if(tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this,true)
        }else{
           val token=""
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
            })
            updateFcmToken(token)
        }
        FirestoreClass().loadUserData(this,true)



        fabButton = findViewById(R.id.fab_create_board)
        rvBoardsList = findViewById(R.id.rv_boards_list)
        tvNoBoards = findViewById(R.id.tv_no_boards_available)
        fabButton.setOnClickListener {
            val intent =Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST)
        }



    }

    fun populateBoardsListToUI(boardsList:ArrayList<Board>){
        hideProgressDialog()
        if(boardsList.size > 0){
            rvBoardsList.visibility = View.VISIBLE
            tvNoBoards.visibility = View.GONE

            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this,boardsList)
            rvBoardsList.adapter = adapter
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_iD,model.documentId)
                    startActivity(intent)

                }

            })

        }else{
            rvBoardsList.visibility = View.GONE
            tvNoBoards.visibility = View.VISIBLE


        }
    }


    private fun setUpActionBar(){
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_navigation_menu)
        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer(){
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
             R.id.nav_my_profile -> {
                 startActivityForResult(Intent(this,MyProfileActivity::class.java),
                     MY_PROFILE_REQUEST_CODE)
             }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE)
            FirestoreClass().loadUserData(this)
        else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST){
            FirestoreClass().getBoardsList(this)

        }
    }


    fun updateNavigationDetails(user:User,readBoardsList:Boolean){
        hideProgressDialog()
        mUserName = user.name

        userImage = findViewById(R.id.user_image)
        userNameText = findViewById(R.id.userName)
        Glide
            .with(this)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(userImage)

        userNameText.text = user.name
        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }

    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this,true)

    }
    private fun updateFcmToken(token:String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }
}