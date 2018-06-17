package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log

class MAX30102Reader(context: Context, device: UsbDevice, val motion: Boolean = false): AbstractDeviceReader(context, device) {

    private var startTime: Long? = null

    private val offsets = mutableListOf<Int>()
    private val ir = mutableListOf<Int>()
    private val red = mutableListOf<Int>()
    private var oxygen:Pair<Float, Boolean>? = null
    private var hr:Pair<Int, Boolean>? = null
    private var ratio:Float? = null
    private var correlation:Float? = null

    private var accelerometerX = mutableListOf<Float>()
    private var accelerometerY = mutableListOf<Float>()
    private var accelerometerZ = mutableListOf<Float>()
    private var accelerometerSqrt = mutableListOf<Float>()
    private var gyroX = mutableListOf<Float>()
    private var gyroY = mutableListOf<Float>()
    private var gyroZ = mutableListOf<Float>()

    private var endTime: Long? = null

    override fun onDataReceived(bytes: ByteArray) {
        val byteString = bytes.toString(Charsets.UTF_8)
        if (byteString.isBlank())
            return
        val words = byteString.split("\\s+".toRegex())
        when (words.first()) {
            "start" -> {
                clearData();
                startTime = System.currentTimeMillis()
            }
            "offset" -> offsets.add(words[1].toInt())
            "led" -> {
                ir.add(words[1].toInt())
                red.add(words[2].toInt())
            }
            "oxygen" -> oxygen = Pair(words[1].toFloat(), words[2] == "1")
            "hr" -> hr = Pair(words[1].toInt(), words[2] == "1")
            "ratio" -> ratio = words[1].toFloat()
            "correl" -> correlation = words[1].toFloat()
            "end" -> {
                endTime = System.currentTimeMillis()
                startTime?.let {
                    saveData()
                }
            }
            else -> {
                if (motion) {
                    when(words.first()) {
                        "accel" -> {
                            accelerometerX.add(words[1].toFloat())
                            accelerometerY.add(words[2].toFloat())
                            accelerometerZ.add(words[3].toFloat())
                            accelerometerSqrt.add(words[4].toFloat())
                        }
                        "gyro" -> {
                            gyroX.add(words[1].toFloat())
                            gyroY.add(words[2].toFloat())
                            gyroZ.add(words[3].toFloat())
                        }
                        else -> {
                            throw Exception("Unknown output from Arduino: $byteString")
                        }
                    }
                }
            }
        }
    }

    private fun saveData() {
        Log.d(tag, "Saving data")
    }

    private fun clearData() {
        offsets.clear()
        ir.clear()
        red.clear()
        oxygen = null
        hr = null
        ratio = null
        correlation = null

        accelerometerX.clear()
        accelerometerY.clear()
        accelerometerZ.clear()
        accelerometerSqrt.clear()

        gyroX.clear()
        gyroY.clear()
        gyroZ.clear()
    }
}