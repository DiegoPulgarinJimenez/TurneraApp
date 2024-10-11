package com.proyectocompumovil.myturneraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.proyectocompumovil.myturneraapp.ui.theme.MyTurneraAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTurneraAppTheme {
                MyTurneraAppApp()
            }
        }
    }
}

data class Participant(val id: String, val name: String)

class MyTurneraAppViewModel : ViewModel() {
    private val _participants = mutableStateListOf<Participant>()
    val participants: List<Participant> = _participants

    private var _currentTurnIndex by mutableStateOf(0)
    val currentTurnIndex: Int = _currentTurnIndex

    fun addParticipant(name: String) {
        _participants.add(Participant(id = _participants.size.toString(), name = name))
    }

    fun nextTurn() {
        _currentTurnIndex = (_currentTurnIndex + 1) % _participants.size
    }

    fun resetTurns() {
        _currentTurnIndex = 0
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MyTurneraAppApp(viewModel: MyTurneraAppViewModel = viewModel()) {
    var isAddingParticipant by remember { mutableStateOf(false) }
    var newParticipantName by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = "Turnera App Manager",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Current Turn: ${viewModel.participants.getOrNull(viewModel.currentTurnIndex)?.name ?: "No participants"}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TvLazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(viewModel.participants) { participant ->
                    ParticipantItem(
                        participant = participant,
                        isCurrentTurn = viewModel.participants.indexOf(participant) == viewModel.currentTurnIndex
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { isAddingParticipant = true }) {
                    Text("Add Participant")
                }
                Button(onClick = { viewModel.nextTurn() }) {
                    Text("Next Turn")
                }
                Button(onClick = { viewModel.resetTurns() }) {
                    Text("Reset Turns")
                }
            }
        }

        if (isAddingParticipant) {
            AddParticipantDialog(
                onDismiss = { isAddingParticipant = false },
                onAddParticipant = { name ->
                    viewModel.addParticipant(name)
                    isAddingParticipant = false
                }
            )
        }
    }
}

@Composable
fun ParticipantItem(participant: Participant, isCurrentTurn: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = if (isCurrentTurn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = participant.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun AddParticipantDialog(onDismiss: () -> Unit, onAddParticipant: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Participant") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Participant Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAddParticipant(name)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MyTurneraAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = Typography(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontSize = 36.sp),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
        ),
        content = content
    )
}