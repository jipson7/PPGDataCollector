package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.felhr.usbserial.UsbSerialDevice

abstract class AbstractDeviceReader(val context: Context, val device: UsbDevice): Runnable {

    val tag = "DeviceReader"


    override fun run() {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceConnection = usbManager.openDevice(device)
        val usbSerial = UsbSerialDevice.createUsbSerialDevice(device, deviceConnection)
        usbSerial.open()
        usbSerial.read {
            onDataReceived(it)
        }
    }

    abstract fun onDataReceived(bytes: ByteArray)

}