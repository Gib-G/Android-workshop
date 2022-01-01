package com.gib.filrouge.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.gib.filrouge.R

// This fragment gets displayed when the user is
// prompted to authenticate.
// User is asked to log in or sign up.
class AuthenticationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        // Setting callback functions for click events on the buttons.
        val loginButton = view.findViewById<Button>(R.id.fragment_authentication_login_button)
        val signupButton = view.findViewById<Button>(R.id.fragment_authentication_signup_button)
        loginButton?.setOnClickListener {
            navController.navigate(R.id.action_authenticationFragment_to_loginFragment)
        }
        signupButton?.setOnClickListener {
            navController.navigate(R.id.action_authenticationFragment_to_signupFragment)
        }
    }

}