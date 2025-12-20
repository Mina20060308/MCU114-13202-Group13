package com.example.notes.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // 建立任務資料表（含早 / 午 / 晚）
        db.execSQL(
            "CREATE TABLE $TABLE_TASKS (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_TITLE TEXT NOT NULL, " +
                    "$COL_DATE TEXT NOT NULL, " +
                    "$COL_TIME TEXT, " +
                    "$COL_PERIOD TEXT NOT NULL, " +   //  新增：早 / 午 / 晚
                    "$COL_IS_DONE INTEGER DEFAULT 0" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DATE = "date"
        const val COL_TIME = "time"
        const val COL_PERIOD = "period"   //  新增這一行
        const val COL_IS_DONE = "is_done" //  建議用底線
    }
}