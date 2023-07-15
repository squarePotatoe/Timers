package com.mjdoescode.simpletimerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mjdoescode.simpletimerapp.databinding.RecyclerTimestampBinding
import com.mjdoescode.simpletimerapp.entities.TimerEntity

class TimerAdapter(private val time: ArrayList<TimerEntity>): RecyclerView.Adapter<TimerAdapter.MainViewHolder>() {
    inner class MainViewHolder(private val binding: RecyclerTimestampBinding) : ViewHolder(binding.root){
        val timerId = binding.textId
        val timerText = binding.timerText
        val timeDifference = binding.timeDifference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RecyclerTimestampBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun getItemCount(): Int {
        return time.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val timeStamp = time[time.size - position - 1]

        holder.timerId.text = timeStamp.id.toString()
        holder.timerText.text = timeStamp.time
        holder.timeDifference.text = timeStamp.timeDiff
    }
}