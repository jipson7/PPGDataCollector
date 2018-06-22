package ca.utoronto.caleb.ppgdatacollector
import android.util.Log
import org.json.JSONObject
import khttp.post as httpPost

object DataPusher {

    private val tag = "DataPusher"

    fun push(json: JSONObject) {
        val time = System.currentTimeMillis()
        Log.d(tag, "$time : $json")
    }
}