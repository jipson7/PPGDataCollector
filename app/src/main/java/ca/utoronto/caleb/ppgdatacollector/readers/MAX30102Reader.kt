package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log
import org.json.JSONObject

class MAX30102Reader(context: Context, device: UsbDevice): AbstractDeviceReader(context, device) {


    override fun onDataReceived(bytes: ByteArray) {
        val jsonString = bytes.toString(Charsets.UTF_8)
        if (jsonString.isBlank())
            return
        val json= JSONObject(jsonString)
        saveData(json)
    }

    private fun saveData(json: JSONObject) {
        Log.d(tag, "Saving: $json")
    }
}