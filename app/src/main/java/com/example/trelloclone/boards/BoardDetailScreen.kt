package com.example.trelloclone.boards

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import com.example.trelloclone.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.trelloclone.ui.theme.LightBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


enum class BoardState{
    DISPLAY,EDIT
}

enum class CardState{
    DISPLAY,EDIT
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BoardDetail(
    boardName : String,
    viewModel: BoardViewModel,
    toCardDetailScreen: (BoardCard) -> Unit
) {

    Log.d("name",boardName)

    viewModel.getBoardByName(name = boardName)



    var board by remember {
        mutableStateOf(viewModel.boardDetail.value)
    }
    var listBeingEdited by remember{
        mutableStateOf(BoardList())
    }

    LaunchedEffect(key1 = viewModel.boardDetail.value){
        board = viewModel.boardDetail.value
    }




    var isEdited by remember{
        mutableStateOf(listBeingEdited.isEdited)
    }
    val lazyListScope = rememberLazyListState()

    val snappingLayout = remember(lazyListScope) { SnapLayoutInfoProvider(lazyListScope) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    var cardState by remember{
        mutableStateOf(CardState.DISPLAY)
    }
    var boardState by rememberSaveable {
        mutableStateOf(BoardState.DISPLAY)
    }
    val configuration = LocalConfiguration.current
    val screenWidthDp = with(LocalDensity.current) { configuration.screenWidthDp }
    val expandedWidth = Dp((screenWidthDp - 50).toFloat())
    val listWidth = Dp((screenWidthDp - 80).toFloat())

    val transition = updateTransition(targetState = boardState,label = "transition")

    val width by transition.animateDp(label = "widhtTransition") {
        if(it == BoardState.DISPLAY) {
           listWidth
        }
        else {
            expandedWidth
        }
    }
    val scope = rememberCoroutineScope()

    val height by transition.animateDp(label = "height transition") {
        if(it == BoardState.DISPLAY){
            60.dp
        }else 70.dp
    }

    Surface(
        modifier = Modifier.fillMaxSize(),

    ) {

            Scaffold(
                containerColor = Color(
                    alpha = (board.color shr 24 and 0xFF) / 255f,
                    red = (board.color shr 16 and 0xFF) / 255f,
                    green = (board.color shr 8 and 0xFF) / 255f,
                    blue = (board.color and 0xFF) / 255f
                ),
                topBar = {
                    BoardAppBar(
                        cardState = cardState,
                        onCardCreated = {
                            viewModel.addCardsToList(
                                BoardCard(viewModel.cardName.value),
                                listBeingEdited
                            )
                            cardState = CardState.DISPLAY
                        },
                        boardName = boardName,
                        state = boardState,
                        onClose = {
                            boardState = it
                            cardState = CardState.DISPLAY
                        },
                        onListCreated = {
                            viewModel.addListToDatabase(
                                BoardList(isEdited = isEdited, viewModel.listName.value)
                            )
                            boardState = BoardState.DISPLAY
                        }
                    )
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(it)
                ) {
                    Spacer(modifier = Modifier.width(10.dp))


                    var draggedItemOffset by remember {
                        mutableStateOf(Offset.Zero)
                    }

                    var listContainsDraggedItem by remember{
                        mutableStateOf(false)
                    }

                    LazyRow(
                            state = lazyListScope,
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .zIndex(if(listContainsDraggedItem) 10f else 0f)
                            ,
                            contentPadding = PaddingValues(end = 15.dp),
                            flingBehavior = flingBehavior
                        ) {
                        items(board.lists.size) { index ->
                            BoardListItem(
                                onCardCreated = { cs, boardList ->
                                    cardState = cs
                                    listBeingEdited = boardList
                                    listBeingEdited.isEdited = true
                                },
                                state = cardState,
                                viewModel = viewModel,
                                modifier = Modifier
                                    .width(listWidth)
                                    .padding(end = 15.dp)
                                 ,

                                editableBoardList = listBeingEdited,
                                boardList = board.lists[index],
                                toCardDetailScreen = {toCardDetailScreen(it)}
                            )

                        }
                        item{
                            AddList(
                                modifier = Modifier
                                    .width(width)
                                    .height(height),
                                state = boardState,
                                onClick = {
                                    boardState = it
                                    scope.launch {
                                    //    lazyListScope.animateScrollToItem()
                                    }
                                },
                                viewModel = viewModel
                            )
                        }
                    }



                }
            }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddList(
    modifier: Modifier = Modifier,
    state : BoardState,
    onClick : (BoardState) -> Unit,
    viewModel: BoardViewModel,
) {


    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        isVisible = true
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(tween(500)) + slideIn(tween(300, easing = FastOutSlowInEasing)) {
            IntOffset(40, 60)
        }
    ) {
        Card(
            modifier = modifier
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .clickable {
                    onClick(BoardState.EDIT)
                }
                ,

            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(Color.Black),
            elevation = CardDefaults.cardElevation(15.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if(state == BoardState.DISPLAY){
                Text(
                    modifier = Modifier
                        .align(Center),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    text = "Add List",
                    color = LightBlue
                )
                }else{
                   TextField(
                       modifier = Modifier
                           .fillMaxWidth(),
                       placeholder = {
                           Text(text = "List name", color = Color.Gray)
                       },
                       value = viewModel.listName.value,
                       onValueChange = {
                           viewModel.listName.value = it
                       },
                       colors = TextFieldDefaults.textFieldColors(
                           focusedIndicatorColor = LightBlue
                       )
                   )
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardListItem(
    modifier: Modifier = Modifier,
    boardList: BoardList,
    viewModel: BoardViewModel,
    state: CardState,
    editableBoardList : BoardList,
    onCardCreated : (CardState,BoardList) -> Unit,
    toCardDetailScreen : (BoardCard) -> Unit
){
    Box(
        modifier = modifier
            .background(Color.Black, RoundedCornerShape(8.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = boardList.listName,
                    color = LightBlue,
                    fontSize = 16.sp
                )
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "options",
                    tint = LightBlue,
                )
            }
            if (boardList.cards.isEmpty()) {
                Spacer(modifier = Modifier.height(70.dp))
            } else {


                Box() {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 20.dp)
                    ) {

                        items(boardList.cards.size){i ->
                            CardItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        toCardDetailScreen(boardList.cards[i])
                                    }
                                    .padding(bottom = 10.dp),
                                card = boardList.cards[i]
                            )
                        }

                    }
                }

            }



            Box(modifier = Modifier) {
                Row(
                    modifier = Modifier

                        .align(BottomCenter)
                        .padding(start = 10.dp, end = 10.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (boardList == editableBoardList && state == CardState.EDIT) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            placeholder = {
                                Text(text = "Card Name", color = Color.Gray)
                            },
                            value = viewModel.cardName.value,
                            onValueChange = {
                                viewModel.cardName.value = it
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = LightBlue
                            )
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onCardCreated(CardState.EDIT, boardList)
                                },
                            text = "+ Add Card",
                            color = LightBlue,
                            fontSize = 14.sp
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(TopEnd),
                                painter = painterResource(id = R.drawable.image_icon),
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }

                }
            }
        }
    }
}




