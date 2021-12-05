package com.gib.filrouge.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.gib.filrouge.R
import com.gib.filrouge.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // The different view elements in the form activity.
        val taskNameInput = findViewById<EditText>(R.id.taskNameInput);
        val taskDescriptionInput = findViewById<EditText>(R.id.taskNameInput);
        val validateButton = findViewById<Button>(R.id.validateTaskCreation);

        // Retrieves the task embedded in the received intent if such a task exists.
        // If the intent does not contain any task, we are creating a new task.
        // Otherwise, we are editing an existing task.
        var task: Task? = intent.getSerializableExtra("task") as? Task?;

        // Retrieves the task title and description if the intent
        // contains a task instance.
        val taskTitle = task?.title ?: "New task";
        val taskDescription = task?.description ?: "This is a new task!";

        // Filling the input fields.
        taskNameInput.setText(taskTitle);
        taskDescriptionInput.setText(taskDescription);

        validateButton.setOnClickListener {

            // Retrieves user inputs in the text fields.
            val taskTitle = taskNameInput.text.toString();
            val taskDescription = taskDescriptionInput.text.toString();

            // Creates a new Task instance.
            val newTask = Task(id = task?.id ?: UUID.randomUUID().toString(), title = taskTitle, description = taskDescription);

            // Filling the returned intent with the newly created Task instance.
            intent.putExtra("task", newTask);
            setResult(RESULT_OK, intent);
            finish();
        };
    }
}