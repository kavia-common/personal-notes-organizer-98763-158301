package org.example.app.model

// PUBLIC_INTERFACE
/**
 * Domain model for a Note used by the app UI and repository.
 */
data class Note(
    val id: Long? = null,
    val title: String,
    val contentHtml: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    fun tagsCsv(): String = tags.joinToString(",") { it.trim() }
    companion object {
        fun fromCsv(csv: String): List<String> =
            csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
