package com.example.trelloclone.ui.register

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.trelloclone.R
import com.example.trelloclone.ui.googlelogin.SignInState
import com.example.trelloclone.ui.theme.LightBlue



@Composable
fun LoginScreen(
    state: SignInState,
    navController: NavHostController,
    onSignInClicked : () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.isSignInSuccessful){
        if(state.isSignInSuccessful){
            Toast.makeText(context, "logged in", Toast.LENGTH_SHORT).show()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ){
        Column(modifier = Modifier.fillMaxWidth()
            , horizontalAlignment = Alignment.CenterHorizontally){

            Image(
                painter = painterResource(id = R.drawable.login_screen_image),
                contentDescription = "login image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, LightBlue),
                            startY = size.height / 3,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(45.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = {
                    onSignInClicked()
                })
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Login with GOOGLE"
                    )
                    Text(
                        modifier = Modifier.padding(end = 70.dp),
                        text = "Continue with GOOGLE",
                        color = LightBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp)
                    ,
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.buttonColors(LightBlue),
                onClick = {
                    navController.navigate("register_user"){
                        popUpTo("login_register_screen"){
                            inclusive = true
                        }
                    }
                })
            {
                Text("Enter email and password", color = Color.White)
            }
        }
    }

}