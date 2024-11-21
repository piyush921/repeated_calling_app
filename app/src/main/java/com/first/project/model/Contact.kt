package com.first.project.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String?,
    val name: String?,
    val phone: String?
): Parcelable
