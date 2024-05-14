package com.arc.cinexpert.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.R
import com.bumptech.glide.Glide

class ActorsAdapter(private var actors: List<Actor>) : RecyclerView.Adapter<ActorsAdapter.ActorViewHolder>() {

    class ActorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.actorName)
        val profileImage: ImageView = view.findViewById(R.id.actorImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.actor_item, parent, false)
        return ActorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = actors[position]
        holder.name.text = actor.name
        Glide.with(holder.profileImage.context)
            .load("https://image.tmdb.org/t/p/w500${actor.profile_path}")
            .into(holder.profileImage)
    }

    override fun getItemCount() = actors.size

    fun updateActors(newActors: List<Actor>) {
        actors = newActors
        notifyDataSetChanged()
    }
}
