package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import ca.utoronto.caleb.ppgdatacollector.data.DataWrangler
import org.json.JSONObject
import ca.utoronto.caleb.ppgdatacollector.Sensor

class MAX30102Reader(context: Context, private val sensor: Sensor): AbstractDeviceReader(context, sensor.device) {


    override fun onDataReceived(bytes: ByteArray) {
        val jsonString = bytes.toString(Charsets.UTF_8)
        if (jsonString.isBlank())
            return
        val json= JSONObject(jsonString)
        DataWrangler.createData(json, sensor.deviceType)
    }
}