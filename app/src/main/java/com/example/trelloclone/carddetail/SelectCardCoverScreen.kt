package com.example.trelloclone.carddetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trelloclone.ui.theme.Blue
import com.example.trelloclone.ui.theme.Brown
import com.example.trelloclone.ui.theme.Cyan
import com.example.trelloclone.ui.theme.Gray
import com.example.trelloclone.ui.theme.Green
import com.example.trelloclone.ui.theme.LightBlue
import com.example.trelloclone.ui.theme.Orange
import com.example.trelloclone.ui.theme.Pink
import com.example.trelloclone.ui.theme.Purple
import com.example.trelloclone.ui.theme.Red
import com.example.trelloclone.ui.theme.Yellow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCardCoverScreen(
    onColorSelected: (Color) -> Unit
) {

    val listOfColors = listOf(LightBlue, Red, Green, Blue, Yellow, Orange, Cyan, Pink, Brown, Gray, Purple)

    Scaffold(
        topBar = { TopBar() }
    ) {

        Surface(
            modifier = Modifier.padding(it)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(110.dp),
                content = {
                    items(listOfColors.size) {
                        ColorOption(
                            modifier = Modifier
                                .size(110.dp)
                                .padding(10.dp)
                                .clickable {
                                    onColorSelected(listOfColors[it])
                                },
                            color = listOfColors[it]
                        )

                    }
                },
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 16.dp
                )
            )
        }

    }
}

@Composable
fun ColorOption(
    modifier: Modifier,
    color : Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(color),
        shape = RoundedCornerShape(15.dp)
    ){

    }

}


@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .background(LightBlue)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 35.dp),
            text = "Select the card cover",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

