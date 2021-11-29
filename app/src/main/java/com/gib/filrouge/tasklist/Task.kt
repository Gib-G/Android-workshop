package com.gib.filrouge.tasklist

import java.io.Serializable

data class Task(val id: String, val title: String, val description: String = "This is a task!") : Serializable {}