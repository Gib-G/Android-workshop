package com.gib.filrouge.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.gib.filrouge.R
import com.gib.filrouge.Api
import com.gib.filrouge.task.Task
import java.util.*

// The activity launched when the user
// needs to add or edit a task.
class FormActivity : AppCompatActivity() {

    // The Task instance received from the main activity
    // (task list fragment) in case the user wants to edit
    // a specific task.
    // This attribute is null if the user just wants to
    // add a new task.
    private var task: Task? = null

    // The different views in the activity's layout.
    private lateinit var text: TextView
    private lateinit var taskNameField: EditText
    private lateinit var taskDescriptionField: EditText
    private lateinit var okButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Retrieving the different views in the activity's
        // layout.
        text = findViewById(R.id.activity_form_text)
        taskNameField = findViewById<EditText>(R.id.activity_form_task_name_field);
        taskDescriptionField = findViewById<EditText>(R.id.activity_form_task_description_field);
        okButton = findViewById<Button>(R.id.activity_form_ok_button);

        okButton.setOnClickListener {
            // We check that the user has at least given a name to the task.
            if(taskNameField.text.isEmpty()) {
                Toast.makeText(Api.appContext, "You will need some name for your task, no?", Toast.LENGTH_LONG).show()
            }
            else {
                // We create a new Task instance with the
                // data provided by the user in the form's fields,
                // regardless of whether the user edits an existing task,
                // or creates a new one.
                val newTask = Task(task?.id ?: UUID.randomUUID().toString(), taskNameField.text.toString(), taskDescriptionField.text.toString())

                // The activity returns an intent to the main activity
                // before it finishes. This intent contains the newly
                // created task instance.
                intent.putExtra("task", newTask)
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // When the activity is resumed, we retrieve the
        // Task instance that is embedded in the intent sent by
        // the main activity in the task list fragment.
        task = intent.getSerializableExtra("task") as Task?

        // In any case, we set some hints for the form's fields.
        taskNameField.hint = "Task name"
        taskDescriptionField.hint = "Task description (optional)"
        // We then adapt the layout of the activity based on
        // whether the task received is null
        // (user wants to add a new task) or not (user wants
        // to edit an existing task).
        if(task == null) {
            text.text = "Hooray! Let's add a new task!"
            okButton.text = "Create"
        }
        else {
            text.text = "Let's change a few things here..."
            taskNameField.setText(task?.name)
            taskDescriptionField.setText(task?.description)
            okButton.text = "Save"
        }
    }

}