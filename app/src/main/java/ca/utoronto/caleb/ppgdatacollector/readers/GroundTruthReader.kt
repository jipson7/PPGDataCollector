package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.data.DataWrangler
import ca.utoronto.caleb.ppgdatacollector.Sensor
import org.json.JSONObject

class GroundTruthReader(context: Context, private val sensor: Sensor): AbstractDeviceReader(context, sensor.device) {
    override fun onDataReceived(bytes: ByteArray) {
        val dataMap = mutableMapOf<String, Any?>()
        val dataRead = bytes.toHex()

        val hr: Int
        val oxygen: Int

        try {
            hr = Integer.parseInt(dataRead[6] + "" + dataRead[7], 16)
            oxygen = Integer.parseInt(dataRead[8] + "" + dataRead[9], 16)
        } catch (e: StringIndexOutOfBoundsException) {
            Log.e(tag, "Malformed data received.")
            return
        }

        dataMap["hr_valid"] = 127 != hr
        dataMap["hr"] = hr

        dataMap["oxygen_valid"] = 127 != oxygen
        dataMap["oxygen"] = oxygen

        val json = JSONObject(dataMap)
        DataWrangler.createData(json, sensor)
    }

}

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}