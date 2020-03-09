package ru.mperika.simplenotes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val NOTES_TABLE_NAME = "note"

class DBHelper(context: Context) : SQLiteOpenHelper(context, "sNotes", null, 1)  {

    override fun onCreate(db: SQLiteDatabase?) {
        createNotesTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createNotesTable(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS note (" +
                " n_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                " n_header TEXT DEFAULT \"\"," +
                " n_text TEXT DEFAULT \"\"," +
                " n_image TEXT DEFAULT \"\");")
    }
}