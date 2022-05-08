package tk.nikomitk.dooropenerhalfnew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logTextView: TextView = view.findViewById(R.id.logTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.logTextView.text = dataSet[position]
        holder.itemView.setBackgroundResource(R.drawable.customborder_white)
        if (dataSet[position].substring(0, 15)
                .startsWith("[ERROR]")
        ) holder.itemView.setBackgroundResource(R.drawable.customborder_red)
        else if (dataSet[position].substring(0, 15)
                .startsWith("[WARN")
        ) holder.itemView.setBackgroundResource(R.drawable.customborder_yellow)
    }

    override fun getItemCount() = dataSet.size
}