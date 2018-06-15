package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import android.hardware.usb.UsbDevice

class MAX30102Reader(context: Context, device: UsbDevice, val motion: Boolean = false): AbstractDeviceReader(context, device) {

    private var startTime: Long? = null

    private val offsets = mutableListOf<Int>()
    private val ir = mutableListOf<Int>()
    private val red = mutableListOf<Int>()
    private var oxygen:Pair<Float, Boolean>? = null
    private var hr:Pair<Int, Boolean>? = null
    private var ratio:Float? = null
    private var correlation:Float? = null

    private var endTime: Long? = null

    override fun onDataReceived(bytes: ByteArray) {
        val line = bytes.toString().split("\\s+")
        when (line[0]) {
            "start" -> {
                clearData();
                startTime = System.currentTimeMillis()
            }
            "offset" -> offsets.add(line[1].toInt())
            "led" -> {
                ir.add(line[1].toInt())
                red.add(line[2].toInt())
            }
            "oxygen" -> oxygen = Pair(line[1].toFloat(), line[2] == "1")
            "hr" -> hr = Pair(line[1].toInt(), line[2] == "1")
            "ratio" -> ratio = line[1].toFloat()
            "correl" -> correlation = line[1].toFloat()
            "end" -> endTime = System.currentTimeMillis()
            else -> {
                if (motion) {
                    // Do motion when here
                }
            }
        }
    }

    private fun clearData() {
        offsets.clear()
        ir.clear()
        red.clear()
        oxygen = null
        hr = null
        ratio = null
        correlation = null
    }
}