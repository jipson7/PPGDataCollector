package ca.utoronto.caleb.ppgdatacollector
import android.util.Log
import org.json.JSONObject
import khttp.post as httpPost

object DataPusher {

    private val tag = "DataPusher"
    private val trialJson: JSONObject? = null

    fun push(data: JSONObject, sensor: Sensor) {
        val time = System.currentTimeMillis()
        Log.d(tag, "$time : $data")
    }
}