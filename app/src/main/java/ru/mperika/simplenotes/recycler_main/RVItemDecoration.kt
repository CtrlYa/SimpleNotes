package ru.mperika.simplenotes.recycler_main

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RVItemDecoration(val margin: Int, val columns: Int): RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        var position = parent.getChildLayoutPosition(view)
        outRect.top = margin
        outRect.bottom = margin
        outRect.left = margin
        outRect.right = margin
    }
}