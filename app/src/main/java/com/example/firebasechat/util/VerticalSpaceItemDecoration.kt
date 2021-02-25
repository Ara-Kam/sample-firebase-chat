package com.example.firebasechat.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
    context: Context,
    topMarginDpi: Float,
    firstItemTopMarginDpi: Float,
    lastItemBottomMargin: Float
) : RecyclerView.ItemDecoration() {

    private val mTopMargin: Int = pixels(context, topMarginDpi)
    private val mFirstItemTopMargin: Int = pixels(context, firstItemTopMarginDpi)
    private var mLastItemBottomMargin = 0
    private var mFooterMargin: Int? = null
    private var mBackgroundPaint: Paint? = null

    init {
        this.mLastItemBottomMargin = pixels(context, lastItemBottomMargin)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = mTopMargin
        } else {
            outRect.top = mFirstItemTopMargin
        }

        // Last child
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            if (mLastItemBottomMargin > 0) {
                outRect.bottom = mLastItemBottomMargin
            }

            if (mFooterMargin != null) {
                outRect.top = mFooterMargin!!
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            // Draw the offset background if we have any
            if (mBackgroundPaint != null) {
                // Only draw it for regular children, not the first one.
                if (parent.getChildAdapterPosition(child) > 0) {
                    val offset = if (mFooterMargin != null && i == childCount - 1)
                        mFooterMargin!!
                    else
                        mTopMargin

                    c.drawRect(
                        child.left.toFloat(),
                        (child.top - offset).toFloat(),
                        child.right.toFloat(),
                        child.top.toFloat(),
                        mBackgroundPaint!!
                    )
                }
            }
        }

    }
}