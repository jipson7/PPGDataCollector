package ca.utoronto.caleb.ppgdatacollector.ui

import android.hardware.usb.UsbDevice

interface DeviceTypeSelectedCallback {
    fun onDeviceTypeSelected(deviceType: String, device: UsbDevice)
}