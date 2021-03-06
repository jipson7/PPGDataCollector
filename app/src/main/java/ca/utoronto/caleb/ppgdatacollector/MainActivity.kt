package ca.utoronto.caleb.ppgdatacollector

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.app.PendingIntent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import ca.utoronto.caleb.ppgdatacollector.data.DataWrangler
import ca.utoronto.caleb.ppgdatacollector.data.TrialCreatedCallback
import ca.utoronto.caleb.ppgdatacollector.ui.DeviceInfoAdapter
import ca.utoronto.caleb.ppgdatacollector.ui.DeviceTypeSelectedCallback
import ca.utoronto.caleb.ppgdatacollector.ui.SelectDeviceDialogFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), DeviceTypeSelectedCallback, TrialCreatedCallback {

    private val tag = "main_activity"

    private val dialog = SelectDeviceDialogFragment()

    init {
        dialog.deviceTypeSelectedCallback = this
    }

    private val sensorList = mutableListOf<Sensor>()

    private val deviceInfoAdapter = DeviceInfoAdapter(sensorList, this)

    private val actionUsbPermission = "ca.utoronto.caleb.ppgdatacollector.action.USB_PERMISSION"

    private lateinit var usbManager: UsbManager

    private lateinit var permissionIntent: PendingIntent

    private var running: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBroadcastReceiver()
        setupRecyclerView()
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(actionUsbPermission), 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }

    private fun setupBroadcastReceiver() {
        val filter = IntentFilter(actionUsbPermission)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbReceiver, filter)
    }

    private fun setupRecyclerView() {
        device_info_list.layoutManager = LinearLayoutManager(this)
        device_info_list.adapter = deviceInfoAdapter
    }


    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val device: UsbDevice = it.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                when (it.action){
                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> deviceAttached(device)
                    UsbManager.ACTION_USB_DEVICE_DETACHED -> deviceDetached()
                    actionUsbPermission -> {
                        if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            reset("Permission not granted")
                        }
                    }
                }
            }
        }

    }

    private fun deviceAttached(device: UsbDevice) {
        Log.d(tag, "Device attached")
        dialog.usbDevice = device
        promptForDeviceType()
    }

    private fun promptForDeviceType() {
        dialog.show(fragmentManager, "dialog")
    }

    private fun deviceDetached() {
        reset("Sensor disconnected")
    }

    private fun reset(reason: String) {
        stopSensors()
        showSnackbar("$reason. Reconnect all sensors to begin again.")
        sensorList.clear()
        deviceInfoAdapter.notifyDataSetChanged()
    }

    override fun onDeviceTypeSelected(deviceType: Int, device: UsbDevice) {
        if (!usbManager.hasPermission(device)) {
            usbManager.requestPermission(device, permissionIntent)
        }
        val sensor = Sensor(deviceType, device, this)
        sensorList.add(sensor)
        deviceInfoAdapter.notifyDataSetChanged()
    }

    fun btnClickRecord(view: View) {
        if (running) {
            reset("User ended session")
        } else {
            if (!sensorsEmpty()) {
                collectUserInformation()
            }
        }
    }

    private fun sensorsEmpty(): Boolean {
        if (sensorList.isEmpty()) {
            showSnackbar("No sensors available to monitor")
            return true
        }
        return false
    }

    private fun collectUserInformation() {
        val name: String = username.text.toString().toLowerCase()
        if (name.isBlank()) {
            showSnackbar("Enter a name.")
            return
        }

        val age: Int? = try {
            user_age.text.toString().toInt()
        } catch (e: NumberFormatException) {
            null
        }

        val copd: Boolean = copd_switch.isChecked

        val info: String = additional_info.text.toString()

        DataWrangler.createTrial(name, age, copd, info, this)
    }

    override fun onTrialCreated(success: Boolean) {
        if (success) {
            showSnackbar("Trial created successfully, starting sensors.")
            startSensors()
        } else {
            showSnackbar("Failed to create Trial.")
        }
    }

    private fun startSensors() {
        running = true
        sensorList.map {it.start()}
        runOnUiThread {
            fab.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_pause))
        }
    }

    private fun stopSensors() {
        running = false
        sensorList.map {it.stop()}
        runOnUiThread {
            fab.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_play))
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show()
    }
}
