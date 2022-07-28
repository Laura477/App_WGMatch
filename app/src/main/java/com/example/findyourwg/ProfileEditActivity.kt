package com.example.findyourwg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.findyourwg.MatchActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.example.findyourwg.databinding.ActivityProfileEditBinding

/**
 * Screen to edit own user information
 */
class ProfileEditActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUid: String
    private lateinit var usersDb: DatabaseReference
    private lateinit var userDb: DatabaseReference

    private lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) //R.layout.activity_profil_edit

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profil bearbeiten"

        // set up database references
        usersDb = FirebaseDatabase.getInstance().reference.child("users")
        mDatabase = Firebase.database.reference
        mAuth = FirebaseAuth.getInstance()
        currentUid = mAuth.currentUser?.uid.toString()
        userDb = usersDb.child(currentUid)

        //navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_matches -> {
                    val intent = Intent(this, MatchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_logout -> {
                    mAuth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_swipe -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
            false // return true;
        }

        binding.bottomNavigation.menu.findItem(R.id.menu_settings).isChecked = true

        // get user information from database to prefill the edit fields
        val mDatabase = FirebaseDatabase.getInstance().reference
        val userDb = currentUid?.let { mDatabase.child("users").child(it) }
        userDb?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val currentUser = dataSnapshot.getValue(User::class.java)

                    binding.profileName.setText(currentUser?.name.toString())
                    Picasso.get().load(currentUser?.profileImageUrl).into(binding.profileImageView)
                    binding.profilePlace.setText(currentUser?.placeOfResidence.toString())
                    binding.profileJob.setText(currentUser?.job.toString())
                    binding.profileAge.setText(currentUser?.age.toString())
                    binding.profileAbout.setText(currentUser?.aboutMe.toString())

                    when (currentUser?.gender.toString()) {
                        "weiblich" -> binding.profileGender.check(R.id.profile_f)
                        "männlich" -> binding.profileGender.check(R.id.profile_m)
                        "divers" -> binding.profileGender.check(R.id.profile_d)
                    }

                    when (currentUser?.active) {
                        true -> binding.activeToggle.isChecked = true
                        false -> binding.activeToggle.isChecked = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //on click save button, write the information from the edit fields back to the database and
        //return to swipe activity
        binding.settingsButtonSave.setOnClickListener {
            var shownAttributes: HashMap<String, Any> = HashMap<String, Any>()
            shownAttributes["name"] = binding.profileName.text.toString()
            shownAttributes["age"] = binding.profileAge.text.toString()
            shownAttributes["aboutMe"] = binding.profileAbout.text.toString()
            shownAttributes["job"] = binding.profileJob.text.toString()
            shownAttributes["placeOfResidence"] = binding.profilePlace.text.toString()
            shownAttributes["active"] = binding.activeToggle.isChecked

            val mRadiogroup = findViewById<View>(R.id.profile_gender) as RadioGroup
            val usertypeId: Int = mRadiogroup.checkedRadioButtonId
            val radioButton = findViewById<View>(usertypeId) as RadioButton
            when (radioButton.text) {
                "f" -> shownAttributes["gender"] = "weiblich"
                "m" -> shownAttributes["gender"] = "männlich"
                "d" -> shownAttributes["gender"] = "divers"
            }

            usersDb.child(currentUid).updateChildren(shownAttributes as Map<String, Any>)
            Log.d("ProfileEditActivity", "saved changes to the user in Firebase Database")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
}

