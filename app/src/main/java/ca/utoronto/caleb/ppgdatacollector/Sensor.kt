package ca.utoronto.caleb.ppgdatacollector

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.readers.GroundTruthReader
import ca.utoronto.caleb.ppgdatacollector.readers.MAX30102Reader
import org.json.JSONArray
import org.json.JSONObject


class Sensor(val deviceType: String, val device: UsbDevice, context: Context): DeviceCreatedCallback {

    companion object {
        const val FINGERTIP_SENSOR = "Fingertip Sensor"
        const val WRIST_SENSOR = "Wrist Worn Device"
        const val GROUND_TRUTH = "Ground Truth Sensor"
        val DEVICE_TYPES = arrayOf(FINGERTIP_SENSOR, WRIST_SENSOR, GROUND_TRUTH)
    }

    val tag = deviceType

    val deviceName = "${device.manufacturerName} ${device.productName}"

    private val reader = when (deviceType) {
        GROUND_TRUTH -> GroundTruthReader(context, this)
        WRIST_SENSOR -> MAX30102Reader(context, this)
        FINGERTIP_SENSOR -> MAX30102Reader(context, this)
        else -> throw IllegalArgumentException("Unknown Device type")
    }

    private val thread: Thread = Thread(reader)

    fun start() {
        Log.d(tag, "Creating sensor in db.")
        DataWrangler.createDevice(this, this)
    }

    fun stop() {
        Log.d(tag, "Stop Sensor")
        thread.interrupt()
    }

    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("type", deviceType)
        obj.put("name", deviceName)
        obj.put("data", JSONArray())
        return obj
    }

    override fun onDeviceCreated(success: Boolean) {
        if (success) {
            Log.d(tag, "Device created successfully.")
            Log.d(tag, "Starting $deviceType.")
            thread.start()
        }
    }
 }