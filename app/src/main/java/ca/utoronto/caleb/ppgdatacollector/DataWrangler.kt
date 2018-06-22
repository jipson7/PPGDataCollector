package ca.utoronto.caleb.ppgdatacollector
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketException
import kotlin.concurrent.thread
import khttp.post as httpPost

object DataWrangler {

    private const val tag = "DataWrangler"

    fun push(data: JSONObject, sensor: Sensor) {
        val time = System.currentTimeMillis()
        Log.d(tag, "$time : $data")
    }

    fun createTrial(name: String, age: Int?, copd: Boolean, info: String, callback: TrialCreatedCallback) {
        val userJson = JSONObject()
        userJson.put("name", name)
        userJson.put("age", age)
        userJson.put("copd", copd)
        val trialJson = JSONObject()
        trialJson.put("user", userJson)
        trialJson.put("info", info)
        trialJson.put("devices", JSONArray())
        thread {
            try {
                val response = httpPost(
                        url = "http://192.168.1.120:3000/trials",
                        json = trialJson
                )
                callback.onTrialCreated(response.statusCode == 200)
            } catch (e: SocketException) {
                Log.e(tag, "Failed to contact server. Is it running?")
                callback.onTrialCreated(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}