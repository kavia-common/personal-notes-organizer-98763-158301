package org.example.notesdb

// PUBLIC_INTERFACE
/**
 * Data transfer object representing a Note row in the database.
 *
 * @property id The primary key of the note. 0 means not yet persisted.
 * @property title The note title (non-empty).
 * @property contentHtml The note content stored as HTML to preserve simple formatting (bold/italic).
 * @property tagsCsv Comma-separated tags string. May be empty.
 * @property createdAt Epoch millis when the note was created.
 * @property updatedAt Epoch millis when the note was last updated.
 */
data class NoteRecord(
    val id: Long = 0L,
    val title: String,
    val contentHtml: String,
    val tagsCsv: String,
    val createdAt: Long,
    val updatedAt: Long
)
