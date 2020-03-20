package ru.mperika.simplenotes.data_source

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.mperika.simplenotes.DBHelper
import ru.mperika.simplenotes.NOTES_TABLE_NAME

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
    cursor.close()

    return notesList
}

fun isImageURIMultiplyUsing(context: Context, uri: Uri) : Boolean {
    val db = DBHelper(context).readableDatabase
    val cursor = db.rawQuery("SELECT COUNT(*) AS count FROM $NOTES_TABLE_NAME WHERE n_image = '${uri.toString()}'", null)
    val countIndex = cursor.getColumnIndex("count")
    cursor.moveToNext()
    val cursorValue = cursor.getInt(countIndex)
    val result = cursor.getInt(countIndex) > 1
    cursor.close()
    return result
}