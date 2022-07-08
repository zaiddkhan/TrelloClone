package com.example.trelloclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloclone.databinding.ActivityMyProfileBinding
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.BaseActivity
import com.example.trelloclone.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private lateinit var mUserDetails:User
    private var mSelectedImageUri : Uri? = null
    private var mProfileImageUrl : String = ""

    val selectPictureLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        mSelectedImageUri = it

        try {
            Glide
                .with(this)
                .load(mSelectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(binding.ivUserImage)
        }catch (e:IOException){
            e.printStackTrace()
        }


    }

    private lateinit var binding : ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpActionBar()
        FirestoreClass().loadUserData(this)

        binding.ivUserImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                showImageChooser()

            }else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnUpdate.setOnClickListener {
            if(mSelectedImageUri != null)
                uploadUserImage()
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()

            }else{
                Toast.makeText(this, "You denied the permissions for storage, you can still give it from the settings :)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showImageChooser(){
       selectPictureLauncher.launch("image/*")

    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title = resources.getString(R.string.my_profile)

        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
     fun setUserDataINUI(user:User){

         mUserDetails = user

         Glide
             .with(this)
             .load(user.image)
             .centerCrop()
             .placeholder(R.drawable.ic_user_placeholder)
             .into(binding.ivUserImage)

         binding.etName.setText(user.name)
         binding.etEmail.setText(user.email)
         if(user.mobile != null)
             binding.etMobile.setText(user.mobile.toString())
     }

    public fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

       if(mSelectedImageUri != null){
           val sRef : StorageReference = FirebaseStorage
               .getInstance()
               .reference
               .child("USER_IMAGE"+
                       System.currentTimeMillis()+
                       "."+
                       getFileExtension(mSelectedImageUri))

           sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
              taskSnapshot ->
               hideProgressDialog()
               Log.i(
                   "Firebase Image Url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
               )

               taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                   uri ->
                   Log.i("Downloadable Image Url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                   mProfileImageUrl = uri.toString()

                   updateUserProfileData()
               }
           }.addOnFailureListener{
               exception ->
               Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
               hideProgressDialog()
           }

       }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()

        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageUrl
        }

        if(binding.etName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding.etName.text.toString()
        }

        if(binding.etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString()
        }


        FirestoreClass().updateUserProfileData(this,userHashMap)

    }

    private fun getFileExtension(uri:Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}