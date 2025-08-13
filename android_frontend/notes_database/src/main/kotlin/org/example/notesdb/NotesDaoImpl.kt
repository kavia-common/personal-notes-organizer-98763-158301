package org.example.notesdb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

/**
 * SQLite-backed implementation of NotesDao.
 */
class NotesDaoImpl(context: Context) : NotesDao {

    private val dbHelper = NotesDbHelper(context.applicationContext)

    override fun getAll(): List<NoteRecord> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDbHelper.TABLE_NOTES,
            PROJECTION,
            null,
            null,
            null,
            null,
            "${NotesDbHelper.COL_UPDATED_AT} DESC"
        )
        return cursor.use { c -> buildListFromCursor(c) }
    }

    override fun search(query: String): List<NoteRecord> {
        val db = dbHelper.readableDatabase
        val like = "%${query.trim()}%"
        val cursor = db.query(
            NotesDbHelper.TABLE_NOTES,
            PROJECTION,
            "${NotesDbHelper.COL_TITLE} LIKE ? OR ${NotesDbHelper.COL_CONTENT_HTML} LIKE ? OR ${NotesDbHelper.COL_TAGS} LIKE ?",
            arrayOf(like, like, like),
            null,
            null,
            "${NotesDbHelper.COL_UPDATED_AT} DESC"
        )
        return cursor.use { c -> buildListFromCursor(c) }
    }

    override fun getById(id: Long): NoteRecord? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDbHelper.TABLE_NOTES,
            PROJECTION,
            "${NotesDbHelper.COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use { c ->
            return if (c.moveToFirst()) mapRow(c) else null
        }
    }

    override fun insert(note: NoteRecord): Long {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(NotesDbHelper.COL_TITLE, note.title)
            put(NotesDbHelper.COL_CONTENT_HTML, note.contentHtml)
            put(NotesDbHelper.COL_TAGS, note.tagsCsv)
            put(NotesDbHelper.COL_CREATED_AT, if (note.createdAt > 0) note.createdAt else now)
            put(NotesDbHelper.COL_UPDATED_AT, now)
        }
        val db = dbHelper.writableDatabase
        return db.insert(NotesDbHelper.TABLE_NOTES, null, values)
    }

    override fun update(note: NoteRecord): Boolean {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(NotesDbHelper.COL_TITLE, note.title)
            put(NotesDbHelper.COL_CONTENT_HTML, note.contentHtml)
            put(NotesDbHelper.COL_TAGS, note.tagsCsv)
            put(NotesDbHelper.COL_CREATED_AT, note.createdAt)
            put(NotesDbHelper.COL_UPDATED_AT, now)
        }
        val db = dbHelper.writableDatabase
        val rows = db.update(
            NotesDbHelper.TABLE_NOTES,
            values,
            "${NotesDbHelper.COL_ID} = ?",
            arrayOf(note.id.toString())
        )
        return rows > 0
    }

    override fun delete(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            NotesDbHelper.TABLE_NOTES,
            "${NotesDbHelper.COL_ID} = ?",
            arrayOf(id.toString())
        )
        return rows > 0
    }

    private fun buildListFromCursor(cursor: Cursor): List<NoteRecord> {
        val list = ArrayList<NoteRecord>(cursor.count)
        if (cursor.moveToFirst()) {
            do {
                list.add(mapRow(cursor))
            } while (cursor.moveToNext())
        }
        return list
    }

    private fun mapRow(c: Cursor): NoteRecord {
        val id = c.getLong(c.getColumnIndexOrThrow(NotesDbHelper.COL_ID))
        val title = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COL_TITLE))
        val content = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COL_CONTENT_HTML))
        val tags = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COL_TAGS)) ?: ""
        val createdAt = c.getLong(c.getColumnIndexOrThrow(NotesDbHelper.COL_CREATED_AT))
        val updatedAt = c.getLong(c.getColumnIndexOrThrow(NotesDbHelper.COL_UPDATED_AT))
        return NoteRecord(
            id = id,
            title = title,
            contentHtml = content,
            tagsCsv = tags,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        private val PROJECTION = arrayOf(
            NotesDbHelper.COL_ID,
            NotesDbHelper.COL_TITLE,
            NotesDbHelper.COL_CONTENT_HTML,
            NotesDbHelper.COL_TAGS,
            NotesDbHelper.COL_CREATED_AT,
            NotesDbHelper.COL_UPDATED_AT
        )
    }
}
