package com.gib.filrouge.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gib.filrouge.R

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {

    // are they the same "entity" ? (usually same id)
    override fun areItemsTheSame(oldTask: Task, newTask: Task) = oldTask.id == newTask.id;

    // do they have the same data ? (content)
    override fun areContentsTheSame(oldTask: Task, newTask: Task) = oldTask.title == newTask.title && oldTask.description == newTask.description;

}

class TaskListAdapter(/*private var taskList: List<Task>*/) : androidx.recyclerview.widget.ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback) {

    // Buttons event handlers declaration.
    var onClickDelete: (Task) -> Unit = {};
    var onClickEdit: (Task) -> Unit = {};

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Tasks text views.
        private val taskTitleTextView = itemView.findViewById<TextView>(R.id.task_title);
        private val taskDescriptionTextView = itemView.findViewById<TextView>(R.id.task_description);

        // Buttons.
        private val deleteButton: ImageButton = itemView.findViewById<ImageButton>(R.id.deleteButton);
        private val editButton: ImageButton = itemView.findViewById<ImageButton>(R.id.editButton);

        fun bind(task: Task) {

            // Binding task's attributes to the text views.
            taskTitleTextView.setText(task.title);
            taskDescriptionTextView.setText(task.description);

            // Setting buttons' event handlers.
            // See TaskListFragment.kt for their proper implementation.
            deleteButton.setOnClickListener({ onClickDelete(task); });
            editButton.setOnClickListener({ onClickEdit(task); })

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        // Inflating the single task view.
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false);

        return TaskViewHolder(itemView);

    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        // Just calling our TaskViewHolder::bind method here.
        holder.bind(getItem(position));

    }

}
