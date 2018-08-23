package ca.utoronto.caleb.ppgdatacollector.data
import android.util.Log
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import java.net.SocketException
import khttp.post as httpPost

object DataWrangler {

    private const val tag = "DataWrangler"
    private var trialId: String? = null


    private const val ip = "10.70.2.129"
    private const val rootUrl = "http://$ip:3000/trials"

    fun createTrial(name: String, age: Int?, copd: Boolean, info: String, callback: TrialCreatedCallback) {
        val userJson = JSONObject()
        userJson.put("name", name)
        userJson.put("age", age)
        userJson.put("copd", copd)
        val trialJson = JSONObject()
        trialJson.put("user", userJson)
        trialJson.put("info", info)
        launch {
            try {
                val response = httpPost(
                        url = rootUrl,
                        json = trialJson
                )
                if (response.statusCode == 200) {
                    trialId = response.text
                    callback.onTrialCreated(true)
                }
            } catch (e: SocketException) {
                Log.e(tag, "Failed to contact server. Is it running?")
                callback.onTrialCreated(false)
            }
        }
    }


    fun createData(data: JSONObject, deviceType: Int) {

        val message = JSONObject()

        message.put("timestamp", System.currentTimeMillis())
        message.put("device", deviceType)
        message.put("data", data)

        launch {
            val response = httpPost(
                    url = "$rootUrl/$trialId/data",
                    json = message
            )
            if (response.statusCode != 200) {
                Log.e(tag, "Server error when saving datum ${response.statusCode}")
                throw RuntimeException("Server did not return 200 on data creation request")
            } else {
                Log.d(tag, "Saved $deviceType")
            }
        }
    }

}