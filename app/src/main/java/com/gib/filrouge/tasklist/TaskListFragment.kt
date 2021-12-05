package com.gib.filrouge.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gib.filrouge.R
import com.gib.filrouge.form.FormActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class TaskListFragment : Fragment() {

    // The list of default tasks.
    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    );

    private val adapter = TaskListAdapter(taskList);

    // Used to launch the form activity (FormActivity.kt).
    // In the lambda, we retrieve the intent sent back to the main activity
    // by the form activity.
    // We then process that intent accordingly.
    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        // Get the task instance embedded in the intent.
        val task = result.data?.getSerializableExtra("task") as? Task;

        if (task != null) {
            taskList.add(task);
            adapter.notifyDataSetChanged();
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Ne rien mettre avant d'avoir inflate notre layout (fragment_task_list.xml).
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        return rootView;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState);

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(activity);
        recyclerView.adapter = adapter;

        val addButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton);
        addButton.setOnClickListener {/*
            taskList.add(
                Task(
                    id = UUID.randomUUID().toString(),
                    title = "Task ${taskList.size + 1}"
                )
            );*/
            // Launches the form activity when the add button is clicked.
            formLauncher.launch(Intent(activity, FormActivity::class.java));
            // Notifier l'adapter!
            //adapter.notifyDataSetChanged();
        };

        adapter.onClickDelete = { task ->
            taskList.remove(task);
            adapter.notifyDataSetChanged();
        };

        adapter.onClickEdit = { task ->

            // Creating the intent to send to the creation/edit form to edit task.
            val intent = Intent(activity, FormActivity::class.java);

            // Putting the task instance to edit in the intent.
            intent.putExtra("task", task);

            // Launching the form activity with the created intent.
            formLauncher.launch(intent);

        }
    }
}