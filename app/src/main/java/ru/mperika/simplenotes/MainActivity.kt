package ru.mperika.simplenotes

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import ru.mperika.simplenotes.data_source.Note
import ru.mperika.simplenotes.data_source.createNotesListFromCursor
import ru.mperika.simplenotes.dialogs.SettingsDialog
import ru.mperika.simplenotes.edit_activity.deleteNote
import ru.mperika.simplenotes.recycler_main.RVClickListener
import ru.mperika.simplenotes.recycler_main.RVDataAdapter
import ru.mperika.simplenotes.recycler_main.RVItemDecoration

class MainActivity : AppCompatActivity() {

    private var notes: ArrayList<Note> = arrayListOf()
    private lateinit var adapter: RVDataAdapter
    private val EDIT_ACTIVITY = "ru.mperika.START_EDIT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        downloadDBData() // Если во внутренней БД есть данные, то загружаем и передаем в адаптер готовую модель
        adapter = RVDataAdapter(
            this.baseContext,
            notes
        )

        var lines =
            if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                4
            } else {
                2
            }

        val linear = GridLayoutManager(this, lines, GridLayoutManager.VERTICAL, false)
        recyclerV.setHasFixedSize(true)
        recyclerV.layoutManager = linear
        val decor = RVItemDecoration(15, 2)
        recyclerV.addItemDecoration(decor)
        recyclerV.adapter = adapter

        recyclerV.addOnItemTouchListener(
            RVClickListener(
                this,
                recyclerV,
                object : RVClickListener.OnItemClickListener {

                    //Короткое нажатие
                    override fun onItemClick(view: View, position: Int) {
                        runEditActivity(position)
                    }

                    //Длинное нажатие
                    override fun onItemLongClick(view: View?, position: Int) {
                        val wrapper = ContextThemeWrapper(baseContext, R.style.PopupMenu)
                        val popup = PopupMenu(wrapper, view)
                        popup.inflate(R.menu.note_card_options)
                        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.action_delete -> {
                                    if (deleteNote(baseContext, position, notes)) {
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            true
                        })
                        popup.show()
                    }
                })
        )

        fab.setOnClickListener {
            val intent = Intent(EDIT_ACTIVITY)
            intent.putExtra("requestCode", 0)
            startActivityForResult(intent, 0)
        }
    }

    // Вызывается после вызова метода startActivityForResult(Intent.createChooser(createIntent(), "Select a file"), 1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Преобразует результат активити выбора файла сначала в URI потом проставляет его в ImageView
        if ((requestCode == 0) and (resultCode == Activity.RESULT_OK) and (data != null)) {
            val note = data?.getParcelableExtra("note") as Note
            notes.add(note)
            Log.d("Notes size: ", "${notes.size}")
        } else if ((requestCode == 1) and (resultCode == Activity.RESULT_OK) and (data != null)) {
            val note = data?.getParcelableExtra("note") as Note
            notes[data?.extras["position"] as Int] = note
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
//                var intent = Intent(SETTINGS_ACTIVITY)
//                startActivity(intent)
                val dialog = SettingsDialog()
                dialog.show(supportFragmentManager, "dialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Метод для запуска активити в режиме редактирования, по короткому нажатию на заметку в списке
     */
    fun runEditActivity(position: Int) {
        val intent = Intent(EDIT_ACTIVITY)
        intent.putExtra("requestCode", 1)
        intent.putExtra("note", notes[position])
        intent.putExtra("position", position)
        startActivityForResult(intent, 1)
    }

    /**
     * Метод для загрузки данных из базы данных. Если в БД есть записи.
     */
    private fun downloadDBData() {
        val db = DBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $NOTES_TABLE_NAME", null)
        if (cursor.count > 0) {
            notes = createNotesListFromCursor(cursor) //Вызываем фабрику значений из Note.kt
        }
        cursor.close()
    }
}

