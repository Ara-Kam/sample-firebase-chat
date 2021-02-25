package com.example.firebasechat.util

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.firebasechat.R
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    Picasso.get()
        .load(if (imgUrl != null && imgUrl.isNotBlank()) imgUrl else getUriForResource(R.color.design_default_color_error))
        .placeholder(
            ContextCompat.getDrawable(
                imgView.context,
                R.mipmap.ic_launcher
            )!!
        )
        .transform(
            ResizePicassoTransformation(
                pixels(imgView.context, IMAGE_FRAME_SIZE.toFloat())
            )
        )
        .into(imgView)
}