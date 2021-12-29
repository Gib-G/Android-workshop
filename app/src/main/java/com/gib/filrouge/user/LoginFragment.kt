package com.gib.filrouge.user

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings.Global.putString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import com.gib.filrouge.MainActivity
import com.gib.filrouge.R
import com.gib.filrouge.network.Api
import com.gib.filrouge.tasklist.Task

class LoginFragment : Fragment() {

    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var loginButton: Button? = null

    private val userInfoViewModel = UserInfoViewModel()

    // The launcher used to launch the main activity
    // when login is successful.
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieving the input text fields (for email and password).
        emailField = view.findViewById(R.id.fragment_login_email_field)
        passwordField = view.findViewById(R.id.fragment_login_password_field)
        // Defining what happens when the login button
        // is pressed.
        loginButton = view.findViewById(R.id.fragment_login_login_button)
        loginButton?.setOnClickListener {
            // Retrieving what the user has filled in the form.
            val email = emailField?.text.toString()
            val password = passwordField?.text.toString()
            // Checking that the user is not sending empty
            // data before proceeding.
            if(email != "" && password != "") {
                userInfoViewModel.login(LoginForm(email, password))
                // Login failed.
                if(userInfoViewModel.loginResponse == null) {
                    Toast.makeText(context, "Unknown email - password combination", Toast.LENGTH_LONG).show()
                }
                // Login successful.
                else {
                    // We add the token sent back by the API
                    // to shared preferences.
                    PreferenceManager.getDefaultSharedPreferences(context).edit {
                        putString("auth_token_key", userInfoViewModel.loginResponse?.apiToken)
                    }
                    Toast.makeText(context, "Welcome", Toast.LENGTH_LONG).show()
                    // Launching the main activity to display the
                    // task list.
                    activityLauncher.launch(Intent(activity, MainActivity::class.java))
                }
            }
            else {
                Toast.makeText(context, "Please fill in the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

}