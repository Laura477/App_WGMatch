package com.example.findyourwg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.findyourwg.MatchActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.*
import com.example.findyourwg.databinding.ActivityMainBinding

/**
 * main swipe screen with card stack of user profiles of the opposite user type
 * (Suchende for WGs and WGs for zimmersuchende Leute)
 *
 * relies on this library for the cardstackview: https://github.com/yuyakaido/CardStackView
 * and for the main logic shoutout to https://github.com/SimCoderYoutube/TinderClone
 */
class MainActivity : AppCompatActivity(), CardStackListener {

    private val TAG: String = MainActivity::class.java.name

    private lateinit var binding: ActivityMainBinding

    //Firebase datarefs
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUid: String
    private lateinit var mDatabase: DatabaseReference
    private lateinit var usersDb: DatabaseReference
    private lateinit var userDb: DatabaseReference

    private lateinit var adapter: UserCardStackAdapter
    lateinit var rowItems: MutableList<UserCard>

    private var usertype: String? = null
    private var oppositeUsertype: String? = null

    // from yukakaido library
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }

    //from FilterActivity
    private var filtered: Boolean? = false
    private var filtered_place: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) //R.layout.activity_main

        // initiate Firebase data
        usersDb = FirebaseDatabase.getInstance().reference.child("users")
        mDatabase = Firebase.database.reference
        mAuth = FirebaseAuth.getInstance()
        currentUid = mAuth.currentUser?.uid.toString()
        userDb = usersDb.child(currentUid)

        // navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_matches -> {
                    val intent = Intent(this@MainActivity, MatchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_logout -> {
                    mAuth.signOut()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_settings -> {
                    val intent = Intent(this@MainActivity, ProfileEditActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
            false
        }

        binding.bottomNavigation.menu.findItem(R.id.menu_swipe).isChecked = true

        // Filter option from Filteractivity
        filtered = intent.getBooleanExtra("filtered", false)
        filtered_place = intent.getStringExtra("place").toString()

        binding.mainFilterButton.setOnClickListener {
            val intent = Intent(this@MainActivity, FilterActivity::class.java)
            intent.putExtra("filtered_place", filtered_place)
            startActivity(intent)
        }

        // main function to retrieve the usercards from the database into recycling/-cardstackview
        getOppositeUserType() //+getUserCards for adapter
        rowItems = ArrayList()
        adapter = UserCardStackAdapter(rowItems)

        setupCardStackView() //from yukakaido library
        setupButton() //from yukakaido library
    }

    /**
     * once the opposite usertype has been determined, all active not-yet-swiped opposite user profiles
     * are loaded to the adapter (if previously filtered, for the filtered place only)
     */
    private fun getUserCards() {
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.child("usertype").value != null) {
                        if (snapshot.exists()
                            && snapshot.child("active").value == true
                            && !snapshot.child("connections")
                                .child("dislikes")
                                .hasChild(currentUid)
                            && !snapshot.child("connections")
                                .child("likes")
                                .hasChild(currentUid)
                            && snapshot.child("usertype").value.toString() == oppositeUsertype
                        ) {
                            if (filtered == false || (filtered == true && snapshot.child("placeOfResidence").value.toString() == filtered_place)) {
                                val username = snapshot.child("name").value.toString()
                                val place = snapshot.child("placeOfResidence").value.toString()
                                val uid = snapshot.key.toString()
                                val profileImageUrl =
                                    snapshot.child("profileImageUrl").value.toString()
                                val card = UserCard(place, username, profileImageUrl, uid)
                                rowItems.add(card)
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()

                //if there are no swipeable users, show textview that states that
                if (adapter.itemCount == 0) {
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.skipButton.visibility = View.INVISIBLE
                    binding.likeButton.visibility = View.INVISIBLE
                } else {
                    binding.textEmpty.visibility = View.INVISIBLE
                    binding.skipButton.visibility = View.VISIBLE
                    binding.likeButton.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     *     lookup in Firebase database to retrieve current user type and opposite one and provide
     *     that info to getUserCards function
     */
    private fun getOppositeUserType() {
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("usertype").value != null) {
                        usertype = dataSnapshot.child("usertype").value.toString()
                        when (usertype) {
                            "WG" -> oppositeUsertype = "Sucher"
                            "Sucher" -> oppositeUsertype = "WG"
                        }
                        getUserCards()
                    }
                } else {
                    Log.i(TAG, "snapshot does not exist: $currentUid")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * function to retrieve the id of the user that is currently displayed on top of the card stack
     * (not pretty)
     */
    private var uid: String? = null
    override fun onCardAppeared(view: View, position: Int) {
        uid = view.findViewById<TextView>(R.id.card_user_id).text.toString()
        Log.d("CardStackView", "onCardAppeared: ($position), uid: $uid ") //${textView.text}
    }

    /**
     * Swipe mechanism to write likes and dislikes to the database and check for matches
     */
    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")

        //like
        if (direction.toString() == "Right") {
            Log.d("CardStackView", "id swiped: $uid, swiped right by $currentUid")
            usersDb.child(uid.toString()).child("connections").child("likes").child(currentUid)
                .setValue(true)
            //check for match
            isConnectionMatch(uid.toString())

        }

        //dislike
        if (direction.toString() == "Left") {
            Log.d("CardStackView", "id swiped: $uid, swiped left by $currentUid")
            usersDb.child(uid.toString()).child("connections").child("dislikes").child(currentUid)
                .setValue(true)
        }

        //if swipe leads to empty card stack, show textview that says so
        if (manager.topPosition == adapter.itemCount) {
            Log.i("TEST", manager.topPosition.toString())
            Log.i("TEST", adapter.itemCount.toString())

            binding.textEmpty.visibility = View.VISIBLE
            binding.likeButton.visibility = View.INVISIBLE
            binding.skipButton.visibility = View.INVISIBLE

        }
    }

    /**
     * function that checks for matches after swiping right, based on SimCoder github code
     */
    private fun isConnectionMatch(uid: String) {
        Log.i(TAG, "MATCH, $currentUid swiped $uid right")
        //check if other user likes the user back and implement match node with the uid of the user that likes the
        //other one and vice versa
        val currentUserConnectionsDb =
            usersDb.child(currentUid).child("connections").child("likes").child(uid)
        currentUserConnectionsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //currently subchild with chatId is not needed, but is used as a dummy to
                    // ensure that the relevant keys are set in the DB for the chat function to rely on
                    val keyRef = FirebaseDatabase.getInstance().reference.child("Chat").push()
                    val key = keyRef.key

                    usersDb.child(dataSnapshot.key!!).child("connections").child("matches")
                        .child(currentUid).child("ChatId").setValue(key)
                    usersDb.child(currentUid).child("connections").child("matches")
                        .child(dataSnapshot.key!!).child("ChatId").setValue(key)

                    //show dialog to make user aware of the match after swiping
                    doDialogAlert()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    /**
     * short dialog to inform user about match and ask if he wants to go to the chatroom for messaging
     */
    private fun doDialogAlert() {
        AlertDialog.Builder(this).setMessage("Schreibe jetzt eine Nachricht.")
            .setTitle("Neues Match!")
            .setCancelable(true)
            .setPositiveButton("Zum Chat") { _, id ->
                val intent = Intent(this@MainActivity, MatchActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Später") { _, id ->
            }
            .create()   // Dialog
            .show()
    }


    /**
     * set up cardstackview & like-/dislike buttons from sample in yukakaido library
     */

    private fun setupCardStackView() {
        initialize()
    }

    private fun setupButton() {
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }


    /**
     * following few functions need to be implemented for use of yukakaido's cardstackview
     * library, but are not used
     */

    override fun onCardRewound() {
        //unused option from yukakaido library
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        //unused option from yukakaido library
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        //unused option from yukakaido library
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        //unused mandatory option from yukakaido library
        //val textView = view.findViewById<TextView>(R.id.item_name)
        //Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

}