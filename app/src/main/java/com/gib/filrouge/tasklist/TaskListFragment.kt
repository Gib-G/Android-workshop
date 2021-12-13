package com.gib.filrouge.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gib.filrouge.R
import com.gib.filrouge.form.FormActivity
import com.gib.filrouge.network.Api
import com.gib.filrouge.network.TasksRepository
import com.gib.filrouge.network.UserInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class TaskListFragment : Fragment() {

    // The list of default tasks.
    /*
    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    );
    */

    private var headerTextView : TextView? = null;

    private val tasksRepository = TasksRepository();

    private val adapter = TaskListAdapter(tasksRepository.taskList.value);

    // Used to launch the form activity (FormActivity.kt).
    // In the lambda, we retrieve the intent sent back to the main activity
    // by the form activity.
    // We then process that intent accordingly.
    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        // Get the task instance embedded in the intent.
        val newTask = result.data?.getSerializableExtra("task") as Task;

        lifecycleScope.launch {

            tasksRepository.updateOrCreateTask(newTask);

        }

        // In any case, notify for changes!
        adapter.notifyDataSetChanged();

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

        // Gets the header text view.
        headerTextView = activity?.findViewById<TextView>(R.id.header);

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

            lifecycleScope.launch {

                tasksRepository.deleteTask(task);

            }

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

        lifecycleScope.launch {

            // on lance une coroutine car `collect` est `suspend`
            tasksRepository.taskList.collect { newList ->

                adapter.setTaskList(newList);

                adapter.notifyDataSetChanged();

            }

        }

    }

    override fun onResume() {

        super.onResume();

        // Retrieving user info from the API.
        // GET request to the API with the Api.userWebService.getInfo() method.
        // Ici on ne va pas gérer les cas d'erreur donc on force le crash avec "!!"
        // launch launches asynchronous code (a coroutine) in which you can call functions
        // declared as "suspend".
        lifecycleScope.launch {

            // This method is declared as "suspend".
            var userInfo = Api.userWebService.getInfo().body()!!;

            // Putting user info in the header text view.
            headerTextView?.text = """${userInfo?.firstName}
                |${userInfo?.lastName}""".trimMargin();

            // Refreshing tasks repo.
            tasksRepository.refresh();

        }

    }
}