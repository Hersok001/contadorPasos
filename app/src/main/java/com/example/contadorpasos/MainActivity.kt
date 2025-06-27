package com.example.contadorpasos.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contadorpasos.ui.theme.ContadorPasosTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var pasos = 0
    private var lastMagnitude = 0.0
    private val threshold = 10

    private var pasosState = mutableStateOf(0)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            ContadorPasosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContadorPantalla(pasosState)
                }
            }
        }
    }

    @Composable
    fun ContadorPantalla(pasosState: State<Int>) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
                    )
                )
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Círculo decorativo
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.White, shape = CircleShape)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${pasosState.value}",
                        style = MaterialTheme.typography.headlineLarge.copy(color = Color(0xFF2193b0))
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Pasos dados",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¡Sigue caminando!",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        guardarPasosHoy(pasos)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val magnitude = sqrt((x * x + y * y + z * z).toDouble())
            val delta = magnitude - lastMagnitude

            if (delta > threshold) {
                pasos++
                pasosState.value = pasos
            }

            lastMagnitude = magnitude
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun guardarPasosHoy(pasos: Int) {
        val sharedPref = getSharedPreferences("pasos", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editor.putInt(fecha, pasos)
        editor.apply()
    }
}