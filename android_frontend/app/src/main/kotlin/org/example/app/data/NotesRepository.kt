package org.example.app.data

import android.content.Context
import org.example.app.model.Note
import org.example.notesdb.NoteRecord
import org.example.notesdb.NotesDao
import org.example.notesdb.NotesDaoImpl

// PUBLIC_INTERFACE
/**
 * Abstraction for interacting with notes data source.
 */
interface NotesRepository {
    /** PUBLIC_INTERFACE Get all notes ordered by last updated. */
    fun getAll(): List<Note>

    /** PUBLIC_INTERFACE Search notes by text query. */
    fun search(query: String): List<Note>

    /** PUBLIC_INTERFACE Get a note by id or null. */
    fun get(id: Long): Note?

    /** PUBLIC_INTERFACE Save a note (insert or update). Returns id. */
    fun save(note: Note): Long

    /** PUBLIC_INTERFACE Delete a note by id. Returns true if deleted. */
    fun delete(id: Long): Boolean
}

/**
 * Implementation of NotesRepository backed by a local SQLite database via NotesDao.
 */
class NotesRepositoryImpl(context: Context) : NotesRepository {

    private val dao: NotesDao = NotesDaoImpl(context)

    override fun getAll(): List<Note> = dao.getAll().map { it.toDomain() }

    override fun search(query: String): List<Note> = dao.search(query).map { it.toDomain() }

    override fun get(id: Long): Note? = dao.getById(id)?.toDomain()

    override fun save(note: Note): Long {
        return if (note.id == null || note.id == 0L) {
            dao.insert(note.toRecord())
        } else {
            dao.update(note.toRecordWithId(note.id))
            note.id
        }
    }

    override fun delete(id: Long): Boolean = dao.delete(id)

    private fun NoteRecord.toDomain(): Note = Note(
        id = id,
        title = title,
        contentHtml = contentHtml,
        tags = Note.fromCsv(tagsCsv),
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Note.toRecord(): NoteRecord = NoteRecord(
        id = 0L,
        title = title,
        contentHtml = contentHtml,
        tagsCsv = tagsCsv(),
        createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun Note.toRecordWithId(existingId: Long): NoteRecord = NoteRecord(
        id = existingId,
        title = title,
        contentHtml = contentHtml,
        tagsCsv = tagsCsv(),
        createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
