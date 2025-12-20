package com.example.notes.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserRepository(context: Context) {

    private val dbHelper = UserDatabaseHelper(context)

    /** 註冊使用者 */
    fun registerUser(email: String, password: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserDatabaseHelper.COL_EMAIL, email)
            put(UserDatabaseHelper.COL_PASSWORD, password)
        }
        db.insert(UserDatabaseHelper.TABLE_USERS, null, values)
        db.close()
    }

    /** 檢查 Email 是否存在 ✅（你缺的就是這個） */
    fun isEmailExists(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            arrayOf(UserDatabaseHelper.COL_ID),
            "${UserDatabaseHelper.COL_EMAIL}=?",
            arrayOf(email),
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    /** 登入驗證 */
    fun loginUser(email: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            arrayOf(UserDatabaseHelper.COL_ID),
            "${UserDatabaseHelper.COL_EMAIL}=? AND ${UserDatabaseHelper.COL_PASSWORD}=?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        val success = cursor.count > 0
        cursor.close()
        db.close()
        return success
    }
}
