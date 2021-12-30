package com.gib.filrouge.tasklist

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.gib.filrouge.R
import com.gib.filrouge.form.FormActivity
import com.gib.filrouge.network.Api
import com.gib.filrouge.user.UserInfoFragment
import com.gib.filrouge.user.UserInfoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private var headerTextView : TextView? = null;
    private var avatar: ImageView? = null;

    private val adapter = TaskListAdapter();

    private val taskListViewModel: TaskListViewModel by viewModels();
    private val userInfoViewModel: UserInfoViewModel by viewModels();

    // Used to launch the form activity (FormActivity.kt).
    // In the lambda, we retrieve the intent sent back to the main activity
    // by the form activity.
    // We then process that intent accordingly.
    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        // Get the task instance embedded in the intent.
        val newTask = result.data?.getSerializableExtra("task") as Task;

        taskListViewModel.addOrEdit(newTask);

        // In any case, notify for changes!
        adapter.notifyDataSetChanged();

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieves the API token from shared preferences.
        val token = PreferenceManager.getDefaultSharedPreferences(Api.appContext).getString("auth_token_key", "")
        // If the token does not exist, we redirect to the authentication
        // activity to retrieve it from the API via a login or signup process.
        if(token == "") {
            //activityLauncher.launch(Intent(this, AuthenticationActivity::class.java))
            findNavController().navigate(R.id.action_taskListFragment_to_authenticationFragment)
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

        // Gets the header text view.
        headerTextView = activity?.findViewById<TextView>(R.id.header);

        // Gets the user avatar image view.
        avatar = view?.findViewById(R.id.avatar);

        avatar?.setOnClickListener {
            //formLauncher.launch(Intent(activity, UserInfoFragment::class.java));
            findNavController().navigate(R.id.action_taskListFragment_to_userInfoFragment)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(activity);
        recyclerView.adapter = adapter;

        val addButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton);
        addButton.setOnClickListener {
            // Launches the form activity when the add button is clicked.
            formLauncher.launch(Intent(activity, FormActivity::class.java));
        };

        adapter.onClickDelete = { task ->

            taskListViewModel.delete(task);

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
            taskListViewModel.taskList.collect { newList ->

                adapter.submitList(newList);

                adapter.notifyDataSetChanged();

            }

        }
        lifecycleScope.launch {
            userInfoViewModel.userInfo.collect { userInfo ->
                avatar?.load(userInfo?.avatar ?: "https://goo.gl/gEgYUd") {
                    // Parameter in case of error.
                    error(R.drawable.ic_launcher_background);
                    // Some transformations
                    transformations(CircleCropTransformation());
                }
                // Putting user info in the header text view.
                headerTextView?.text = """${userInfo?.firstName}
                |${userInfo?.lastName}""".trimMargin();
            }
        }

    }

    override fun onResume() {
        // Refreshing tasks view model.
        taskListViewModel.refresh();
        // Refreshing user view model.
        userInfoViewModel.refresh();

        super.onResume();

    }

}