package com.example.trelloclone.ui.profile


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.trelloclone.R
import com.example.trelloclone.boards.Board
import com.example.trelloclone.data.User
import com.example.trelloclone.ui.theme.LightBlue
import java.net.InetAddress
import java.net.NetworkInterface


enum class SwipingStates{
    COLLAPSED,EXPANDED
}


@Composable
fun ProfileScreen(
    toProfileEdit : () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    createNewBoard: () -> Unit,
    toBoardDetailScreen: (String) -> Unit
) {


    val boardsListState by viewModel.boardsList


    var loggedInUser by remember {
        mutableStateOf(viewModel.currentUser.value)
    }
    LaunchedEffect(key1 = viewModel.currentUser.value ){
        loggedInUser = viewModel.currentUser.value
        viewModel.getUserData()
        viewModel.fetchBoardsList()
    }

    Column {

        if(loggedInUser == null) NullUserData() else
            UserData(
                 loggedInUser!!,
                 createNewBoard = createNewBoard,boardsListState,
                 toBoardDetailScreen = toBoardDetailScreen
            )

        if(loggedInUser == null){
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                .fillMaxSize()
            ){
                Text(text = "Please create a profile", color = Color.White, fontSize = 20.sp)
                androidx.compose.material3.Button(
                    modifier = Modifier.padding(top = 55.dp),
                    colors = ButtonDefaults.buttonColors(LightBlue),
                    onClick = {
                        toProfileEdit()
                    }) {

                    Text(text = "Edit profile", color = Color.White)
                }
            }
        }

    }
}

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun UserData(
    user: User,
    createNewBoard : () -> Unit,
    boardsList : List<Board>,
    toBoardDetailScreen : (String) -> Unit
) {

    val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)
    val context = LocalContext.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }


        MotionLayout(
            progress = if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction
            else 1f - swipingState.progress.fraction,
            motionScene = MotionScene(content = motionScene),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        )
        {
            val height by animateDpAsState(targetValue = if(swipingState.currentValue == SwipingStates.COLLAPSED) 300.dp else 50.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue)
                    .height(height)
                    .layoutId("box")
                    .swipeable(
                        state = swipingState,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Vertical,
                        anchors = mapOf(
                            0f to SwipingStates.EXPANDED,
                            400f to SwipingStates.COLLAPSED,
                        )
                    )
            ){

            }
            Scaffold(
                modifier = Modifier
                    .layoutId("content")
                    .fillMaxHeight(),
                floatingActionButton = {
                    FabView(
                        items = listOf(
                            FloatingMenuItem("Cards", R.drawable.card),
                            FloatingMenuItem("Boards", R.drawable.board)
                        ),
                        onClick = {
                            when(it.label){
                                "Boards" -> {
                                    createNewBoard()
                                }
                            }
                        }
                    )
                }
            ) {
                Box(modifier = Modifier.padding(it)){
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp, top = 15.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 12.dp, top = 10.dp, bottom = 10.dp),
                                fontSize = 16.sp,
                                text = "Your workspace boards",
                                color = Color.Black
                            )
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(130.dp),
                            contentPadding = PaddingValues(top = 25.dp, end = 10.dp)
                            )
                        {
                            items(boardsList.size){
                                BoardsDisplay(board = boardsList[it],
                                    modifier = Modifier.width(130.dp)
                                        .height(100.dp)
                                        .clickable {
                                            toBoardDetailScreen(boardsList[it].name)
                                        }
                                        .padding(start = 10.dp)
                                )
                            }
                        }
                    }
                }
            }



            val painter = rememberAsyncImagePainter(
                model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(user.imageUrl)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .layoutId("profile_pic"),
                contentScale = ContentScale.Crop
            )


            Column(
                modifier = Modifier
                    .layoutId("details"),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    modifier = Modifier.padding(start = 13.dp),
                    text = user.email,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Boards : ", fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Cards : ", fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Button(
                    modifier = Modifier
                        .width(110.dp)
                        .padding(start = 10.dp),
                    colors = ButtonDefaults.buttonColors(Color.Blue.copy(0.2f)),
                    onClick = {

                    }) {
                    Text(text = "LogOut", color = Color.White)

                }

            }
            Text(
                text = user.userName,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.layoutId("username")
            )
        }
    }



@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
@Composable
fun NullUserData(

) {


    val context = LocalContext.current
    val motionScene = remember{
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }
    MotionLayout(
        motionScene = MotionScene(content = motionScene),
        modifier = Modifier
            .fillMaxWidth()
    )
    {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue)
                    .layoutId("box")
            )

            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .layoutId("profile_pic")
            )

            Text(
                text = "username",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.layoutId("username")
            )
    }
}



@Composable
fun BoardsDisplay(
    board: Board,
    modifier : Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(
                        alpha = (board.color shr 24 and 0xFF) / 255f,
                        red = (board.color shr 16 and 0xFF) / 255f,
                        green = (board.color shr 8 and 0xFF) / 255f,
                        blue = (board.color and 0xFF) / 255f
                    )
                ),


        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(25.dp)
                    .background(Color.Black.copy(0.4f))
            ) {
                Text(modifier = Modifier.padding(start = 10.dp),text = board.name , color = Color.White)
            }

        }
    }

}