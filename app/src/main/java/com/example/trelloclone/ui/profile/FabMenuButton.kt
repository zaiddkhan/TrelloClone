package com.example.trelloclone.ui.profile

import android.hardware.lights.Light
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trelloclone.ui.theme.LightBlue

data class FloatingMenuItem(
    val label : String,
    val icon : Int,
)

enum class FloatingMenuSate{
    Expanded,Collapsed
}

@Composable
fun FloatingMenuItemButton(
    item : FloatingMenuItem,
    onClick : (FloatingMenuItem) -> Unit,
    modifier : Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = { onClick(item) },
        backgroundColor = LightBlue
    ) {
        Icon(modifier = Modifier.size(50.dp), painter = painterResource(id = item.icon), contentDescription = "", tint = LightBlue)

    }
}

@Composable
fun FloatingMenuItemLabel(
    label: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color.Black.copy(alpha = 0.8f)
    ) {
        Text(
            text = label, color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
fun FloatingMenuItem(
    menuItem: FloatingMenuItem,
    onMenuItemClick : (FloatingMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically)
    {
        FloatingMenuItemLabel(label = menuItem.label)
        FloatingMenuItemButton(item = menuItem, onClick = onMenuItemClick)
    }
}

@Composable
fun FloatingMenu(
    visible : Boolean,
    items : List<FloatingMenuItem>,
    modifier: Modifier = Modifier,
    onClick: (FloatingMenuItem) -> Unit
) {

    val enterTransition = remember{
        expandVertically(
            expandFrom = Alignment.Bottom,
            animationSpec = tween(150, easing =  FastOutSlowInEasing)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        )
    }

    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Bottom,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        )
    }
    AnimatedVisibility(visible = visible, enter = enterTransition, exit = exitTransition) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            items.forEach {
                FloatingMenuItem(menuItem = it, onMenuItemClick = {
                    onClick(it)
                })
            }
        }
    }
}

@Composable
fun FloatingFab(
    state : FloatingMenuSate,
    rotation : Float,
    onClick: (FloatingMenuSate) -> Unit,
    modifier: Modifier = Modifier
) {

    FloatingActionButton(
        modifier = modifier.rotate(rotation),

        onClick = {
            onClick(
                if (state == FloatingMenuSate.Expanded) {
                    FloatingMenuSate.Collapsed
                } else {
                    FloatingMenuSate.Expanded
                })
        },
        backgroundColor = LightBlue,
        shape = CircleShape
    )
    {
        Icon(imageVector = Icons.Default.Add, contentDescription ="", tint = Color.White )
    }

}


@Composable
fun FabView(
    items : List<FloatingMenuItem>,
    modifier: Modifier = Modifier,
    onClick: (FloatingMenuItem) -> Unit
) {
    var filterFabState by rememberSaveable() {
        mutableStateOf(FloatingMenuSate.Collapsed)
    }

    val transitionState = remember {
        MutableTransitionState(filterFabState).apply {
            targetState = FloatingMenuSate.Collapsed
        }
    }

    val transition = updateTransition(targetState = filterFabState,label = "transition")

    val iconRotationByDegrees by transition.animateFloat(
        transitionSpec = {
            tween(1000)
        }, label = ""
    ){
        state -> if(state == FloatingMenuSate.Expanded) 180f else 0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
    ){
        FloatingMenu(visible = filterFabState == FloatingMenuSate.Expanded, items = items, onClick = {onClick(it)})
        FloatingFab(
            state = filterFabState,
            rotation = iconRotationByDegrees,
            onClick = {
                filterFabState = it
            })
    }
}
