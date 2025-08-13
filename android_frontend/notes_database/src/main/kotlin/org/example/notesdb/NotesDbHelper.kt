package org.example.notesdb

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLiteOpenHelper that manages the Notes database.
 *
 * Schema:
 *  - notes(id INTEGER PRIMARY KEY AUTOINCREMENT,
 *          title TEXT NOT NULL,
 *          content_html TEXT NOT NULL,
 *          tags TEXT,
 *          created_at INTEGER NOT NULL,
 *          updated_at INTEGER NOT NULL)
 */
internal class NotesDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NOTES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_CONTENT_HTML TEXT NOT NULL,
                $COL_TAGS TEXT,
                $COL_CREATED_AT INTEGER NOT NULL,
                $COL_UPDATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_notes_title ON $TABLE_NOTES($COL_TITLE)")
        db.execSQL("CREATE INDEX idx_notes_updated_at ON $TABLE_NOTES($COL_UPDATED_AT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Simple strategy for demo: drop and recreate. Real apps should migrate.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NOTES = "notes"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_CONTENT_HTML = "content_html"
        const val COL_TAGS = "tags"
        const val COL_CREATED_AT = "created_at"
        const val COL_UPDATED_AT = "updated_at"
    }
}
