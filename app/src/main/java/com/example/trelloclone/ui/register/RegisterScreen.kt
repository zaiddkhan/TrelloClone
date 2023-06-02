package com.example.trelloclone.ui.register

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.trelloclone.R
import com.example.trelloclone.ui.googlelogin.LoginRegisterViewModel
import com.example.trelloclone.ui.theme.LightBlue
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(
    viewModel: LoginRegisterViewModel,
    navController: NavHostController
) {


    BackHandler(enabled = true, onBack = {
        viewModel.resetEmailAndPass()
        navController.popBackStack()
    })
    val listOfPages = listOf("SignUp","SignIn")
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    val isLoading by remember {
        mutableStateOf(viewModel.isCreatingAccount.value)
    }

    AnimatedVisibility(
        visible = viewModel.isCreatingAccount.value,
        enter = expandIn(expandFrom = Alignment.Center),
        exit = fadeOut()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(90.dp))
            Text(
                text = "Signing you in ",
                color = LightBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    if(!isLoading) {
        LaunchedEffect(key1 = true) {
            pagerState.scrollToPage(1)
        }
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            pageCount = listOfPages.size,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> SignUpScreen(viewModel,navController)
                1 -> SignInScreen(viewModel = viewModel, onNewUserClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },navController = navController
                )
            }
        }
    }
    }
    



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNewUserClick :() -> Unit,
    viewModel: LoginRegisterViewModel,
    navController: NavHostController
) {


    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    Scaffold(

        snackbarHost = {
            SnackbarHost(snackbarHostState){
                Snackbar(
                    snackbarData = it,
                    contentColor = Color.White,
                    containerColor = LightBlue)
            }
        },

    ) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(it),
        color = Color.White
    ) {

        Column(modifier = Modifier.padding(start = 20.dp)) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome back.",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Email address",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                singleLine = true,
                value = viewModel.email.value,
                onValueChange = {email ->
                    viewModel.email.value = email
                },
                placeholder = {
                    Text(text = "Email address")
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent

                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Password",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                singleLine = true,
                value = viewModel.password.value,
                onValueChange = {
                    viewModel.password.value = it
                },
                placeholder = {
                    Text(text = "Password")
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(15.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.Black),
                onClick = {
                    viewModel.isCreatingAccount.value = true
                    viewModel.signInUserWithEmailAndPassword()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Successfully logged in",
                            duration = SnackbarDuration.Long
                            )
                    }
                   navController.navigate("profile_screen"){
                       popUpTo("register_user"){
                           inclusive = true
                       }
                   }

                })
            {
                Text("Sign In", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .clickable {
                            onNewUserClick()
                            viewModel.resetEmailAndPass()
                        },
                    text = "Don't have an account? \n Click to register",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: LoginRegisterViewModel,
    navController: NavHostController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        var passwordTextColor by remember {
            mutableStateOf(Color.Gray)
        }
        var passwordConditionIcon by remember {
            mutableStateOf(R.drawable.cancel)
        }
        var patternTextColor by remember {
            mutableStateOf(Color.Gray)
        }
        var patternConditionIcon by remember {
            mutableStateOf(R.drawable.cancel)
        }
        val pattern = Regex("[^A-Za-z0-9 ]")

        Column(modifier = Modifier.padding(start = 20.dp)) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Become a Trello member",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Email address",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                singleLine = true,
                value = viewModel.email.value,
                onValueChange = { email ->
                    viewModel.email.value = email
                },
                placeholder = {
                    Text(text = "Email address")
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent

                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Password",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                singleLine = true,
                value = viewModel.password.value,
                onValueChange = { password ->

                    viewModel.password.value = password
                    if(password.length > 10){
                        passwordConditionIcon = R.drawable.condition_checked
                        passwordTextColor = Color.Green
                    }else{
                        passwordConditionIcon = R.drawable.cancel
                        passwordTextColor = Color.Gray
                    }
                    if(password.contains(pattern)){
                        patternConditionIcon = R.drawable.condition_checked
                        patternTextColor = Color.Green
                    }else{
                        patternConditionIcon = R.drawable.cancel
                        patternTextColor = Color.Gray
                    }


                },
                placeholder = {
                    Text(text = "Password")
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))


            ConditionsRow(id = passwordConditionIcon, textColor = passwordTextColor, condition = "At least contains 10 letter")
            ConditionsRow(id = patternConditionIcon, textColor = patternTextColor, condition = "Contains a special character")

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.Black),
                onClick = {
                    viewModel.isCreatingAccount.value = true
                    viewModel.createUserWithEmailAndPassword()
                    navController.navigate("profile_screen"){
                        popUpTo("register_user"){
                            inclusive = true
                        }
                    }

                })
            {
                Text("Sign Up", color = Color.White)
            }
        }
    }
}


@Composable
fun ConditionsRow(
    @DrawableRes id : Int,
    textColor : Color,
    condition : String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.size(10.dp),
            painter = painterResource(id = id),
            contentDescription = "id"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = condition, fontSize = 10.sp,color = textColor)
    }
}