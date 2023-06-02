package com.example.trelloclone.carddetail

import android.graphics.fonts.FontFamily
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.trelloclone.data.User
import com.example.trelloclone.ui.theme.LightBlue
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelectDialog(
    dueDate : Boolean,
    setShowDialog: (Boolean) -> Unit,
    dateAndTimeSelected : (LocalDate,LocalTime) -> Unit
) {

    val timeDialogState = rememberMaterialDialogState()
    val dateDialogState = rememberMaterialDialogState()
    var pickedDate by remember{
        mutableStateOf(LocalDate.now())
    }
    var pickedTime by remember{
        mutableStateOf(LocalTime.NOON)
    }

    val formattedState by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMM dd")
                .format(pickedDate)
        }
    }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm")
                .format(pickedTime)
        }
    }
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("ok"){

            }
            negativeButton("cancel"){

            }
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",

        ){
            pickedDate = it
        }
    }
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("OK"){

            }
            negativeButton("cancel"){

            }
        }
    ) {
        timepicker(
            initialTime = LocalTime.NOON,
            title = "Pick a time",
            ){
            pickedTime = it
        }
    }
    Dialog(onDismissRequest = {
            setShowDialog(false)
    }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier
                    .padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if(dueDate) "Due Date" else "Start date",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)

                    ){
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    dateDialogState.show()
                                },
                            enabled = false,
                            value = formattedState,
                            onValueChange = {},
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(end = 10.dp),
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "",
                                    tint = Color.Black
                                )
                            }
                        )
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    timeDialogState.show()
                                },
                            enabled = false,
                            value = formattedTime,
                            onValueChange = {},
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(end = 10.dp),
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = ""
                                )
                            }
                        )

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth()){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        setShowDialog(false)
                                    },
                                text = "CANCEL",
                                color = LightBlue,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        dateAndTimeSelected(pickedDate,pickedTime)
                                        setShowDialog(false)
                                    },
                                text = "OK",
                                color = LightBlue,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                }
            }
    }
}

@Composable
fun AddMembersDialog(
    signedInUsers : List<User>,
    showUsersDialog : (Boolean) -> Unit,
    userAdded: () -> Unit
) {

    Dialog(onDismissRequest = {
        showUsersDialog(false)
    }) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(text = "Add Members", fontSize = 24.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))
                    Column {
                    for(i in signedInUsers.indices){
                        UserDetailItem(user = signedInUsers[i],userAdded)
                    }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailItem(user: User,userAdded :()->Unit) {

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            userAdded()
        }
        .padding(top = 15.dp, bottom = 15.dp, start = 8.dp, end = 8.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current).data(user.imageUrl).build()
            )
            Image(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painter,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = user.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = user.email, fontSize = 13.sp)
            }
        }
    }

}