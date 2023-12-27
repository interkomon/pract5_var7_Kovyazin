package com.example.ollegeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ollegeapp.R
import com.example.ollegeapp.db.Speciality


class SpecialitiesAdapter : RecyclerView.Adapter<SpecialitiesAdapter.SpecialitiesViewHolder>() {
    private var specialitiesList:List<Speciality> = emptyList()
    private var onDeleteClickListener: ((Int) -> Unit)? = null
    private var onEditClickListener:((Int)->Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialitiesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.speciatiesitem,parent,false)
        return SpecialitiesViewHolder(itemView,onDeleteClickListener,onEditClickListener)
    }
    override fun onBindViewHolder(holder: SpecialitiesViewHolder, position: Int) {
        holder.bind(specialitiesList[position], position)
    }


    override fun getItemCount(): Int =specialitiesList.size
    fun setData(newSpecialities: List<Speciality>) {
        specialitiesList = newSpecialities
        notifyDataSetChanged()
    }
    fun setOnDeleteClickListener(listener: (Int) -> Unit) {
        onDeleteClickListener = listener
    }
    fun setOnEditClickListener(listener: (Int) -> Unit){
        onEditClickListener = listener
    }
    fun getSpecialitiesList():List<Speciality>{
        return specialitiesList
    }
    class SpecialitiesViewHolder(
        itemView: View,
        private val onDeleteClickListener:((Int)->Unit)?=null,
            private val onEditClickListener:((Int)->Unit)?=null
    ):RecyclerView.ViewHolder(itemView) {
        private val specialitytext:TextView = itemView.findViewById(R.id.specialitytext)
        private val deleteButton: Button = itemView.findViewById(R.id.delbuttonspeciality)
        private val editButton:Button = itemView.findViewById(R.id.editbuttonspiciality)
        fun bind(speciality: Speciality,position:Int){
            specialitytext.text = "Специальность: ${speciality.speciality}"


            deleteButton.setOnClickListener{
                onDeleteClickListener?.invoke(position)
            }
            editButton.setOnClickListener{
                onEditClickListener?.invoke(position)
            }
        }
    }

}