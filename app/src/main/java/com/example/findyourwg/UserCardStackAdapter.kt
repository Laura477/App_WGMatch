package com.example.findyourwg

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * for the recycling-/cardstackview based on yukakaido library, fill recyclerview viewholder
 * with user cards
 */
class UserCardStackAdapter(
    private var cards: List<UserCard> = emptyList()
) : RecyclerView.Adapter<UserCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        holder.name.text = card.username
        holder.place.text = card.place
        holder.id.text = card.uid

        Glide.with(holder.image)
            .load(card.profileImageUrl)
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            val intent : Intent =  Intent(v.context, ProfilDetailActivity()::class.java)
            intent.putExtra("detail_uid", card.uid)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.card_user_name)
        var place: TextView = view.findViewById(R.id.card_place)
        var image: ImageView = view.findViewById(R.id.card_profile_image)
        val id: TextView = view.findViewById(R.id.card_user_id)
    }

}