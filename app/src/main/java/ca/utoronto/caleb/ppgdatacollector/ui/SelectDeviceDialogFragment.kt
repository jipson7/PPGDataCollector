package ca.utoronto.caleb.ppgdatacollector.ui

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.app.AlertDialog
import android.hardware.usb.UsbDevice
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.Sensor


class SelectDeviceDialogFragment: DialogFragment() {

    private val debugTag = "device_dialog"

    private val deviceTypes = Sensor.DEVICE_TYPES

    lateinit var deviceTypeSelectedCallback: DeviceTypeSelectedCallback

    lateinit var usbDevice: UsbDevice

    var selectedDeviceType: String = deviceTypes[0]

    init {
        Log.d(debugTag, "Dialog object created")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select device type: ")
        builder.setSingleChoiceItems(deviceTypes, 0) { _, which ->
            selectedDeviceType = deviceTypes[which]
        }.setPositiveButton("Confirm") { _, _ ->
            deviceTypeSelectedCallback.onDeviceTypeSelected(selectedDeviceType, usbDevice)
        }.setNegativeButton("Cancel") { _, _ ->
            Log.d(debugTag, "Device selection cancelled")
        }

        return builder.create()
    }
}