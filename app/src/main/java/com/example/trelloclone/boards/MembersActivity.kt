package com.example.trelloclone.boards

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapters.MembersActivityAdapter
import com.example.trelloclone.databinding.ActivityMembersBinding
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.BaseActivity
import com.example.trelloclone.utils.Constants
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var binding:ActivityMembersBinding
    private lateinit var mBoardDetail : Board
    private lateinit var mAssignedMembersList:ArrayList<User>
    private var anyChangesMade:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpActionBar()

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
           mBoardDetail = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedUsers(this,mBoardDetail.assignedTo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {

            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()

            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this,email)
            }else{
                Toast.makeText(this, "Enter an email address", Toast.LENGTH_SHORT).show()
            }

        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title = "Members"

        }

        binding.toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun memberDetails(user:User){

        mBoardDetail.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetail,user)

    }

    fun setUpMembersList(list:ArrayList<User>){

        mAssignedMembersList = list
        hideProgressDialog()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MembersActivityAdapter(this,list)
        binding.rvMembersList.adapter = adapter

    }

    fun memberAssignedSuccess(user:User){
        anyChangesMade = true
        hideProgressDialog()
        mAssignedMembersList.add(user)
        setUpMembersList(mAssignedMembersList)
        SendNotificationTOUser(mBoardDetail.name,user.fcmToken).execute()
    }
    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
    private inner class SendNotificationTOUser(val boardName:String,val token:String):AsyncTask<Any,Void,String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }
        override fun doInBackground(vararg p0: Any?): String {
            var result:String=""
            var connection:HttpURLConnection?=null
            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")

                connection.setRequestProperty(Constants.FCM_AUTHORIZATION,"${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")

                connection.useCaches = false

                val wr  = DataOutputStream(connection.outputStream)
                val jsonRequest=JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE,"kaam karo jaldi ${boardName}")
                dataObject.put(Constants.FCM_KEY_MESSAGE,"time ke pehle hoajana chahiye, order by ${mAssignedMembersList[0].name}")

                jsonRequest.put(Constants.FCM_KEY_DATA,dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO,token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()
                val httpResult :Int=connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val sb = StringBuilder()
                    var line:String?
                    try{
                        while(reader.readLine().also{line=it} != null){
                            sb.append(line+"\n")
                        }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()

                }else{
                    result = connection.responseMessage
                }
            }catch (e:SocketTimeoutException){
                e.printStackTrace()
            }finally {
                connection?.disconnect()
            }

            return result

        }

        override fun onPostExecute(result: String?) {
            hideProgressDialog()
            super.onPostExecute(result)
        }

    }

}