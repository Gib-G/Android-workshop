package com.gib.filrouge.user

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import com.gib.filrouge.R

// Fragment displayed when the user needs to
// log in.
class LoginFragment : Fragment() {

    private val userViewModel = UserViewModel()

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
        val emailField = view.findViewById<EditText>(R.id.fragment_login_email_field)
        val passwordField = view.findViewById<EditText>(R.id.fragment_login_password_field)

        // Defining what happens when the login button
        // is pressed.
        val loginButton = view.findViewById<Button>(R.id.fragment_login_login_button)
        loginButton?.setOnClickListener {
            // Retrieving what the user has filled in the form.
            val email = emailField?.text.toString()
            val password = passwordField?.text.toString()
            // Checking that the user is not sending empty
            // data before proceeding.
            if(email != "" && password != "") {
                // Communicates with the API.
                userViewModel.login(LoginForm(email, password))
                // Login failed.
                if(userViewModel.authenticationResponse == null) {
                    Toast.makeText(context, "Unknown email - password combination", Toast.LENGTH_LONG).show()
                }
                // Login successful.
                else {
                    // We add the token sent back by the API
                    // to shared preferences.
                    PreferenceManager.getDefaultSharedPreferences(context).edit {
                        putString("auth_token_key", userViewModel.authenticationResponse?.apiToken)
                    }
                    // User logged in! We can redirect to the task list fragment.
                    findNavController().navigate(R.id.action_loginFragment_to_taskListFragment)
                }
            }
            // Incomplete form.
            else {
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

}