package ca.utoronto.caleb.ppgdatacollector

import android.util.Log

class DeviceReader: Runnable {

    val tag = "device_reader"

    override fun run() {
        Log.d(tag, "Begin run device reader")
    }


}