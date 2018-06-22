package ca.utoronto.caleb.ppgdatacollector
import android.util.Log
import org.json.JSONObject
import khttp.post as httpPost

object DataWrangler {

    private val tag = "DataWrangler"
    private val trialJson: JSONObject? = null

    fun push(data: JSONObject, sensor: Sensor) {
        val time = System.currentTimeMillis()
        Log.d(tag, "$time : $data")
    }
}