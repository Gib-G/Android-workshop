package com.gib.filrouge.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gib.filrouge.R

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {

    override fun areItemsTheSame(oldTask: Task, newTask: Task) = oldTask.id == newTask.id;

    override fun areContentsTheSame(oldTask: Task, newTask: Task) = oldTask.name == newTask.name && oldTask.description == newTask.description;

}

class TaskListAdapter : androidx.recyclerview.widget.ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback) {

    // Forward declarations of the callback functions
    // called when the user clicks on a task's edit
    // or delete button.
    var onClickDelete: (Task) -> Unit = {}
    var onClickEdit: (Task) -> Unit = {}

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // The views comprising the task item layout.
        private val taskTitle = itemView.findViewById<TextView>(R.id.item_task_task_name)
        private val taskDescription = itemView.findViewById<TextView>(R.id.item_task_task_description)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.item_task_delete_button)
        private val editButton = itemView.findViewById<ImageButton>(R.id.item_task_edit_button)

        fun bind(task: Task) {
            // Binding task's attributes to the text views of the layout.
            taskTitle.text = task.name
            taskDescription.text = task.description

            // Setting buttons' event handlers.
            // See TaskListFragment.kt for their proper implementation.
            // I don't remember why we define them in the bind function...
            deleteButton.setOnClickListener {
                onClickDelete(task)
            }
            editButton.setOnClickListener {
                onClickEdit(task)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Inflating the item task layout.
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position));
    }

}
