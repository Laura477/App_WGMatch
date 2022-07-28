package com.example.findyourwg

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

/**
 * copied  straight from simcoder git!
 */
class MatchActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mMatchesAdapter: RecyclerView.Adapter<*>? = null
    private var mMatchesLayoutManager: RecyclerView.LayoutManager? = null
    private var currentUserID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Matches"

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        mRecyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(this@MatchActivity)
        mRecyclerView!!.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(dataSetMatches, this@MatchActivity)
        mRecyclerView!!.adapter = mMatchesAdapter
        userMatchId
    }

    private val userMatchId: Unit
        private get() {
            val matchDb = FirebaseDatabase.getInstance().reference.child("users").child(
                currentUserID.toString()
            ).child("connections").child("matches")
            matchDb.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (match in dataSnapshot.children) {
                            FetchMatchInformation(match.key.toString())
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun FetchMatchInformation(key: String) {
        val userDb = FirebaseDatabase.getInstance().reference.child("users").child(key)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("profileImageUrl").value != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                    }
                    val obj = MatchesObject(userId.toString(), name, profileImageUrl)
                    resultsMatches.add(obj)
                    mMatchesAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val resultsMatches = ArrayList<MatchesObject>()
    private val dataSetMatches: List<MatchesObject>
        private get() = resultsMatches
}

class MatchesObject(var userId: String, var name: String, var profileImageUrl: String) {

    fun setUserID(userID: String?) {
        userId = userId
    }

}


