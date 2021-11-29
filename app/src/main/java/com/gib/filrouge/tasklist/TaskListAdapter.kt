package com.gib.filrouge.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gib.filrouge.R

class TaskListAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    var onClickDelete: (Task) -> Unit = {};

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val deleteButton: ImageButton = itemView.findViewById<ImageButton>(R.id.deleteButton);

        fun bind(task: Task) {
            val taskTitleTextView = itemView.findViewById<TextView>(R.id.task_title);
            taskTitleTextView.setText(task.title);
            val taskDescriptionTextView = itemView.findViewById<TextView>(R.id.task_description);
            taskDescriptionTextView.setText(task.description);

            deleteButton.setOnClickListener({ onClickDelete(task); });
        }
    }

    override fun getItemCount(): Int = taskList.size;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false);
        return TaskViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position]);
    }

}
