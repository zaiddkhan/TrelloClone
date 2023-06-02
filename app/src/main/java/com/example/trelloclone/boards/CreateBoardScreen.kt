package com.example.trelloclone.boards

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.trelloclone.ui.theme.LightBlue
import kotlinx.coroutines.launch
import kotlin.math.exp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoard(
    onBackPressed : () -> Unit,
    goToColorSelection : () -> Unit,
    viewModel: BoardViewModel,
    boardSuccessfullyAdded : () -> Unit
) {



    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedPrivacyType by remember {
        mutableStateOf("")
    }
    val listOfPrivacyTypes = listOf<String>("Public","Private")
    var buttonBackgroundColor by remember {
        mutableStateOf(Color.DarkGray)
    }
    var textFieldSixe by remember {
        mutableStateOf(Size.Zero)
    }
    var buttonTextColor by remember {
        mutableStateOf(Color.Gray)
    }
    var boardName by remember{
        mutableStateOf("")
    }
    val icon = if(expanded){
        Icons.Filled.KeyboardArrowUp
    }else{
        Icons.Filled.KeyboardArrowDown
    }
    if (boardName.equals("")) {
        buttonBackgroundColor = LightBlue
        buttonTextColor = Color.White
    } else {
        buttonBackgroundColor = Color.DarkGray
        buttonTextColor = Color.Gray
    }


    Scaffold(
        topBar = { TopAppBar() }
    ) {
        Surface(
            modifier = Modifier
              .padding(it))
        {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, top = 30.dp)
            ) {
                Text(text = "Board name", color = LightBlue, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp),
                    value = viewModel.boardName.value,
                    onValueChange = {
                        viewModel.boardName.value = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = LightBlue,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    readOnly = true,
                    value = selectedPrivacyType,
                    onValueChange = { selectedPrivacyType = it},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp)
                        .onGloballyPositioned { coords -> textFieldSixe = coords.size.toSize() },
                    label = { Text(text = "Workspace visibility")},
                    trailingIcon = {
                        Icon(
                            icon,
                            contentDescription = "",
                            Modifier.clickable { expanded = !expanded }
                        )
                    }
                )
                DropdownMenu(
                    modifier = Modifier
                        .width(with(LocalDensity.current){textFieldSixe.width.toDp()}),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOfPrivacyTypes.forEach{ type ->
                        DropdownMenuItem(
                            text = {
                                 Text(text = type)
                            },
                            onClick = {
                                selectedPrivacyType = type
                                viewModel.boardPrivacy.value = type
                                expanded = false
                        })
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp)
                        .clickable {
                            goToColorSelection()
                        },
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "Board Background",
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                        Card(
                            modifier = Modifier
                                .size(50.dp)
                                .padding(top = 8.dp, end = 12.dp, bottom = 8.dp),
                            colors = CardDefaults.cardColors(viewModel.selectedBoardColor.value),
                            elevation = CardDefaults.cardElevation(10.dp)
                        ) {

                        }
                    }

                }

                val scope = rememberCoroutineScope()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp, bottom = 15.dp)
                            .align(Alignment.BottomCenter),
                        colors = ButtonDefaults.buttonColors(if(viewModel.boardName.value.equals("")) Color.DarkGray else LightBlue),
                        onClick = {
                            if(!viewModel.boardName.value.equals("")){
                                scope.launch {
                                    try {
                                        val boardAdded =  viewModel.addBoard()
                                        Log.d("value",boardAdded.toString())
                                        if(boardAdded){
                                            boardSuccessfullyAdded()
                                        }
                                    }catch (e : Exception){

                                    }finally {

                                    }
                                }
                            }
                        }) {
                        Text(text = "Create Board", color = if(viewModel.boardName.value.equals("")) Color.Gray else Color.White,)
                    }
                }


            }

        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .background(LightBlue)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 15.dp)
                .size(30.dp),
            imageVector = Icons.Default.Close,
            contentDescription = "",
            tint = Color.White
        )
        Text(
            modifier = Modifier
                 .padding(top = 10.dp, bottom = 10.dp, start = 15.dp),
            text = "Create a new board",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
            )
    }
}

