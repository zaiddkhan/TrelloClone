package com.example.trelloclone

import android.app.Activity.RESULT_OK
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.trelloclone.boards.BoardDetail
import com.google.accompanist.navigation.animation.composable
import com.example.trelloclone.boards.BoardViewModel
import com.example.trelloclone.boards.CreateBoard
import com.example.trelloclone.boards.SelectBoardBackgroundScreen
import com.example.trelloclone.carddetail.CardDetailScreen
import com.example.trelloclone.carddetail.CardViewModel
import com.example.trelloclone.carddetail.SelectCardCoverScreen
import com.example.trelloclone.ui.googlelogin.GoogleAuthClient
import com.example.trelloclone.ui.googlelogin.LoginRegisterViewModel
import com.example.trelloclone.ui.register.LoginScreen
import com.example.trelloclone.ui.SplashScreen
import com.example.trelloclone.ui.profile.ProfileEditScreen
import com.example.trelloclone.ui.profile.ProfileScreen
import com.example.trelloclone.ui.profile.ProfileViewModel
import com.example.trelloclone.ui.register.RegisterScreen
import com.example.trelloclone.ui.theme.TrelloCloneTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrelloCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF202020)
                ) {
                    val viewModel : LoginRegisterViewModel = hiltViewModel()
                    Navigation(viewModel, googleAuthClient = viewModel.googleAuthClient)

                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    viewModel: LoginRegisterViewModel,
    googleAuthClient: GoogleAuthClient
){
    val boardViewModel : BoardViewModel = hiltViewModel()
    val cardViewModel : CardViewModel = hiltViewModel()

    val navController  = rememberAnimatedNavController()
    val scope = rememberCoroutineScope()
    AnimatedNavHost(navController = navController, startDestination = "splash_screen"){
        composable("splash_screen"){
            SplashScreen(navController, viewModel = viewModel)
        }
        composable("login_register_screen"){
            val state by viewModel.state.collectAsStateWithLifecycle()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if(result.resultCode == RESULT_OK){
                        scope.launch {
                            val signInResult = googleAuthClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                           navController.navigate("profile_screen")

                        }
                    }

                })
            LoginScreen(
                state = state,
                onSignInClicked = {
                    scope.launch {
                        val signInIntentSender = googleAuthClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch,
                            ).build()
                        )

                    }
                },
                navController = navController

            )
        }
        composable("register_user"){
            RegisterScreen(viewModel,navController)
        }
        composable("edit_profile_screen"){
            val viewModel : ProfileViewModel = hiltViewModel()
            ProfileEditScreen(viewModel,navController)
        }

        composable("create_board_screen"){

            CreateBoard(
                onBackPressed = {  navController.popBackStack("profile_screen",inclusive = true)
                } ,
                viewModel = boardViewModel,
                goToColorSelection = { navController.navigate("color_selection_page")},
                boardSuccessfullyAdded = {navController.popBackStack()}

            )
        }
        composable("color_selection_page")
        {
            SelectBoardBackgroundScreen(onColorSelected = { color ->
                boardViewModel.selectedBoardColor.value = color
                Log.d("color",boardViewModel.selectedBoardColor.value.toString())
                navController.popBackStack()
            }) {
                navController.popBackStack()
            }
        }
        composable(
            "profile_screen",
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {-300},
                    animationSpec = tween(
                        300,
                        easing =  FastOutSlowInEasing
                    )
                ) + fadeOut(tween(300))
            },

            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = {-300},
                    animationSpec = tween(
                        300,
                        easing =  FastOutSlowInEasing
                    )
                ) + fadeIn(tween(300))
            }
        ){
            ProfileScreen(toProfileEdit = {
                navController.navigate("edit_profile_screen")
            },
                createNewBoard = {
                    navController.navigate("create_board_screen")
                },
                toBoardDetailScreen = {
                    navController.navigate("board_detail_screen/${it}")
                })
        }

       composable(
            "board_detail_screen/{boardName}",
            enterTransition = {
                slideInHorizontally  (
                    initialOffsetX = {400},
                    animationSpec = tween(
                        300,
                        easing =  FastOutSlowInEasing
                    )
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {-300},
                    animationSpec = tween(
                        300,
                        easing =  FastOutSlowInEasing
                    )
                ) + fadeOut(tween(300))
            },
           arguments = listOf(navArgument("boardName"){
               type = NavType.StringType
           })

        ){
           val boardName = remember{
               it.arguments?.getString("boardName")
           }
           if(boardName != null){
               BoardDetail(boardName,boardViewModel, toCardDetailScreen = {
                   cardViewModel.boardCard.value = it
                   navController.navigate("card_detail_screen")
               })
           }
        }

        composable("card_detail_screen"){
            CardDetailScreen(viewModel = cardViewModel, toSelectCover = {
                navController.navigate("card_cover_screen")
            })
        }
        composable("card_cover_screen"){
            SelectCardCoverScreen(onColorSelected = {
                cardViewModel.boardCard.value.color = it.toArgb()
                navController.popBackStack()
            })
        }
    }
}
