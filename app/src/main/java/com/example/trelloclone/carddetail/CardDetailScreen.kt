package com.example.trelloclone.carddetail

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trelloclone.R
import com.example.trelloclone.boards.Checklist
import com.example.trelloclone.ui.theme.Gray
import com.example.trelloclone.ui.theme.LightBlue
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardDetailScreen(
    viewModel: CardViewModel,
    toSelectCover : () -> Unit
) {


    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var showUsersAddDialog by remember {
        mutableStateOf(false)
    }
    var showDialog by remember{
        mutableStateOf(false)
    }

    val scrollState = rememberScrollState()
    var dueDateDialog by remember{
        mutableStateOf(false)
    }
    if(showUsersAddDialog){
        AddMembersDialog(
            signedInUsers = viewModel.listOfUsers.value,
            userAdded = {
            scope.launch { scaffoldState.snackbarHostState.showSnackbar("USER ADDED TO BOARD") }
        }, showUsersDialog = { showUsersAddDialog = it })
    }
    if(showDialog){
        DateSelectDialog(dueDateDialog,setShowDialog = {
            showDialog = it
        }, dateAndTimeSelected = {date,time ->
            viewModel.updateDateAndTime(date,time)
        }
        )
    }

    val showsDialog = {
            dueDate : Boolean ->
        dueDateDialog = dueDate
        showDialog = true
    }

    Scaffold(
        scaffoldState  = scaffoldState,
    backgroundColor = Gray,

    topBar = {
        CardTopBar(
        scrollState = scrollState
        )
    }
) {
    Column(
        modifier = Modifier
            .padding(it)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Color(
                        alpha = (viewModel.boardCard.value.color shr 24 and 0xFF) / 255f,
                        red = (viewModel.boardCard.value.color shr 16 and 0xFF) / 255f,
                        green = (viewModel.boardCard.value.color shr 8 and 0xFF) / 255f,
                        blue = (viewModel.boardCard.value.color and 0xFF) / 255f
                    )
                )
        ) {

            Box(
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 20.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.Black, RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            toSelectCover()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(8.dp),
                        painter = painterResource(id = R.drawable.card),
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        fontSize = 10.sp,
                        text = "Cover",
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        androidx.compose.material3.Text(
            modifier = Modifier.padding(start = 15.dp),
            text = viewModel.boardCard.value.name,
            fontSize = 23.sp,
            color = Color.White)
        Spacer(modifier = Modifier.height(5.dp))

        androidx.compose.material3.Text(
            modifier = Modifier.padding(start = 15.dp),
            text = "lists",
            fontSize = 12.sp,
            color = Color.White)


        Spacer(modifier = Modifier.height(20.dp))

        QuickSection(
            addMember = {
                showUsersAddDialog = true
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .size(29.dp),
                    painter = painterResource(id = R.drawable.description),
                    contentDescription = "",
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                ) {
                    TextField(
                        modifier = Modifier
                            .height(60.dp),
                        value = "",
                        onValueChange = {

                        },
                        placeholder = {
                            Text("Add card description", color = Gray)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = LightBlue
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showsDialog(false)
                    }
                    .padding(start = 10.dp, top = 12.dp, bottom = 12.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                    ,
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(13.dp))
                Text(text = "Start date", fontSize = 14.sp, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showsDialog(true)
                    }
                    .padding(start = 14.dp, top = 12.dp, bottom = 12.dp)
            ) {

                Spacer(modifier = Modifier.width(13.dp))
                Text(text = "Due date...", fontSize = 14.sp, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        var checkListIsDisplaying by remember {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 12.dp, bottom = 12.dp)
            ) {

                Text(
                    text = "Checklists",
                    fontSize = 14.sp,
                    color = Color.White
                )

                Icon(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(20.dp)
                        .clickable {
                            checkListIsDisplaying = if (checkListIsDisplaying) false else true
                        },
                    imageVector = if(checkListIsDisplaying) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "",
                    tint = Color.White
                )

            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth(),
            visible = checkListIsDisplaying
        ) {


            Column {

                if(!viewModel.checklists.value.isEmpty()){
                Column(
                    modifier = Modifier.padding(start = 10.dp)
                ){
                    for(i in 0..viewModel.checklists.value.size-1){
                       CheckListItem(viewModel.checklists.value[i])
                      }
                    }
                }

                Row {
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp)
                            .height(45.dp),
                        placeholderText = "Add an item..",
                        addChecklistItem = {
                            viewModel.addToCheckList(Checklist(title = it))
                        }
                    )
                }
            }
        }


    }
}

}

@Composable
fun CheckListItem(
    checklist: Checklist
) {
    var isCompleted by remember {
        mutableStateOf(checklist.isComplete)
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = {
                    isCompleted = true
                    checklist.isComplete = true
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = checklist.title, color = Color.White,
                fontSize = 16.sp,
                textDecoration = if(checklist.isComplete) TextDecoration.LineThrough else null
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Divider(color = Color.Black, startIndent = 20.dp)

        Spacer(modifier = Modifier.height(10.dp))

    }
}
@Composable
fun CardTopBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if(scrollState.canScrollBackward) Gray else Color.DarkGray
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(backgroundColor),
     )
    {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "close",
            modifier = Modifier
                .size(25.dp)
                .padding(start = 15.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier
            .width(20.dp))
        AnimatedVisibility(visible = scrollState.canScrollBackward) {
            Text(text = "Card name", color = Color.White)
        }
    }

}

@Composable
fun QuickSection(
    addMember : () -> Unit
) {
    Box(modifier = Modifier
        .height(200.dp)
        .fillMaxWidth()
        .background(Color.DarkGray)
    )
    {
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.Center)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Action(
                    modifier = Modifier
                        .weight(1f),
                    iconid = R.drawable.checklist,
                    title = "Add checklist"
                )
                Action(
                    modifier = Modifier.weight(1f),
                    iconid = R.drawable.attachment,
                    title = "Add attachment"
                )
            }

            Spacer(modifier = Modifier
                .height(10.dp))

            Row(modifier = Modifier
                .fillMaxWidth()) {
                Action(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable {
                            addMember()
                        },
                    iconid = R.drawable.person,
                    title = "Add member"
                )
            }

        }


    }
}

@Composable
fun Action(
    modifier: Modifier = Modifier,
    @DrawableRes iconid : Int,
    title : String
) {

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(top = 10.dp, start = 8.dp, end = 8.dp, bottom = 10.dp)
            .background(
                Color.Black, RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(20.dp)
                    ,
            painter = painterResource(id = iconid),
            contentDescription = "",
            tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
         modifier = Modifier
             .padding( top = 15.dp, bottom = 15.dp, end = 10.dp),
             text = title,
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun CustomTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = "Placeholder",
    addChecklistItem : (String) -> Unit,
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize
) {
    var text by rememberSaveable { mutableStateOf("") }
    BasicTextField(modifier = modifier
        .background(
            MaterialTheme.colors.surface,
            MaterialTheme.shapes.small,
        )
        .fillMaxWidth(),
        value = text,
        onValueChange = {
            text = it
        },
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface,
            fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (text.isEmpty()) Text(
                        placeholderText,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            fontSize = fontSize
                        )
                    )
                    innerTextField()
                }
                if (!text.equals(""))
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 10.dp)
                            .clickable {
                                addChecklistItem(text)
                                text = ""
                            },
                        tint = Color.Black,
                        imageVector = Icons.Default.Add,
                        contentDescription = ""
                    )
            }
        }
    )
}
