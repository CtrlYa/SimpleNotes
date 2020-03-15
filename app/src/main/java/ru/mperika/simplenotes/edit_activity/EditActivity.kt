package ru.mperika.simplenotes.edit_activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import ru.mperika.simplenotes.DBHelper
import ru.mperika.simplenotes.R
import ru.mperika.simplenotes.data_source.Note
import java.io.File
import java.io.InputStream


class EditActivity : AppCompatActivity() {

    var imageURI: Uri? = null
    lateinit var header: String
    lateinit var text: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        button.setOnClickListener {
            save()

        }

        floatingActionButton.setOnClickListener {
            startActivityForResult(Intent.createChooser(createIntent(), "Select a file"), 1)
        }
    }

    private fun save() {
        var note = Note(imageURI, headerTV.text.toString(), textTV.text.toString())

        var resultIntent = Intent()
        resultIntent.putExtra("note", note)
        setResult(Activity.RESULT_OK, resultIntent)
        Log.d("Note: ", note.toString())


        val cv = ContentValues();
        cv.put("n_header", note.noteHeader)
        cv.put("n_text", note.noteText)
        cv.put("n_image", note.imageURI.toString())
        DBHelper(baseContext).writableDatabase.insert("note", null, cv)

        finish()
    }

    // Вызывается после вызова метода startActivityForResult(Intent.createChooser(createIntent(), "Select a file"), 1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Преобразует результат активити выбора файла сначала в URI потом проставляет его в ImageView
        if (data != null) {
            Log.d("Path from Intent", data.data.path)
            imageView.setImageURI(data.data)
            Toast.makeText(applicationContext, data?.data.toString(), Toast.LENGTH_LONG).show()
            saveToInnerDir(data.data)
        }
    }

    /**
     * Метод для создания Intent для открытия окна выбора изображения
     */
    private fun createIntent(): Intent {
        return Intent()
            .setType("image/jpeg") //Устанавливаем MIME тип контента
            .setAction(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
    }

    private fun saveToInnerDir(uri: Uri?) {
        val inputStream = contentResolver.openInputStream(uri)

        val returnCursor =
            contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val name = nameIndex?.let { returnCursor?.getString(it)}.toString()

        var file = File("${this.filesDir}/$name.jpeg")

        returnCursor?.close()

        file.copyInputStreamToFile(inputStream)

        imageURI = Uri.fromFile(file)
    }
}

/**
 * Функция расширения java.io.File которая позволяет копировать InputStream непосредственно в файл
 */
fun File.copyInputStreamToFile(inputStream : InputStream) {
    this.outputStream().use { fileOut -> inputStream.copyTo(fileOut) }
}