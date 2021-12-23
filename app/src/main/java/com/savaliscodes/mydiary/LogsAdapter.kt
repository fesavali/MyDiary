package com.savaliscodes.mydiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//class adapter must contain a dataclass arraylist
class LogsAdapter(private val logsList : ArrayList<DiaryData>) : RecyclerView.Adapter<LogsAdapter.LogsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsAdapter.LogsViewHolder {
        //inflate recyclerview with custom data view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_rcv, parent, false)

        //return custom viewholder
        return LogsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogsAdapter.LogsViewHolder, position: Int) {
        //initialise data class and array position
        val diaryData : DiaryData = logsList[position]
        //bind data to views in custom viewholder
        holder.lTitle.text = diaryData.LogTitle
        holder.lContents.text = diaryData.LogContents
        holder.lTime.text = diaryData.LogTime
    }

    override fun getItemCount(): Int {
       //return a arraylist size of the data class
       return logsList.size
    }

     //initialise a Viewholder class that extends recyclerview.viewholder
    public class LogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         //get ref to all views in custom view
        val lTitle : TextView = itemView.findViewById(R.id.log_title)
        val lContents : TextView = itemView.findViewById(R.id.log_contents)
        val lTime : TextView = itemView.findViewById(R.id.log_time)
//        val uImg : ImageView = itemView.findViewById(R.id.my_image)
    }
}