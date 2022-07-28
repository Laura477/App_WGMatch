package com.example.findyourwg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by manel on 10/31/2017. - copied straight from simcoder git!
 */
class MatchesViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var mMatchId: TextView
    var mMatchName: TextView
    var mMatchImage: ImageView

    init {
        itemView.setOnClickListener(this)
        mMatchId = itemView.findViewById<View>(R.id.Matchid) as TextView
        mMatchName = itemView.findViewById<View>(R.id.MatchName) as TextView
        mMatchImage = itemView.findViewById<View>(R.id.MatchImage) as ImageView
    }

    override fun onClick(view: View) {
        //Toast.makeText(this, "Chat function not yet implemented", Toast.LENGTH_SHORT).show()
        //val intent = Intent(view.context, ChatActivity::class.java)
        //intent.putExtra("matchId", mMatchId.text)

        //val b = Bundle()
        //b.putString("matchId", mMatchId.text.toString())
        //intent.putExtras(b)
        //intent.putExtra("matchId", mMatchId.text.toString())
        //Log.i("TEST Match intent extra", mMatchId.text.toString())

        //view.context.startActivity(intent)
    }

/*    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }*/
}
