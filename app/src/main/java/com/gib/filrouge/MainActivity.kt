package com.gib.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import com.gib.filrouge.network.Api
import com.gib.filrouge.user.AuthenticationActivity

// The activity launched whe the user launches the app.
class MainActivity : AppCompatActivity() {

    // The launcher used to launch the authentication activity
    // if the API token is not found in shared preferences.
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieves the API token from shared preferences.
        val token = PreferenceManager.getDefaultSharedPreferences(Api.appContext).getString("auth_token_key", "")
        // If the token does not exist, we redirect to the authentication
        // activity to retrieve it from the API via a login or signup process.
        if(token == "") {
            activityLauncher.launch(Intent(this, AuthenticationActivity::class.java))
        }
    }
    
}