package com.example.trelloclone.boards

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex


internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<BoardCard?>(null)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }
@Composable
fun <T> DragTarget(
    modifier: Modifier,
    dataToDrop: BoardCard,
    content: @Composable (() -> Unit),
    onDragStarted : (BoardCard?) -> Unit
) {

    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current
    val data by remember {
        mutableStateOf(dataToDrop)
    }

    Box(modifier = modifier
        .zIndex(10f)
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                currentState.dataToDrop = data
                currentState.isDragging = true
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = content

            }, onDrag = { change, dragAmount ->
                change.consume()
                currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                if (currentState.dragOffset.x >=  0) {
                    onDragStarted(dataToDrop)
                }
            }, onDragEnd = {
                currentState.isDragging = false
                currentState.dragOffset = Offset.Zero
                currentState.draggableComposable == null
            }, onDragCancel = {
                currentState.dragOffset = Offset.Zero
                currentState.isDragging = false
                currentState.draggableComposable == null
            })
        }
    ) {
        content()
    }
}

@Composable
fun LongPressDraggable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(
            modifier = modifier

        ) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(modifier = Modifier
                    .zIndex(10f)
                    .graphicsLayer {
                        val offset = (state.dragPosition + state.dragOffset)
                        scaleX = 1.3f
                        scaleY = 1.3f
                        alpha = if (targetSize == IntSize.Zero) 0f else .9f
                        translationX = offset.x.minus(targetSize.width / 2)
                        translationY = offset.y.minus(targetSize.height / 2)

                    }
                    .onGloballyPositioned {
                        targetSize = it.size
                    }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}
@Composable
fun <T> DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: T?) -> Unit)
) {

    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
        }
    }) {
        val data = if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as T? else null
        content(isCurrentDropTarget, data)
    }
}

