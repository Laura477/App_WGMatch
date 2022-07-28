package com.example.findyourwg

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Connection

/**
 * zum Speichern und Abfragen der Userdaten in der Datenbank
 */
@Parcelize
class User(
    val uid: String, //
    val username: String, //
    val profileImageUrl: String,//
    val email: String, //
    val usertype: String,//
    val age: String,//
    val aboutApartment: String,//
    val aboutMe: String,//
    val active: Boolean,//
    val gender: String,//
    val image: String,//
    val job: String,//
    val name: String, //
    val numberOfRoomMates: String,
    val placeOfResidence: String,
    val squareMeter: String,
    val accordingToWishes: String

) :
    Parcelable {
    constructor() : this(
        "", "", "", "", "", "", "", "", true, "",
        "", "", "", "", "", "", ""
    )

    /**
     * zu Ausgeben der Userdaten als einen String
     */
    override fun toString(): String {
        return "uid=$uid, username=$username, profileImageUrl=$profileImageUrl, emali=$email, usertype=$usertype, age=$age, aboutApartment=$aboutApartment, " +
                "aboutMe=$aboutMe, active=$active, gender=$gender, image=$image, job=$job, numberOfRoomMates=$numberOfRoomMates, placeOfResidence=$placeOfResidence" +
                "squareMeter=$squareMeter, accordingToWishes=$accordingToWishes"

    }
}