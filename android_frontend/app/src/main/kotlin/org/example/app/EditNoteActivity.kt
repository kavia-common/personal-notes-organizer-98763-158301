package org.example.app

import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.example.app.data.NotesRepository
import org.example.app.data.NotesRepositoryImpl
import org.example.app.model.Note
import org.example.app.ui.ThemeUtils
import android.graphics.Typeface

// PUBLIC_INTERFACE
/**
 * Screen for creating or editing a single note, with basic formatting and tags.
 */
class EditNoteActivity : AppCompatActivity() {

    private lateinit var repository: NotesRepository

    private lateinit var titleInput: TextInputEditText
    private lateinit var contentInput: EditText
    private lateinit var tagsInput: TextInputEditText
    private lateinit var boldBtn: MaterialButton
    private lateinit var italicBtn: MaterialButton

    private var currentNoteId: Long? = null
    private var existingCreatedAt: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        repository = NotesRepositoryImpl(applicationContext)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleInput = findViewById(R.id.input_title)
        contentInput = findViewById(R.id.input_content)
        tagsInput = findViewById(R.id.input_tags)
        boldBtn = findViewById(R.id.btn_bold)
        italicBtn = findViewById(R.id.btn_italic)

        boldBtn.setOnClickListener { toggleStyle(Typeface.BOLD) }
        italicBtn.setOnClickListener { toggleStyle(Typeface.ITALIC) }

        val id = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (id > 0) {
            loadNote(id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            R.id.action_delete -> {
                deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleStyle(style: Int) {
        val start = contentInput.selectionStart
        val end = contentInput.selectionEnd
        if (start >= 0 && end > start) {
            val text = contentInput.text as Spannable
            val spans = text.getSpans(start, end, StyleSpan::class.java)
            var hasSpan = false
            for (span in spans) {
                if (span.style == style) {
                    text.removeSpan(span)
                    hasSpan = true
                }
            }
            if (!hasSpan) {
                text.setSpan(StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun loadNote(id: Long) {
        val note = repository.get(id)
        if (note != null) {
            currentNoteId = note.id
            existingCreatedAt = note.createdAt
            titleInput.setText(note.title)
            @Suppress("DEPRECATION")
            val spanned = Html.fromHtml(note.contentHtml, Html.FROM_HTML_MODE_COMPACT)
            contentInput.setText(spanned)
            tagsInput.setText(note.tags.joinToString(", "))
            supportActionBar?.title = getString(R.string.title_edit_note)
        }
    }

    private fun saveNote() {
        val title = titleInput.text?.toString()?.trim().orEmpty()
        val tagsCsv = tagsInput.text?.toString()?.trim().orEmpty()
        val tags = tagsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (title.isEmpty()) {
            val layout: TextInputLayout = findViewById(R.id.layout_title)
            layout.error = getString(R.string.error_title_required)
            return
        }

        val html = Html.toHtml(contentInput.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        val note = Note(
            id = currentNoteId,
            title = title,
            contentHtml = html,
            tags = tags,
            createdAt = existingCreatedAt
        )
        val id = repository.save(note)
        if (currentNoteId == null) currentNoteId = id
        finish()
    }

    private fun deleteNote() {
        val id = currentNoteId
        if (id != null) {
            repository.delete(id)
        }
        finish()
    }

    companion object {
        // PUBLIC_INTERFACE
        /** Intent extra for passing the note id to edit. */
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
