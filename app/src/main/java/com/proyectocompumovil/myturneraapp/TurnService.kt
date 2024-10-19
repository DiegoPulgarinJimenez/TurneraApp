package com.proyectocompumovil.myturneraapp
import retrofit2.http.GET

interface TurnService {
    @GET("participants") // Reemplazar "turnos" por el endpoint real proporcionado por el servicio
    suspend fun getTurns(): List<Participant> // (Asegurarte de que Participant coincida con la estructura JSON que envía el servicio).
}