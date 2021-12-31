package com.gib.filrouge.task

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.gib.filrouge.R
import com.gib.filrouge.form.FormActivity
import com.gib.filrouge.Api
import com.gib.filrouge.user.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

// The fragment showing the task list to the user.
class TaskListFragment : Fragment() {

    // This fragment uses a recycler view to show the
    // different user's tasks.
    // This is the list adapter for that recycler view.
    private val adapter = TaskListAdapter()

    // The view models for user- and task-related logic.
    private val taskViewModel: TaskViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    // The navigation controller managing this fragment.
    private lateinit var navController: NavController

    // Used to launch the form activity when the user wants
    // to create or edit a task.
    // The lambda catches the result sent back by the form
    // activity when the user has finished editing or creating
    // the task.
    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // We retrieve the task instance embedded in the intent
        // sent back by the form activity.
        val task = result.data?.getSerializableExtra("task") as Task?
        if(task != null) {
            // This ultimately sends the task data to the API.
            taskViewModel.addOrEdit(task)
            // We refresh the recycler view.
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // We retrieve the navigation controller.
        navController = findNavController()

        // Retrieves the API token from shared preferences.
        val token = PreferenceManager.getDefaultSharedPreferences(Api.appContext).getString("auth_token_key", "")
        // If the token does not exist, we redirect to the authentication
        // activity to retrieve it from the API via a login or signup process.
        if(token == "") {
            navController.navigate(R.id.action_taskListFragment_to_authenticationFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieving some views of the layout.
        // The header is used to display the user's first and last names.
        val header = view?.findViewById<TextView>(R.id.fragment_task_list_header)
        val avatar = view?.findViewById<ImageView>(R.id.fragment_task_list_avatar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_task_list_recycler_view)
        val addButton = view.findViewById<FloatingActionButton>(R.id.fragment_task_list_add_task_button)

        // Configuring the recycler view of the fragment.
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        // Bellow, we give proper definitions to the callback
        // functions declared in the TaskListAdapter class.
        adapter.onClickDelete = { task ->
            // This ultimately deletes the task on the API.
            taskViewModel.delete(task)
            adapter.notifyDataSetChanged()
        }
        adapter.onClickEdit = { task ->
            // When the user clicks on a task's edit button,
            // we launch the form activity by passing it the task
            // instance the user wants to edit.
            formLauncher.launch(Intent(activity, FormActivity::class.java).putExtra("task", task))
        }

        // Setting some callback functions for click events.
        avatar?.setOnClickListener {
            // When the user's avatar is clicked, we redirect
            // to the user info fragment.
            navController.navigate(R.id.action_taskListFragment_to_userInfoFragment)
        }
        addButton.setOnClickListener {
            // Launches the form activity when the add button is clicked.
            formLauncher.launch(Intent(activity, FormActivity::class.java));
        }

        // Collecting the state flows.
        // Basically, we define bellow what happens when
        // the task list and the user's avatar change (by
        // linking callbacks to the state flows that represent them
        // inside the view models).
        lifecycleScope.launch {
            taskViewModel.taskList.collect { newList ->
                // The task list has changed:
                // we notify the recycler view's adapter to refresh the view.
                adapter.submitList(newList);
                adapter.notifyDataSetChanged();
            }
        }
        lifecycleScope.launch {
            userViewModel.userInfo.collect { userInfo ->
                // User's info have changed.
                // Using Coil to load the image located at the URI pointed to
                // by UserInfo::avatar.
                avatar?.load(userInfo?.avatar ?: "https://goo.gl/gEgYUd") {
                    // The asset that gets displayed as the user's avatar
                    // if Coil fails to load the right image.
                    error(R.drawable.ic_launcher_background);
                    // Circle-cropping the avatar.
                    transformations(CircleCropTransformation());
                }
                // Refreshing the info displayed within the header.
                header?.text = """${userInfo?.firstName}
                |${userInfo?.lastName}""".trimMargin();
            }
        }
    }

    override fun onResume() {
        // Fetching the task list from the API.
        taskViewModel.refresh();
        // Fetching the user's info from the API.
        userViewModel.refresh();

        super.onResume();
    }

}