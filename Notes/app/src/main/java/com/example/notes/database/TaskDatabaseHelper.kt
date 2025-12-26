package com.example.notes.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "tasks.db"
        const val DATABASE_VERSION = 2
        const val TABLE_TASKS = "tasks"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DATE = "date"
        const val COL_TIME = "time"
        const val COL_PERIOD = "period"
        const val COL_IS_DONE = "is_done"
        const val COL_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_TASKS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_DATE TEXT,
                $COL_TIME TEXT,
                $COL_PERIOD TEXT,
                $COL_IS_DONE INTEGER DEFAULT 0,
                $COL_USER_ID INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // 新增資料並回傳自增 ID
    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val sql = """
            INSERT INTO $TABLE_TASKS ($COL_TITLE, $COL_DATE, $COL_TIME, $COL_PERIOD, $COL_IS_DONE, $COL_USER_ID)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val stmt = db.compileStatement(sql)
        stmt.bindString(1, task.title)
        stmt.bindString(2, task.date ?: "")
        stmt.bindString(3, task.time ?: "")
        stmt.bindString(4, task.period ?: "")
        stmt.bindLong(5, if (task.isDone) 1 else 0)
        stmt.bindLong(6, task.userId.toLong())

        val id = stmt.executeInsert()
        db.close()
        return id
    }

    // 取得使用者任務
    fun getTasksByUser(userId: Int): List<Task> {
        val db = readableDatabase
        val list = mutableListOf<Task>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COL_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME)),
                    period = cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD)),
                    isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_DONE)) == 1,
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID))
                )
                list.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // 更新完成狀態
    fun updateTaskDone(taskId: Int, isDone: Boolean) {
        val db = writableDatabase
        val sql = "UPDATE $TABLE_TASKS SET $COL_IS_DONE = ? WHERE $COL_ID = ?"
        db.execSQL(sql, arrayOf(if (isDone) 1 else 0, taskId))
        db.close()
    }
}
