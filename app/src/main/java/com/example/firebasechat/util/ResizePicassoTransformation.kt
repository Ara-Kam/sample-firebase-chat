package com.example.firebasechat.util

import android.graphics.Bitmap
import com.squareup.picasso.Transformation
import kotlin.math.roundToLong

class ResizePicassoTransformation(private val mTargetCubeSize: Int) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val sourceW = source.width
        val sourceH = source.height

        val targetW: Int
        val targetH: Int

        if (sourceW > sourceH) {
            val aspectIndex = sourceW.toDouble() / sourceH.toDouble()
            targetW = mTargetCubeSize
            targetH = (targetW / aspectIndex).roundToLong().toInt()
        } else {
            val aspectIndex = sourceH.toDouble() / sourceW.toDouble()
            targetH = mTargetCubeSize
            targetW = (targetH / aspectIndex).roundToLong().toInt()
        }

        val result = Bitmap.createScaledBitmap(source, targetW, targetH, true)
        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle()
        }
        return result
    }

    override fun key(): String {
        return "ResizeByCubicDimension_$mTargetCubeSize"
    }
}