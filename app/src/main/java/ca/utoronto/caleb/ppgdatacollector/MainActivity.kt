package ca.utoronto.caleb.ppgdatacollector

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.device_info_list

class MainActivity : Activity(), DeviceTypeSelectedCallback {

    private val tag = "main_activity"

    private val dialog = SelectDeviceDialogFragment()

    init {
        dialog.deviceTypeSelectedCallback = this
    }

    private val sensorList = mutableListOf<Sensor>()

    private val deviceInfoAdapter = DeviceInfoAdapter(sensorList, this)

    private val actionUsbPermission = "ca.utoronto.caleb.ppgdatacollector.action.USB_PERMISSION"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBroadcastReceiver()
        setupRecyclerView()
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
        stopSensors()
        Toast.makeText(this, "Sensor disconnected. Restart all sensors.", Toast.LENGTH_LONG).show()
        sensorList.clear()
        deviceInfoAdapter.notifyDataSetChanged()
    }

    override fun onDeviceTypeSelected(deviceType: String, device: UsbDevice) {
        val sensor = Sensor(deviceType, device)
        sensorList.add(sensor)
        deviceInfoAdapter.notifyDataSetChanged()
    }

    fun btnClickBeginRecording(view: View) {
            startSensors()
    }

    private fun startSensors() {
        if (sensorList.isEmpty()) {
            Toast.makeText(this,"No sensors available to monitor.", Toast.LENGTH_LONG).show()
        }
        for (sensor in sensorList) {
            sensor.start()
        }
    }
    private fun stopSensors() {
        for (sensor in sensorList) {
            sensor.stop()
        }
    }
}
