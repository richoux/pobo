package fr.richoux.pobo.gamescreen

import android.util.Log
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import fr.richoux.pobo.engine.*
import fr.richoux.pobo.ui.BoardColors

private const val TAG = "pobotag BoardView"

@Composable
fun BoardView(
    modifier: Modifier = Modifier,
    board: Board,
    lastMove: Position?,
    onTap: (Position) -> Unit
){
    Box(modifier) {
        BoardBackground(lastMove, onTap)
        Log.d(TAG, "BoardView call")
        BoardLayout(
            pieces = board.allPieces,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
        )
    }
}

@Composable
fun BoardBackground(
    lastMove: Position?,
    onTap: (Position) -> Unit
){
    Column {
        for (y in 0 until 6) {
            Row {
                for (x in 0 until 6) {
                    val position = Position(x, y)
                    val white = y % 2 == x % 2
                    val color = if (position.isSame(lastMove)) {
                        if (white) BoardColors.lastMoveLight else BoardColors.lastMoveDark
                    } else {
                        if (white) BoardColors.lightSquare else BoardColors.darkSquare
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(color)
                            .aspectRatio(1.0f)
                            .clickable(
                                onClick = { onTap(position) }
                            )
                    ) {
                        if (y == 5) {
                            Text(
                                text = "${'a' + x}",
                                modifier = Modifier.align(Alignment.BottomEnd),
                                style = MaterialTheme.typography.caption,
                                color = Color.Black.copy(0.5f)
                            )
                        }
                        if (x == 0) {
                            Text(
                                text = "${6 - y}",
                                modifier = Modifier.align(Alignment.TopStart),
                                style = MaterialTheme.typography.caption,
                                color = Color.Black.copy(0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PieceView(piece: Piece, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = piece.imageResource()),
        modifier = modifier.padding(4.dp),
        contentDescription = piece.id
    )
}

@Composable
private fun BoardLayout(
    modifier: Modifier = Modifier,
    pieces: List<Pair<Position, Piece>>
) {
    Log.d(TAG, "BoardView: BoardLayout")
    val constraints: ConstraintSet = constraintsFor(pieces)
    Log.d(TAG, "BoardView: BoardLayout")

    ConstraintLayout(
        modifier = modifier,
        animateChanges = true,
        animationSpec = spring(),
        constraintSet = constraints
    ) {
        pieces.forEach { (_, piece) ->
            PieceView(piece = piece, modifier = Modifier.layoutId(piece.id))
            Log.d(TAG, "BoardView, PieceView call for ${piece.id}")
        }
    }
}

private fun constraintsFor(pieces: List<Pair<Position, Piece>>): ConstraintSet {
    return ConstraintSet {
        val horizontalGuidelines = (0..6).map { createGuidelineFromAbsoluteLeft(it.toFloat() / 6f) }
        val verticalGuidelines = (0..6).map { createGuidelineFromTop(it.toFloat() / 6f) }
        Log.d(TAG, "BoardView, number of pieces = ${pieces.size}")
        pieces.forEach { (position, piece) ->
            val pieceRef = createRefFor(piece.id)
            Log.d(TAG, "BoardView, pieceRef=${pieceRef} at ${position}")
            constrain(pieceRef) {
                top.linkTo(verticalGuidelines[position.y])
                bottom.linkTo(verticalGuidelines[position.y + 1])
                start.linkTo(horizontalGuidelines[position.x])
                end.linkTo((horizontalGuidelines[position.x + 1]))
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        }
    }
}
