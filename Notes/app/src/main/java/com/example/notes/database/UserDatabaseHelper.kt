package com.example.notes.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_USERS (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_EMAIL TEXT UNIQUE NOT NULL, " +
                    "$COL_PASSWORD TEXT NOT NULL" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "notes.db"   // ✅ 跟 Task 共用同一個 DB
        const val DATABASE_VERSION = 2         // ⚠️ 一定要升版

        const val TABLE_USERS = "users"
        const val COL_ID = "id"                // ✅ 這行就是你缺的
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
    }
}
