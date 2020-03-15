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

    private var imageURI: Uri? = null
    private var note: Note? = null
    private var requestCode: Int = 0 // 0 - новая заметка, 1 - редактирование существующей
    private var position: Int? = -1
    private lateinit var oldURI: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        requestCode = intent.extras["requestCode"] as Int
        if (requestCode == 1) {
            note = intent.getParcelableExtra("note")
            imageURI = note!!.imageURI
            imageView.setImageURI(note!!.imageURI)
            headerTV.setText(note!!.noteHeader)
            textTV.setText(note!!.noteText)
            position = intent.extras["position"] as Int
        }

        button.setOnClickListener {
            save()
        }

        floatingActionButton.setOnClickListener {
            oldURI = imageURI!!
            startActivityForResult(Intent.createChooser(createIntent(), "Select a file"), 1)
        }
    }

    private fun save() {
        note = if (requestCode == 0) {
            Note(imageURI, headerTV.text.toString(), textTV.text.toString())
        } else {
            Note(note!!.id, imageURI, headerTV.text.toString(), textTV.text.toString())
        }

        var resultIntent = Intent()
        resultIntent.putExtra("note", note)
        resultIntent.putExtra("requestCode", requestCode)
        resultIntent.putExtra("position", position)
        setResult(Activity.RESULT_OK, resultIntent)
        Log.d("Note: ", note.toString())


        val cv = ContentValues();
        cv.put("n_header", note!!.noteHeader)
        cv.put("n_text", note!!.noteText)
        cv.put("n_image", note!!.imageURI.toString())
        if (requestCode == 0) {
            DBHelper(baseContext).writableDatabase.insert("note", null, cv)
        } else {
            DBHelper(baseContext).writableDatabase.update("note", cv, "n_id = ${note!!.id}", null)
        }

        if (!(note!!.imageURI?.equals(oldURI))!!) {
            if (File(oldURI.path).delete()) {
                Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
                //TODO: добавить проверку на то ссылаются ли другие заметки на это изображение
            }
        }

        finish()
    }

    // Вызывается после вызова метода startActivityForResult(Intent.createChooser(createIntent(), "Select a file"), 1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Преобразует результат активити выбора файла сначала в URI потом проставляет его в ImageView
        if (data != null) {

            Log.d("Path from Intent", data.data.path)
            imageView.setImageURI(data.data)
//            Toast.makeText(applicationContext, data?.data.toString(), Toast.LENGTH_LONG).show()
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