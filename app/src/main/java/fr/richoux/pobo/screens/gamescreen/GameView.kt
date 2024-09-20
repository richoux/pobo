package fr.richoux.pobo.screens.gamescreen

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as CColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.richoux.pobo.R
import fr.richoux.pobo.engine.*
import fr.richoux.pobo.ui.LockScreenOrientation
import kotlinx.coroutines.coroutineScope
import fr.richoux.pobo.engine.Color as EColor

private const val TAG = "pobotag GameView"

@Composable
fun GameActions(viewModel: GameViewModel = viewModel()) {
  val canGoBack by viewModel.canGoBack.collectAsState()
  val canGoForward by viewModel.canGoForward.collectAsState()
  if( LocalLayoutDirection.current == LayoutDirection.Rtl ) {
    IconButton(
      onClick = { viewModel.goBackMove() },
      enabled = canGoBack
    ) {
      Icon(Icons.Filled.ArrowForward, contentDescription = "Undo Move")
    }
    IconButton(
      onClick = { viewModel.goForwardMove() },
      enabled = canGoForward
    ) {
      Icon(Icons.Filled.ArrowBack, contentDescription = "Redo Move")
    }
  }
  else {
    IconButton(
      onClick = { viewModel.goBackMove() },
      enabled = canGoBack
    ) {
      Icon(Icons.Filled.ArrowBack, contentDescription = "Undo Move")
    }
    IconButton(
      onClick = { viewModel.goForwardMove() },
      enabled = canGoForward
    ) {
      Icon(Icons.Filled.ArrowForward, contentDescription = "Redo Move")
    }
  }
}

@Composable
fun GameView(
  viewModel: GameViewModel,
//  stringForDebug: String = ""
) {
  LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
  Column(
    Modifier.fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
      BoardView(
        viewModel,
//      stringForDebug = stringForDebug
      )
      Spacer(modifier = Modifier.height(8.dp))
      BelowBoardView(viewModel)
    }

  val gameViewState by viewModel.poolViewState.collectAsStateWithLifecycle()

  if(gameViewState.victory) {
    val style = TextStyle(
      color = if(gameViewState.currentPlayer == EColor.Blue) CColor.Blue else CColor.Red,
      fontSize = MaterialTheme.typography.body1.fontSize,
      fontWeight = FontWeight.Bold,
      fontStyle = MaterialTheme.typography.body1.fontStyle
    )
    EndOfGameDialog(gameViewState.currentPlayer, style, viewModel)
  }
}

fun Modifier.customDialogModifier() = layout { measurable, constraints ->
  val placeable = measurable.measure(constraints);
  layout(constraints.maxWidth, constraints.maxHeight) {
    placeable.place(
      (constraints.maxWidth - placeable.width) / 2,
      9 * (constraints.maxHeight - placeable.height) / 10,
      10f
    )
  }
}

@Composable
fun EndOfGameDialog(
  player: EColor,
  style: TextStyle,
  viewModel: GameViewModel
) {
  val openAlertDialog = remember { mutableStateOf(true) }
  if(viewModel.xp && viewModel.countNumberGames < 100) {
    openAlertDialog.value = false
    viewModel.newGame(viewModel.navController, viewModel.p1IsAI, viewModel.p2IsAI, true)
  }
  else {
    when {
      openAlertDialog.value -> {
        AlertDialog(
          modifier = Modifier
            .customDialogModifier()
            .background(CColor.Transparent)
            .padding(8.dp),
          onDismissRequest = { openAlertDialog.value = false },
          title = {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = stringResource(
                  id = R.string.win, when(player) {
                    EColor.Red -> stringResource(id = R.string.red)
                    else -> stringResource(id = R.string.blue)
                  }
                ),
                style = style
              )
            }
          },
          text = {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = stringResource(id = R.string.newgame),
                style = MaterialTheme.typography.h6
              )
            }
          },
          buttons = {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Button(
                {
                  openAlertDialog.value = false
                  viewModel.newGame(viewModel.navController, viewModel.p1IsAI, viewModel.p2IsAI, false)
                },
                modifier = Modifier.padding(bottom = 12.dp)
              ) {
                Text(text = stringResource(id = R.string.sure))
              }
              Button(
                {
                  openAlertDialog.value = false
                  viewModel.navController.popBackStack()
//                  viewModel.navController.navigate(Screen.Title.route)
                },
                modifier = Modifier.padding(bottom = 12.dp)
              ) {
                Text(text = stringResource(id = R.string.next_time))
              }
            }
          }
        )
      }
    }
  }
}

@Preview(locale = "fr")
@Composable
private fun EndOfGameDialogPreview(
  player: EColor = EColor.Red,
  style: TextStyle = TextStyle(
    color = CColor.Red,
    fontSize = MaterialTheme.typography.h5.fontSize,
    fontWeight = FontWeight.Bold,
    fontStyle = MaterialTheme.typography.body1.fontStyle
  ),
  xp: Boolean = false,
  countNumberGames: Int = 0
) {
  val openAlertDialog = remember { mutableStateOf(true) }
  if(xp && countNumberGames < 100) {
    openAlertDialog.value = false
  }
  else {
    when {
      openAlertDialog.value -> {
        AlertDialog(
          modifier = Modifier
            .customDialogModifier()
            .background(CColor.Transparent)
            .padding(8.dp),
          onDismissRequest = { openAlertDialog.value = false },
          title = {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ){
              Text(
                text = stringResource(
                  id = R.string.win, when(player) {
                    EColor.Red -> stringResource(id = R.string.red)
                    else -> stringResource(id = R.string.blue)
                  }
                ),
                style = style
              )
            }
          },
          text = {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = stringResource(id = R.string.newgame),
                style = MaterialTheme.typography.h6
              )
            }
          },
          buttons = {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Button(
                {
                  openAlertDialog.value = false
                },
                modifier = Modifier.padding(bottom = 12.dp)
              ) {
                Text(text = stringResource(id = R.string.sure))
              }
              Button(
                { openAlertDialog.value = false },
                modifier = Modifier.padding( bottom = 12.dp)
              ) {
                Text(text = stringResource(id = R.string.next_time))
              }
            }
          }
        )
      }
    }
  }
}
