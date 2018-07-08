package ca.utoronto.caleb.ppgdatacollector.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.utoronto.caleb.ppgdatacollector.R
import ca.utoronto.caleb.ppgdatacollector.Sensor
import kotlinx.android.synthetic.main.device_info_item.view.*

class DeviceInfoAdapter(val sensors: MutableList<Sensor>, val context: Context): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.device_info_item, parent, false))
    }

    override fun getItemCount(): Int {
        return sensors.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sensor = sensors.get(position)
        holder.tvDeviceType.text = "(Type ${sensor.deviceType})"
        holder.tvDeviceName.text = sensor.deviceName
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvDeviceType = view.device_type
    val tvDeviceName = view.device_name
}