package ru.mperika.simplenotes.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.mperika.simplenotes.R

class SettingsDialog() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity);

        return builder
            .setTitle("Настройки")
            .setView(R.layout.activity_settings)
            .setIcon(R.drawable.settings)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null)
            .create()
    }
}