package com.example.trelloclone.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.trelloclone.R
import com.example.trelloclone.ui.theme.LightBlue
import com.example.trelloclone.ui.theme.Pink80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    navController: NavHostController
) {


    var state = viewModel.state.collectAsState()
    var selectedImageUri by remember {
        mutableStateOf("")
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            selectedImageUri = it.toString()
            viewModel.imageUrl.value = selectedImageUri
        })
    Column(modifier = Modifier.fillMaxSize())
    {

        if (viewModel.isUploading.value) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = LightBlue
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(70.dp)
                                .align(CenterHorizontally),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Uploading your data",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .background(LightBlue)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 15.dp, top = 20.dp),
                text = "Edit Profile",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 15.dp, top = 20.dp)
                    .size(30.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "done",
                tint = Color.White
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            )
            {
                if (selectedImageUri.equals("")) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(105.dp),
                        painter = painterResource(id = R.drawable.placeholder),
                        contentDescription = ""
                    )
                } else {
                    AsyncImage(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(85.dp),
                        model = selectedImageUri ,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .align(Alignment.TopEnd)
                        .background(Pink80)
                ) {

                    Icon(
                        modifier = Modifier
                            .size(17.dp)
                            .align(Alignment.Center)
                            .clickable {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(start = 10.dp, top = 15.dp),
            text = "Profile Details",
            color = Pink80,
            fontSize = 18.sp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Username",
                color = LightBlue, fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                singleLine = true,
                value = viewModel.userName.value,
                onValueChange = {
                    viewModel.userName.value = it
                },
                placeholder = {
                    Text(text = "Enter Username")
                },
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Email",
                color = LightBlue, fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            var text by remember {
                mutableStateOf("")
            }
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                singleLine = true,
                value = text,
                onValueChange = {
                    text= it
                },
                placeholder = {
                    Text(text = "Enter Email")
                },
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            val scope = CoroutineScope(Dispatchers.Main)
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                androidx.compose.material3.Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 15.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(LightBlue),
                    onClick = {
                        scope.launch {
                            try {
                                viewModel.isUploading.value = true
                                if(viewModel.saveUserData()){
                                    viewModel.isUploading.value = false
                                    navController.popBackStack()
                                }
                            }catch (e : Exception){

                            }finally {

                            }

                        }

                    }) {
                    Text(text = "Save")
                }
            }
        }

    }
}