    @Composable
    fun BoardAppBar(
        onListCreated: () -> Unit,
        state: BoardState,
        boardName: String,
        onClose: (BoardState) -> Unit,
        cardState: CardState,
        onCardCreated : () -> Unit
    ) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Center
        ) {
            Row(
                verticalAlignment = CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp, top = 10.dp, end = 10.dp, bottom = 12.dp)
            ) {
                if (cardState == CardState.EDIT) {
                    Icon(
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                onClose(BoardState.DISPLAY)
                            },
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Text(
                        modifier = Modifier.width(100.dp),
                        text = "Add Card",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp)
                    ) {
                        Row(modifier = Modifier.align(TopEnd)) {
                            Icon(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clickable {
                                        onCardCreated()
                                    },
                                imageVector = Icons.Default.Done,

                                contentDescription = "",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                        }

                    }
                }
                else {
                    if (state == BoardState.DISPLAY) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = Color.White,
                        )

                        Spacer(modifier = Modifier.width(30.dp))
                        Text(
                            modifier = Modifier.width(120.dp),
                            text = boardName,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp)
                        ) {
                            Row(modifier = Modifier.align(TopEnd)) {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(id = R.drawable.filter_icon),
                                    contentDescription = "",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "",
                                    tint = Color.White
                                )
                            }
                        }

                    } else {
                        Icon(
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    onClose(BoardState.DISPLAY)
                                },
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            tint = Color.White,
                        )
                        Spacer(modifier = Modifier.width(40.dp))
                        Text(
                            modifier = Modifier.width(70.dp),
                            text = "Add List",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp)
                        ) {
                            Row(modifier = Modifier.align(TopEnd)) {
                                Icon(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable {
                                            onListCreated()
                                        },
                                    imageVector = Icons.Default.Done,

                                    contentDescription = "",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                }
                }

        }
    }

    @Composable
    fun CardItem(
        modifier: Modifier = Modifier,
        card: BoardCard,
    ) {

                Box(
                    modifier = modifier
                        .background(Color.DarkGray, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
                        text = card.name,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }


    }
