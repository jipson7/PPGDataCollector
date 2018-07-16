package ca.utoronto.caleb.ppgdatacollector

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.readers.GroundTruthReader
import ca.utoronto.caleb.ppgdatacollector.readers.MAX30102Reader


class Sensor(val deviceType: Int, val device: UsbDevice, context: Context) {

    companion object {
        const val WRIST_SENSOR = 0
        const val FINGERTIP_SENSOR = 1
        const val GROUND_TRUTH = 2
        val DEVICE_NAMES = arrayOf("Wrist Worn Device", "Fingertip Reflective", "Fingertip Transitive")
    }

    val deviceName = "${device.manufacturerName} ${device.productName}"

    private val tag = "Device $deviceType"

    private val reader = when (deviceType) {
        GROUND_TRUTH -> GroundTruthReader(context, this)
        WRIST_SENSOR -> MAX30102Reader(context, this)
        FINGERTIP_SENSOR -> MAX30102Reader(context, this)
        else -> throw IllegalArgumentException("Unknown Device type")
    }

    private val thread: Thread = Thread(reader)

    fun start() {
        thread.start()
    }

    fun stop() {
        Log.d(tag, "Stop Sensor")
        reader.running = false
        thread.interrupt()
    }

 }