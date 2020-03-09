package ru.mperika.simplenotes.data_source

import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Note(var id : Int = -1, var imageURI: Uri?, var noteHeader: String, var noteText: String) : Parcelable {
    constructor(imageUri: Uri?, noteHeader: String, noteText: String) : this(-1, imageUri, noteHeader, noteText)
    constructor(id: Int, imageURI: String, noteHeader: String, noteText: String) : this(id, Uri.parse(imageURI), noteHeader, noteText)

}

/**
 * Фабричная функция для получения значений из курсора базы 
 */
fun createNotesListFromCursor(cursor: Cursor) : ArrayList<Note> {
    val notesList = arrayListOf<Note>()
    val id_indx = cursor.getColumnIndex("n_id")
    val head_indx = cursor.getColumnIndex("n_header")
    val text_indx = cursor.getColumnIndex("n_text")
    val img_indx = cursor.getColumnIndex("n_image")
    while (!cursor.isLast){
        cursor.moveToNext()
        notesList.add(Note(cursor.getInt(id_indx), cursor.getString(img_indx), cursor.getString(head_indx), cursor.getString(text_indx)))
    }

    return notesList
}