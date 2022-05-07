package tk.nikomitk.dooropenerhalfnew

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.File

class CustomAdapter(private val dataSet: ArrayList<Otp>, private val otpActivity: OTPActivity) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val otpText: TextView = view.findViewById(R.id.otpText)
        val expirationText: TextView = view.findViewById(R.id.exprationText)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val copyButton: Button = view.findViewById(R.id.copyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.otp_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.otpText.text = dataSet[position].pin
        holder.expirationText.text = dataSet[position].expirationDate
        holder.deleteButton.setOnClickListener {
            otpActivity.removeOtp(position)
        }
        holder.copyButton.setOnClickListener {
            (otpActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                ClipData.newPlainText("text", holder.otpText.text.toString()))
        }
    }

    override fun getItemCount() = dataSet.size

}