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

// Fragment that gets displayed when the user needs to
// create an account (signup).
class SignupFragment : Fragment() {

    private val userViewModel = UserViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieving the form's fields.
        val firstnameField = view.findViewById<EditText>(R.id.fragment_signup_firstname)
        val lastnameField = view.findViewById<EditText>(R.id.fragment_signup_lastname)
        val emailField = view.findViewById<EditText>(R.id.fragment_signup_email_field)
        val passwordField = view.findViewById<EditText>(R.id.fragment_signup_password_field)
        val passwordConfirmationField = view.findViewById<EditText>(R.id.fragment_signup_repeat_password_field)

        // Defining what happens when the signup button
        // is pressed.
        val signupButton = view.findViewById<Button>(R.id.fragment_signup_signup_button)
        signupButton?.setOnClickListener {
            // Retrieving what the user has filled in the form.
            val firstname = firstnameField?.text.toString()
            val lastname = lastnameField?.text.toString()
            val email = emailField?.text.toString()
            val password = passwordField?.text.toString()
            val passwordConfirmation = passwordConfirmationField?.text.toString()

            // Bellow, we operate some verifications to the data
            // submitted by the user.
            // The API might already perform some of those verifications,
            // but we still carry them here to avoid sending invalid
            // data to the API.
            if(firstname == "") {
                Toast.makeText(context, "Your firstname is needed", Toast.LENGTH_LONG).show()
            }
            else if(lastname == "") {
                Toast.makeText(context, "Your lastname is needed", Toast.LENGTH_LONG).show()
            }
            else if(email == "") {
                Toast.makeText(context, "Please fill in your email", Toast.LENGTH_LONG).show()
            }
            else if(password == "") {
                Toast.makeText(context, "Please fill in a password", Toast.LENGTH_LONG).show()
            }
            else if(password != passwordConfirmation) {
                Toast.makeText(context, "Passwords mismatch", Toast.LENGTH_LONG).show()
            }
            // Ok! Form is complete!
            else {
                // Calls the API.
                userViewModel.signUp(SignUpForm(firstname, lastname, email, password, passwordConfirmation))
                // Signup failed.
                if(userViewModel.authenticationResponse == null) {
                    Toast.makeText(context, "Signup failed", Toast.LENGTH_LONG).show()
                }
                // Signup successful.
                else {
                    // We add the token sent back by the API
                    // to shared preferences.
                    PreferenceManager.getDefaultSharedPreferences(context).edit {
                        putString("auth_token_key", userViewModel.authenticationResponse?.apiToken)
                    }
                    // Account successfully created! We can redirect the user to the task list fragment.
                    findNavController().navigate(R.id.action_signupFragment_to_taskListFragment)
                }
            }
        }
    }

}