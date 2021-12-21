package com.savaliscodes.mydiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogsAdapter(private val LogsList : ArrayList<DiaryData>) : RecyclerView.Adapter<LogsAdapter.LogsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsAdapter.LogsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_rcv, parent, false)

        return LogsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogsAdapter.LogsViewHolder, position: Int) {

        val diaryData : DiaryData = LogsList[position]

        holder.lTitle.text = diaryData.LogTitle
        holder.lContents.text = diaryData.LogContents
        holder.lTime.text = diaryData.LogTime
    }

    override fun getItemCount(): Int {
       return LogsList.size
    }

    public class LogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val lTitle : TextView = itemView.findViewById(R.id.log_title)
        val lContents : TextView = itemView.findViewById(R.id.log_contents)
        val lTime : TextView = itemView.findViewById(R.id.log_time)
//        val uImg : ImageView = itemView.findViewById(R.id.my_image)
    }
}