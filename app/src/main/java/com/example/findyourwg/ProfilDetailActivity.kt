package com.example.findyourwg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.findyourwg.databinding.ActivityProfilDetailsBinding
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

/**
 * detail screen where user can see all info about other user after having clicked on the card
 */
class ProfilDetailActivity : AppCompatActivity() {

    var detailedUser: User? = null

    private lateinit var binding: ActivityProfilDetailsBinding

    private lateinit var mDatabase: DatabaseReference
    private lateinit var userDb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) //R.layout.activity_profil_details

        // uid of the user whose details are to be shown
        val detailUid: String = intent.getStringExtra("detail_uid")!!

        // calling the action bar + showing the back button in action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profildetails"

        // get data from db
        mDatabase = FirebaseDatabase.getInstance().reference
        userDb = mDatabase.child("users").child(detailUid)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    detailedUser = dataSnapshot.getValue(User::class.java)

                    binding.profileTitle.text = detailedUser?.name.toString()
                    Picasso.get().load(detailedUser?.profileImageUrl).into(binding.profileImage)
                    binding.Ort.text = detailedUser?.placeOfResidence.toString()
                    binding.taetigkeit.text = detailedUser?.job.toString()
                    binding.gender.text =
                        detailedUser?.gender.toString() // TODO radio button vs text
                    binding.age.text = detailedUser?.age.toString()
                    binding.about.text = detailedUser?.aboutMe.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

}

