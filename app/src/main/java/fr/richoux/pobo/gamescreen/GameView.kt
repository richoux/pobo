package fr.richoux.pobo.gamescreen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.richoux.pobo.R
import fr.richoux.pobo.engine.*
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

private const val TAG = "pobotag GameView"

@Composable
fun GameActions(viewModel: GameViewModel = viewModel()) {
    IconButton(
        onClick = { viewModel.goBackMove() },
        enabled = viewModel.canGoBack
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Undo Move")
    }
    IconButton(
        onClick = { viewModel.goForwardMove() },
        enabled = viewModel.canGoForward
    ) {
        Icon(Icons.Filled.ArrowForward, contentDescription = "Redo Move")
    }
}

@Composable
fun MainView(
    viewModel: GameViewModel,
    lastMove: Position? = null,
    onTap: (Position) -> Unit = { _ -> },
    displayGameState: String = ""
) {
    val board = viewModel.currentBoard
    val player = viewModel.currentPlayer
    val promotionable = viewModel.getFlatPromotionable()
    val selected = viewModel.piecesToPromote.toList()
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(Modifier.fillMaxHeight()) {
                BoardView(
                    board = board,
                    lastMove = lastMove,
                    onTap = onTap,
                    promotionable = promotionable,
                    selected = selected
                )
                Spacer(modifier = Modifier.height(8.dp))
                PiecesStocksView(
                    pool = board.getPlayerPool(PieceColor.Blue),
                    PieceColor.Blue,
                    Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                PiecesStocksView(
                    pool = board.getPlayerPool(PieceColor.Red),
                    PieceColor.Red,
                    Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Player's turn: ",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                    )
                    val style = TextStyle(
                        color = if (player == PieceColor.Blue) Color.Blue else Color.Red,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        fontWeight = FontWeight.Bold,
                        fontStyle = MaterialTheme.typography.body1.fontStyle
                    )
                    Text(
                        text = player.toString(),
                        style = style,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = displayGameState,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                val hasChoiceOfPiece = viewModel.twoTypesInPool()
                if (hasChoiceOfPiece) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        RadioButtonPoBo(player, viewModel)
                    }
                } else {
                    val gameState by viewModel.gameState.collectAsState()
                    val completeSelectionForRemoval =
                        gameState == GameState.SELECTGRADUATION
                                && (((viewModel.state == GameViewModelState.SELECT3 || viewModel.state == GameViewModelState.SELECT1OR3) && viewModel.piecesToPromote.size == 3)
                                || ((viewModel.state == GameViewModelState.SELECT1 || viewModel.state == GameViewModelState.SELECT1OR3) && viewModel.piecesToPromote.size == 1))
                    if(gameState == GameState.SELECTGRADUATION) {
                        Button(
                            onClick = { viewModel.validateGraduationSelection() },
                            enabled = completeSelectionForRemoval,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Validate",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(Modifier.fillMaxHeight()) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .width(IntrinsicSize.Min)
                ) {
                    PiecesStocksView(
                        pool = board.getPlayerPool(PieceColor.Blue),
                        PieceColor.Blue,
                        Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PiecesStocksView(
                        pool = board.getPlayerPool(PieceColor.Red),
                        PieceColor.Red,
                        Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Player's turn: ",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                        )
                        val style = TextStyle(
                            color = if (player == PieceColor.Blue) Color.Blue else Color.Red,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                            fontWeight = FontWeight.Bold,
                            fontStyle = MaterialTheme.typography.body1.fontStyle
                        )
                        Text(
                            text = player.toString(),
                            style = style,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = displayGameState,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    val hasChoiceOfPiece = viewModel.twoTypesInPool()
                    if (hasChoiceOfPiece) {
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            RadioButtonPoBo(player, viewModel)
                        }
                    } else {
                        val gameState by viewModel.gameState.collectAsState()
                        val completeSelectionForRemoval =
                            gameState == GameState.SELECTGRADUATION
                                    && (((viewModel.state == GameViewModelState.SELECT3 || viewModel.state == GameViewModelState.SELECT1OR3) && viewModel.piecesToPromote.size == 3)
                                    || ((viewModel.state == GameViewModelState.SELECT1 || viewModel.state == GameViewModelState.SELECT1OR3) && viewModel.piecesToPromote.size == 1))
                        Button(
                            onClick = { viewModel.validateGraduationSelection() },
                            enabled = completeSelectionForRemoval
                        ) {
                            Text(
                                text = "Validate",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
                BoardView(
                    board = board,
                    lastMove = lastMove,
                    onTap = onTap,
                    promotionable = promotionable,
                    selected = selected
                )
                Column(
                    Modifier
                        .fillMaxHeight()
                        .width(IntrinsicSize.Min)
                ) {
                    PiecesStocksView(
                        pool = board.getPlayerPool(PieceColor.Blue),
                        PieceColor.Blue,
                        Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PiecesStocksView(
                        pool = board.getPlayerPool(PieceColor.Red),
                        PieceColor.Red,
                        Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Player's turn: ",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                        )
                        val style = TextStyle(
                            color = if (player == PieceColor.Blue) Color.Blue else Color.Red,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                            fontWeight = FontWeight.Bold,
                            fontStyle = MaterialTheme.typography.body1.fontStyle
                        )
                        Text(
                            text = player.toString(),
                            style = style,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = displayGameState,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    val hasChoiceOfPiece = viewModel.twoTypesInPool()
                    if (hasChoiceOfPiece) {
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            RadioButtonPoBo(player, viewModel)
                        }
                    } else {
                        val gameState by viewModel.gameState.collectAsState()
                        val completeSelectionForRemoval =
                            gameState == GameState.SELECTGRADUATION
                                    && (((viewModel.state == GameViewModelState.SELECT3 || viewModel.state == GameViewModelState.SELECT1OR3) && viewModel.piecesToPromote.size == 3)
                                    || ((viewModel.state == GameViewModelState.SELECT1 || viewModel.state == GameViewModelState.SELECT1OR3) && viewModel.piecesToPromote.size == 1))
                        Button(
                            onClick = { viewModel.validateGraduationSelection() },
                            enabled = completeSelectionForRemoval
                        ) {
                            Text(
                                text = "Validate",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    val gameState by viewModel.gameState.collectAsState()
    val player = viewModel.currentPlayer
    var lastMove: Position? by remember { mutableStateOf(null) }

    when (gameState) {
        GameState.INIT -> {
            MainView(
                viewModel,
                displayGameState = viewModel.displayGameState
            )
            viewModel.goToNextState()
        }
        GameState.PLAY -> {
            if(viewModel.historyCall)
                lastMove = null

            MainView(
                viewModel,
                lastMove = lastMove,
                displayGameState = viewModel.displayGameState
            )
            if(!viewModel.historyCall)
                viewModel.nextTurn()
            viewModel.goToNextState()
        }
        GameState.SELECTPIECE -> {
            MainView(
                viewModel,
                lastMove = lastMove,
                displayGameState = viewModel.displayGameState)
        }
        GameState.SELECTPOSITION -> {
            val onSelect: (Position) -> Unit = {
                if (viewModel.canPlayAt(it)) {
                    lastMove = it
                    viewModel.playAt(it)
                }
            }
            MainView(
                viewModel,
                lastMove = lastMove,
                onTap = onSelect,
                displayGameState = viewModel.displayGameState
            )
        }
        GameState.CHECKGRADUATION -> {
            MainView(
                viewModel,
                displayGameState = viewModel.displayGameState
            )
            viewModel.checkGraduation()
        }
        GameState.AUTOGRADUATION -> {
            MainView(
                viewModel,
                displayGameState = viewModel.displayGameState
            )
            viewModel.autograduation()
        }
        GameState.SELECTGRADUATION -> {
            lastMove = null
            val onSelect: (Position) -> Unit = {
                viewModel.selectForGraduationOrCancel(it)
            }
            MainView(
                viewModel,
                lastMove = lastMove,
                onTap = onSelect,
                displayGameState = viewModel.displayGameState
            )
        }
        GameState.REFRESHSELECTGRADUATION -> {
            lastMove = null
            MainView(
                viewModel,
                lastMove = lastMove,
                displayGameState = viewModel.displayGameState
            )
            viewModel.goToNextState()
        }
        GameState.END -> {
            val style = TextStyle(
                color = if(player == PieceColor.Blue) Color.Blue else Color.Red,
                fontSize = MaterialTheme.typography.body1.fontSize,
                fontWeight = FontWeight.Bold,
                fontStyle = MaterialTheme.typography.body1.fontStyle
            )
            val acceptNewGame: () -> Unit = {
                viewModel.newGame(viewModel.aiEnabled)
            }
            val declineNewGame: () -> Unit = {
                viewModel.resume()
            }
            AlertDialog(
                onDismissRequest = {},
                buttons = {
                    Row() {
                        Button(
                            { acceptNewGame() }
                        ) {
                            Text(text = "Sure!")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            { declineNewGame() }
                        ) {
                            Text(text = "Next time")
                        }
                    }
                },
                title = {
                    Row()
                    {
                        Text(
                            text = "$player",
                            style = style
                        )
                        Text(
                            text = " wins!"
                        )
                    }
                },
                text = {
                    Text(
                        text = "New game?"
                    )
                }
            )
        }
    }
}

@Composable
fun RadioButtonPoBo(player: PieceColor, viewModel: GameViewModel) {
//    MaterialTheme {
        val icon_po = when (player) {
            PieceColor.Blue -> R.drawable.blue_po
            PieceColor.Red -> R.drawable.red_po
        }
        val icon_bo = when (player) {
            PieceColor.Blue -> R.drawable.blue_bo
            PieceColor.Red -> R.drawable.red_bo
        }

        val selectedValue by viewModel.selectedValue.collectAsState()
        val items = listOf("Po", "Bo")
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = (selectedValue == item),
                            onClick = {
                                //selectedValue.value = item
                                viewModel.cancelPieceSelection()
                                when (item) {
                                    "Po" -> viewModel.selectPo()
                                    else -> viewModel.selectBo()
                                }
                            },
                            role = Role.RadioButton
                        )
                        .padding(8.dp)
                ) {
                    IconToggleButton(
                        checked = selectedValue == item,
                        onCheckedChange = {
                            //selectedValue.value = item
                            viewModel.cancelPieceSelection()
                            when(item) {
                                "Po" -> viewModel.selectPo()
                                else -> viewModel.selectBo()
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (selectedValue == item) {
                                    R.drawable.ic_baseline_check_circle_24
                                } else {
                                    R.drawable.ic_baseline_circle_24
                                }
                            ),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    Image(
                        painter = painterResource(
                            id = when(item) {
                                "Po" -> icon_po
                                else -> icon_bo
                            }
                        ),
                        contentDescription = ""
                    )
                }
            }
        }
//    }
}