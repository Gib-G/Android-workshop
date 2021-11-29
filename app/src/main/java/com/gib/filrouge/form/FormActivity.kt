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

        val validateButton = findViewById<Button>(R.id.validateTaskCreation);

        validateButton.setOnClickListener({

            val taskTitle = findViewById<EditText>(R.id.taskNameInput).text.toString();
            val taskDescription = findViewById<EditText>(R.id.taskDescriptionInput).text.toString();

            // Instanciation d'un nouvel objet [Task]
            val newTask = Task(id = UUID.randomUUID().toString(), title = taskTitle, description = taskDescription);
            
            intent.putExtra("task", newTask);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}