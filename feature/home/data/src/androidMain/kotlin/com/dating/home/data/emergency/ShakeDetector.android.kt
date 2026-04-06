package com.dating.home.data.emergency

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt
import org.koin.core.context.GlobalContext

actual class ShakeDetector actual constructor() {

    private var sensorManager: SensorManager? = null
    private var sensorEventListener: SensorEventListener? = null
    private var shakeCount = 0
    private var lastShakeTime = 0L
    private val shakeThreshold = 15f
    private val shakeCountRequired = 3
    private val shakeWindowMs = 2000L

    fun startWithContext(context: Context, onShake: () -> Unit) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH

                if (acceleration > shakeThreshold) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime > shakeWindowMs) {
                        shakeCount = 0
                    }
                    shakeCount++
                    lastShakeTime = now
                    if (shakeCount >= shakeCountRequired) {
                        shakeCount = 0
                        onShake()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        sensorManager?.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    actual fun start(onShake: () -> Unit) {
        val context = GlobalContext.get().get<android.app.Application>()
        startWithContext(context, onShake)
    }

    actual fun stop() {
        sensorEventListener?.let { sensorManager?.unregisterListener(it) }
        sensorEventListener = null
        sensorManager = null
        shakeCount = 0
    }
}
