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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

data class Participant(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("cabina") val cabina: String,
    @SerializedName("turno") val turno: String
) {
    val turni: Int get() = turno.toIntOrNull() ?: Int.MAX_VALUE
}

class MyTurneraAppViewModel : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://673e4f140118dbfe860ae787.mockapi.io/turnos/") // URL del servicio
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(TurnService::class.java)

    private val _participants = mutableStateListOf<Participant>()
    val participants: List<Participant> = _participants

    private var _currentTurnIndex by mutableStateOf(0)
    val currentTurnIndex: Int get() = _currentTurnIndex

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    init {
        fetchParticipants()
    }

    fun fetchParticipants() {
        viewModelScope.launch {
            try {
                // Simplemente obtenemos los participantes del servicio y los ordenamos usando `turno`
                val fetchedParticipants = service.getTurns().sortedBy { it.turni }

                // Actualizamos la lista con los participantes ordenados
                _participants.clear()
                _participants.addAll(fetchedParticipants)
            } catch (e: Exception) {
                // Manejo de errores
                _errorMessage = "Error al cargar los turnos: ${e.message}"
            }
        }
    }



    fun loadTurns() {
        fetchParticipants()
    }

//    fun addParticipant(name: String) {
//        _participants.add(Participant(id = _participants.size.toString(), name = name))
//    }

    fun nextTurn() {
        if (_participants.isNotEmpty()) {
            _currentTurnIndex = (_currentTurnIndex + 1) % _participants.size
        }
    }

//    fun resetTurns() {
//        if (_participants.isNotEmpty()) {
//            _participants.clear()
//            _currentTurnIndex = 0
//        }
//    }

}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MyTurneraApp(viewModel: MyTurneraAppViewModel = viewModel()) {
//    var isAddingParticipant by remember { mutableStateOf(false) }
//    var newParticipantName by remember { mutableStateOf("") }
    val isLoading = viewModel.participants.isEmpty()
    val errorMessage = viewModel.errorMessage

    if (isLoading) {
        if (errorMessage != null) {
            // Mostrar el mensaje de error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorMessage, // Mensaje de error desde el ViewModel
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadTurns() }) {
                        Text("Reintentar")
                    }
                }
            }
        } else {
            // Mostrar indicador de carga
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        // Mostrar la lista de participantes cuando no hay errores
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(viewModel.participants, key = { it.id }) { participant ->
                    ParticipantItem(
                        participant = participant,
                        isCurrentTurn = participant == viewModel.participants.getOrNull(viewModel.currentTurnIndex)
                    )
                }
            }
        }
    }


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
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp)) {
                        append("Current Turn:\n\n")                    }
                    val participant = viewModel.participants.getOrNull(viewModel.currentTurnIndex)
                    if (participant != null) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                            append("Nombre: ${participant.nombre}\n")
                        }
                        append("ID: ${participant.id}\n")
                        append("Turno: ${participant.turni}\n")
                        append("Cabina: ${participant.cabina}\n")
                    } else {
                        append("No participants")
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
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
                items(viewModel.participants.sortedBy { it.turni }, key = { participant -> participant.id }) { participant ->
                    val isCurrentTurn = participant == viewModel.participants.getOrNull(viewModel.currentTurnIndex)
                    ParticipantItem(
                        participant = participant,
                        isCurrentTurn = isCurrentTurn
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                Button(onClick = { isAddingParticipant = true }) {
//                    Text("Add Participant")
//                }
                Button(
                    onClick = { viewModel.nextTurn() },
                    enabled = viewModel.participants.size > 1
                ) {
                    Text("Next Turn")
                }
//                Button(onClick = { viewModel.resetTurns() }) {
//                    Text("Reset Turns")
//                }
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
            VideoPlayer(url = "https://youtu.be/fHoEd1hE0HY?si=dcYBZzmSEX9LuOA1")
        }
    }

//        if (isAddingParticipant) {
//            AddParticipantDialog(
//                onDismiss = { isAddingParticipant = false },
//                onAddParticipant = { name ->
//                    viewModel.addParticipant(name)
//                    isAddingParticipant = false
//                }
//            )
//        }
    }
}

@Composable
fun ParticipantItem(participant: Participant, isCurrentTurn: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = if (isCurrentTurn) Color(0xff4d0066)
        else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            // Turno
            Text(
                text = " Turno: ${participant.turni}",
                style = MaterialTheme.typography.bodyMedium
            )
            // Nombre
            Text(
                text = " Nombre: ${participant.nombre}",
                style = MaterialTheme.typography.bodyMedium
            )
            // ID
            Text(
                text = " ID: ${participant.id}",
                style = MaterialTheme.typography.bodyMedium
            )
            // Cabina
            Text(
                text = " Cabina: ${participant.cabina}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}




//@Composable
//fun AddParticipantDialog(onDismiss: () -> Unit, onAddParticipant: (String) -> Unit) {
//    var name by remember { mutableStateOf("") }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Add Participant") },
//        text = {
//            TextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Participant Name") }
//            )
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    if (name.isNotBlank()) {
//                        onAddParticipant(name)
//                    }
//                }
//            ) {
//                Text("Add")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
//}

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