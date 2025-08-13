package org.example.notesdb

// PUBLIC_INTERFACE
/**
 * Public DAO for CRUD operations on notes.
 */
interface NotesDao {
    /** PUBLIC_INTERFACE Retrieve all notes ordered by last update descending. */
    fun getAll(): List<NoteRecord>

    /** PUBLIC_INTERFACE Retrieve notes whose title or content matches the query (case-insensitive). */
    fun search(query: String): List<NoteRecord>

    /** PUBLIC_INTERFACE Retrieve a note by its id, or null if not found. */
    fun getById(id: Long): NoteRecord?

    /** PUBLIC_INTERFACE Insert a note. Returns the newly assigned id. */
    fun insert(note: NoteRecord): Long

    /** PUBLIC_INTERFACE Update an existing note. Returns true if a row was updated. */
    fun update(note: NoteRecord): Boolean

    /** PUBLIC_INTERFACE Delete a note by id. Returns true if a row was deleted. */
    fun delete(id: Long): Boolean
}
