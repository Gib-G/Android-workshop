package com.gib.filrouge.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task
    (
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val name: String,
    @SerialName("description")
    val description: String
    ) : java.io.Serializable