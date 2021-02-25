package com.example.firebasechat.data.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @param email the user's email. Immutable field in this project version.
 * @param avatar the Url resource. Will be input through the input-field inside a dialog. Will be shown inside dialog if it isn't empty.
 * @param uid the user's ID, unique to the Firebase project.
 *
 */

data class User(
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("avatar") var avatar: String? = null,
    @SerializedName("uid") var uid: String? = null,
) : Serializable{
    enum class Field {
        NAME,
        EMAIL,
        PASSWORD,
        AVATAR,
        AVATAR_GALLERY
    }
}