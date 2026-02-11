package com.jotangi.cxms.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.R
import com.jotangi.cxms.ui.home.Appointment2
import com.jotangi.cxms.ui.home.TreatmentItem

class TreatmentAdapter(private val items: List<TreatmentItem>) : RecyclerView.Adapter<TreatmentAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_treatment, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val price: TextView = view.findViewById(R.id.price)
        private val therapyCount: TextView = view.findViewById(R.id.therapy_count)
        private val therapyPrice: TextView = view.findViewById(R.id.therapy_price)
        private val appointmentsRecyclerView: RecyclerView = view.findViewById(R.id.recyclerViewAppointments)

        fun bind(item: TreatmentItem) {
            title.text = item.title
            price.text = "$${item.price}"

            if (item.appointments.isNotEmpty()) {
                therapyCount.visibility = View.VISIBLE
                therapyPrice.visibility = View.VISIBLE

                therapyCount.text = "${item.appointments.size} 次療程"
                therapyPrice.text = "$${item.price * item.appointments.size}"
            } else {
                therapyCount.visibility = View.GONE
                therapyPrice.visibility = View.GONE
            }

            appointmentsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            appointmentsRecyclerView.adapter = AppointmentAdapter(item.appointments)
        }
    }

    class AppointmentAdapter(private val appointments: List<Appointment2>) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment2, parent, false)
            return AppointmentViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
            holder.bind(appointments[position])
        }

        override fun getItemCount(): Int = appointments.size

        inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val dateTime: TextView = view.findViewById(R.id.dateTime)

            fun bind(appointment: Appointment2) {
                dateTime.text = appointment.dateTime
            }
        }
    }
}
