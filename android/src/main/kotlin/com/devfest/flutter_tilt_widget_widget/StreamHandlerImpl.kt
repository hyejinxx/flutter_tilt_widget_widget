package com.devfest2023.flutter_tilt_widget_widget // ktlint-disable package-name

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink

internal class StreamHandlerImpl(
    private val sensorManager: SensorManager,
    sensorType: Int,
    private val updateInterval: Int,
) : EventChannel.StreamHandler {
    private var sensorEventListener: SensorEventListener? = null

    private val sensor: Sensor by lazy {
        sensorManager.getDefaultSensor(sensorType)
    }
    override fun onListen(arguments: Any?, events: EventSink) {
        sensorEventListener = createSensorEventListener(events)
        sensorManager.registerListener(sensorEventListener, sensor, updateInterval)
    }
    private fun createSensorEventListener(events: EventSink): SensorEventListener {
        return object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                val sensorValues = DoubleArray(event.values.size)
                event.values.forEachIndexed { index, value ->
                    sensorValues[index] = value.toDouble()
                }
                events.success(sensorValues)
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        sensorManager.unregisterListener(sensorEventListener)
    }
}
