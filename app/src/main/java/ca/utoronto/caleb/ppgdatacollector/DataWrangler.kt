package ca.utoronto.caleb.ppgdatacollector
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketException
import khttp.post as httpPost

object DataWrangler {

    private val tag = "DataWrangler"

    fun push(data: JSONObject, sensor: Sensor) {
        val time = System.currentTimeMillis()
        Log.d(tag, "$time : $data")
    }

    fun createTrial(name: String, age: Int?, copd: Boolean, info: String): Boolean {
        val userJson = JSONObject()
        userJson.put("name", name)
        userJson.put("age", age)
        userJson.put("copd", copd)
        val trialJson = JSONObject()
        trialJson.put("user", userJson)
        trialJson.put("info", info)
        trialJson.put("devices", JSONArray())
        return post(trialJson)
    }

    private fun post(data: JSONObject): Boolean {
        return try {
            val response = httpPost(
                    url = "http://127.0.0.1:3000",
                    data = data
            )
            (response.statusCode == 200)
        } catch (e: SocketException) {
            Log.d(tag, "Failed to contact server. Is it running?")
            false
        }
    }
}