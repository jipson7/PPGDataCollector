package ca.utoronto.caleb.ppgdatacollector

import android.hardware.usb.UsbDevice
import android.util.Log


class Sensor(val deviceType: String, val device: UsbDevice) {

    val tag = deviceType

    val deviceName = "${device.manufacturerName} ${device.productName}"

    companion object {
        val FINGERTIP_SENSOR = "Fingertip Sensor"
        val WRIST_SENSOR = "Wrist Worn Device"
        val GROUND_TRUTH = "Ground Truth Sensor"
        val DEVICES = arrayOf(FINGERTIP_SENSOR, WRIST_SENSOR, GROUND_TRUTH)
    }

    private val reader = when (deviceType) {
        FINGERTIP_SENSOR -> DeviceReader()
        else -> DeviceReader()
    }

    val thread: Thread = Thread(reader)

    fun logData() {

    }

    fun start() {
        Log.d(tag, "Starting Sensor")
        thread.start()
    }

    fun stop() {
        Log.d(tag, "Stop Sensor")
        thread.interrupt()
    }
}