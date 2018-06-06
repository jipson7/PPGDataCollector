package ca.utoronto.caleb.ppgdatacollector

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.readers.GroundTruthReader


class Sensor(val deviceType: String, val device: UsbDevice, context: Context) {

    companion object {
        const val FINGERTIP_SENSOR = "Fingertip Sensor"
        const val WRIST_SENSOR = "Wrist Worn Device"
        const val GROUND_TRUTH = "Ground Truth Sensor"
        val DEVICE_TYPES = arrayOf(FINGERTIP_SENSOR, WRIST_SENSOR, GROUND_TRUTH)
    }

    val tag = deviceType

    val deviceName = "${device.manufacturerName} ${device.productName}"

    private val reader = when (deviceType) {
        GROUND_TRUTH -> GroundTruthReader(context, device)
        else -> GroundTruthReader(context, device)
    }

    private val thread: Thread = Thread(reader)

    fun start() {
        Log.d(tag, "Starting Sensor")
        thread.start()
    }

    fun stop() {
        Log.d(tag, "Stop Sensor")
        thread.interrupt()
    }
}