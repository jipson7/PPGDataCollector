package ca.utoronto.caleb.ppgdatacollector.ui

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.app.AlertDialog
import android.hardware.usb.UsbDevice
import android.util.Log
import ca.utoronto.caleb.ppgdatacollector.MainActivity
import ca.utoronto.caleb.ppgdatacollector.Sensor


class SelectDeviceDialogFragment: DialogFragment() {

    private val debugTag = "device_dialog"

    lateinit var deviceTypeSelectedCallback: DeviceTypeSelectedCallback

    lateinit var usbDevice: UsbDevice

    var selectedDeviceType: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select device type: ")
        builder.setSingleChoiceItems(Sensor.DEVICE_TYPES, -1) { _, which ->
            selectedDeviceType = Sensor.DEVICE_TYPES[which]
        }.setPositiveButton("Confirm") { _, _ ->
            if (selectedDeviceType != null) {
                deviceTypeSelectedCallback.onDeviceTypeSelected(selectedDeviceType!!, usbDevice)
            } else {
                throw RuntimeException("Must select a device type");
            }

        }.setNegativeButton("Cancel") { _, _ ->
            Log.d(debugTag, "Device selection cancelled")
        }

        return builder.create()
    }
}