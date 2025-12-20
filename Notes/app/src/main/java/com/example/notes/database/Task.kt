package com.example.notes.database

data class Task(
    val id: Int = 0,
    val title: String,
    val date: String,
    val time: String?,
    val period: String,
    val isDone: Boolean = false
)