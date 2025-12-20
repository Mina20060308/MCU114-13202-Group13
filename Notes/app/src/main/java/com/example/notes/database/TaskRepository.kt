package com.example.notes.database

import android.content.ContentValues
import android.content.Context

class TaskRepository(context: Context) {

    private val dbHelper = TaskDatabaseHelper(context)

    /** 新增任務 */
    fun insertTask(task: Task) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COL_TITLE, task.title)
            put(TaskDatabaseHelper.COL_DATE, task.date)
            put(TaskDatabaseHelper.COL_TIME, task.time)
            put(TaskDatabaseHelper.COL_PERIOD, task.period)
            put(TaskDatabaseHelper.COL_IS_DONE, if (task.isDone) 1 else 0)
        }
        db.insert(TaskDatabaseHelper.TABLE_TASKS, null, values)
        db.close()
    }

    /** 取得所有任務 */
    fun getAllTasks(): List<Task> {
        val list = mutableListOf<Task>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            TaskDatabaseHelper.TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "${TaskDatabaseHelper.COL_ID} DESC"
        )

        while (cursor.moveToNext()) {
            val task = Task(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COL_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COL_TITLE)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COL_DATE)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COL_TIME)),
                period = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COL_PERIOD)),
                isDone = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COL_IS_DONE)) == 1
            )
            list.add(task)
        }

        cursor.close()
        db.close()
        return list
    }

    /** 更新完成狀態 */
    fun updateTaskDone(id: Int, isDone: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COL_IS_DONE, if (isDone) 1 else 0)
        }
        db.update(
            TaskDatabaseHelper.TABLE_TASKS,
            values,
            "${TaskDatabaseHelper.COL_ID}=?",
            arrayOf(id.toString())
        )
        db.close()
    }
}