package ca.utoronto.caleb.ppgdatacollector.data
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.Sensor
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketException
import kotlin.concurrent.thread
import khttp.post as httpPost

object DataWrangler {

    private const val tag = "DataWrangler"
    private var trialId: String? = null
    private val deviceIds = mutableMapOf<Sensor, String>()


    private const val ip = "192.168.1.120"
    private const val rootUrl = "http://$ip:3000/trials"

    fun createTrial(name: String, age: Int?, copd: Boolean, info: String, callback: TrialCreatedCallback) {
        val userJson = JSONObject()
        userJson.put("name", name)
        userJson.put("age", age)
        userJson.put("copd", copd)
        val trialJson = JSONObject()
        trialJson.put("start", System.currentTimeMillis())
        trialJson.put("user", userJson)
        trialJson.put("info", info)
        trialJson.put("devices", JSONArray())
        thread {
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

    fun createDevice(sensor: Sensor, callback: DeviceCreatedCallback) {
        if (trialId == null) {
            throw RuntimeException("Cannot create a device for a trial that does not exist")
        }
        val json = sensor.toJson()
        thread {
            try {
                val response = httpPost(
                        url = "$rootUrl/$trialId",
                        json = json
                )
                if (response.statusCode == 200) {
                    deviceIds[sensor] = response.text
                    callback.onDeviceCreated(true)
                }
            } catch (e: SocketException) {
                Log.e(tag, "Failed to contact server. Is it running?")
                callback.onDeviceCreated(false)
            }
        }

    }


    fun createData(data: JSONObject, sensor: Sensor) {
        if (!deviceIds.containsKey(sensor)) {
            throw RuntimeException("Cannot create a data point for a device that does not exist")
        }

        val deviceId = deviceIds[sensor]

        val time = System.currentTimeMillis()
        data.put("timestamp", time)
        thread {
            val response = httpPost(
                    url = "$rootUrl/$trialId/devices/$deviceId",
                    json = data
            )
            if (response.statusCode != 200) {
                Log.e(tag, "Server error when saving datum ${response.statusCode}")
                throw RuntimeException("Server did not return 200 on data creation request")
            }
        }
    }

}