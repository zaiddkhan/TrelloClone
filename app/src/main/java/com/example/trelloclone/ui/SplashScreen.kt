package com.example.trelloclone.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.trelloclone.R
import com.example.trelloclone.ui.googlelogin.LoginRegisterViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    viewModel : LoginRegisterViewModel
) {

    val scale = remember{
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true){
        scale.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(2500)
        if(viewModel.googleAuthClient.getSignedInUser() != null || viewModel.getCurrentUser() != null){
            navHostController.navigate("profile_screen"){
                popUpTo("splash_screen"){
                    inclusive = true
                }
            }

        }else {
            navHostController.navigate("login_register_screen"){
            popUpTo("splash_screen"){
                inclusive = true
            }
        }
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Image(modifier = Modifier.scale(scale.value), painter = painterResource(id = R.drawable.t_icon), contentDescription = "Logo")
    }
}