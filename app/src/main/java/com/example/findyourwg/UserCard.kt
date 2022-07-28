package com.example.findyourwg

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * usercard object with short user information for display on the cards in the swipe screen
 */
@Parcelize
class UserCard(val place: String, val username: String, val profileImageUrl: String, val uid: String):
    Parcelable {
    constructor() : this("", "", "", "")
}