package ca.utoronto.caleb.ppgdatacollector.readers

import android.content.Context
import android.hardware.usb.UsbDevice

class WristDeviceReader(context: Context, device: UsbDevice): AbstractDeviceReader(context, device) {
    override fun onDataReceived(bytes: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}