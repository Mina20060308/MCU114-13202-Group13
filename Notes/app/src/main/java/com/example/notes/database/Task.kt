package com.example.notes.database

data class Task(
    var id: Int = 0,
    var title: String,
    var date: String = "",
    var time: String = "",
    var period: String,
    var isDone: Boolean = false,
    var userId: Int
)
