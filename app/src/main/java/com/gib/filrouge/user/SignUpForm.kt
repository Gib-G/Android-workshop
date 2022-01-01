package com.gib.filrouge.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Data class meant to encapsulate the signup details
// provided by the user for sending to the API.
@Serializable
data class SignUpForm(
    @SerialName("firstname")
    val firstname: String,
    @SerialName("lastname")
    val lastname: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirmation: String,
)
