package com.proyectocompumovil.myturneraapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTurneraAppTheme {
                MyTurneraApp()
            }
        }
    }
}

data class Participant(val id: String, val name: String)

class MyTurneraAppViewModel : ViewModel() {
    private val _participants = mutableStateListOf<Participant>()
    val participants: List<Participant> = _participants

    private var _currentTurnIndex by mutableStateOf(0)
    val currentTurnIndex: Int get() = _currentTurnIndex

    fun addParticipant(name: String) {
        _participants.add(Participant(id = _participants.size.toString(), name = name))
    }

    fun nextTurn() {
        if (_participants.isNotEmpty()) {
            _currentTurnIndex = (_currentTurnIndex + 1) % _participants.size
        }
    }

    fun resetTurns() {
        if (_participants.isNotEmpty()) {
            _participants.clear()
            _currentTurnIndex = 0
        }
    }

}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MyTurneraApp(viewModel: MyTurneraAppViewModel = viewModel()) {
    var isAddingParticipant by remember { mutableStateOf(false) }
    var newParticipantName by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Usar un Row para dividir la pantalla en dos
        Row(
            modifier = Modifier.fillMaxSize()
        ){
        // Parte izquierda: Lista de funcionalidades
        Column(
            modifier = Modifier
                .weight(1f)
                //.fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = "Turnera App Manager",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = buildAnnotatedString {
                    append("Current Turn: ")
                    withStyle(style = SpanStyle(color = Color(0xff9900cc))) { // Cambia el color solo del nombre
                        append(viewModel.participants.getOrNull(viewModel.currentTurnIndex)?.name ?: "No participants")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            /*Text(
                text = "Current Turn: ${viewModel.participants.getOrNull(viewModel.currentTurnIndex)?.name ?: "No participants"}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(viewModel.participants, key = { participant -> participant.id }) { participant ->
                    val participantIndex = viewModel.participants.indexOf(participant)
                    val isCurrentTurn = viewModel.participants.indexOf(participant) == viewModel.currentTurnIndex
                    ParticipantItem(
                        participant = participant,
                        // isCurrentTurn = viewModel.participants.indexOf(participant) == viewModel.currentTurnIndex
                        isCurrentTurn = isCurrentTurn
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
                Button(
                    onClick = { viewModel.nextTurn() },
                    enabled = viewModel.participants.size > 1
                ) {
                    Text("Next Turn")
                }
                Button(onClick = { viewModel.resetTurns() }) {
                    Text("Reset Turns")
                }
            }
        }

        // Parte derecha: Reproductor multimedia
        Box(
            modifier = Modifier
                .weight(1f) // Asigna el otro 50% del ancho
                .fillMaxHeight()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Aquí es donde se agregará el reproductor multimedia
            VideoPlayer(url = "https://youtu.be/ROgcM9-N9jM?si=rvEq-IAZPyFlZ4Vl")
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
            color = if (isCurrentTurn) Color(0xff4d0066)
                    else MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium

            //color = if (isCurrentTurn) MaterialTheme.colorScheme.primaryContainer
                    //else MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayer(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}