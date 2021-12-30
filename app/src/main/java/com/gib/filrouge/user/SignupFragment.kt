package com.gib.filrouge.user

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
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

class SignupFragment : Fragment() {

    private var firstnameField: EditText? = null
    private var lastnameField: EditText? = null
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var passwordConfirmationField: EditText? = null
    private var signupButton: Button? = null

    private val userInfoViewModel = UserInfoViewModel()

    // The launcher used to launch the main activity
    // when signup is successful.
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
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieving the input text fields (for email and password).
        firstnameField = view.findViewById(R.id.fragment_signup_firstname)
        lastnameField = view.findViewById(R.id.fragment_signup_lastname)
        emailField = view.findViewById(R.id.fragment_signup_email_field)
        passwordField = view.findViewById(R.id.fragment_signup_password_field)
        passwordConfirmationField = view.findViewById(R.id.fragment_signup_repeat_password_field)

        // Defining what happens when the signup button
        // is pressed.
        signupButton = view.findViewById(R.id.fragment_signup_signup_button)
        signupButton?.setOnClickListener {

            // Retrieving what the user has filled in the form.
            val firstname = firstnameField?.text.toString()
            val lastname = lastnameField?.text.toString()
            val email = emailField?.text.toString()
            val password = passwordField?.text.toString()
            val passwordConfirmation = passwordConfirmationField?.text.toString()

            // A flag to indicate whether we can send the
            // data filled by the user to the API.
            var canSend = true
            // Bellow, we operate some verifications to the data
            // submitted by the user.
            // The API might already perform some of those verifications,
            // but we still carry them here to avoid sending invalid
            // data to the API.
            if(firstname == "") {
                canSend = false
                Toast.makeText(context, "Your firstname is needed", Toast.LENGTH_LONG).show()
            }
            // For the following verifications, we check if some
            // previous verification has already cancelled sending.
            // If so, no need to carry out extra verifs.
            // This is meant to avoid the user to be spammed with a
            // ton of toast messages displayed one after the other.
            if(canSend && lastname == "") {
                canSend = false
                Toast.makeText(context, "Your lastname is needed", Toast.LENGTH_LONG).show()
            }
            if(canSend && email == "") {
                canSend = false
                Toast.makeText(context, "Please fill in your email", Toast.LENGTH_LONG).show()
            }
            if(canSend && password == "") {
                canSend = false;
                Toast.makeText(context, "Please fill in a password", Toast.LENGTH_LONG).show()
            }
            if(canSend && password != passwordConfirmation) {
                canSend = false;
                Toast.makeText(context, "Passwords mismatch", Toast.LENGTH_LONG).show()
            }
            if(canSend) {
                userInfoViewModel.signUp(SignUpForm(firstname, lastname, email, password, passwordConfirmation))
                // Signup failed.
                if(userInfoViewModel.authenticationResponse == null) {
                    Toast.makeText(context, "Signup failed", Toast.LENGTH_LONG).show()
                }
                // Signup successful.
                else {
                    // We add the token sent back by the API
                    // to shared preferences.
                    PreferenceManager.getDefaultSharedPreferences(context).edit {
                        putString("auth_token_key", userInfoViewModel.authenticationResponse?.apiToken)
                    }
                    Toast.makeText(context, "Welcome", Toast.LENGTH_LONG).show()
                    // Launching the main activity to display the
                    // task list.
                    //activityLauncher.launch(Intent(activity, MainActivity::class.java))
                    findNavController().navigate(R.id.action_signupFragment_to_taskListFragment)
                }
            }
        }
    }

}