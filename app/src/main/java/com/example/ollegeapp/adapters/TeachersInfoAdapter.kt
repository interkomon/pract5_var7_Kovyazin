package com.example.ollegeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ollegeapp.R
import com.example.ollegeapp.db.Teacher
import com.example.ollegeapp.db.TeacherWithSpeciality

class TeachersInfoAdapter: RecyclerView.Adapter<TeachersInfoAdapter.TeachersInfoViewHolder>() {

    private var teachersList: List<TeacherWithSpeciality> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeachersInfoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacherinfoitem, parent, false)
        return TeachersInfoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeachersInfoViewHolder, position: Int) {
        holder.bind(teachersList[position])
    }

    override fun getItemCount(): Int = teachersList.size

    fun setData(newTeachers: List<TeacherWithSpeciality>) {
        teachersList = newTeachers
        notifyDataSetChanged()
    }
    fun getTeachersList(): List<TeacherWithSpeciality> {
        return teachersList
    }
    class TeachersInfoViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        private val teacherNameTextView: TextView = itemView.findViewById(R.id.teachernametext)
        private val teacherSpeciality: TextView = itemView.findViewById(R.id.teacherspecialitutext)

        private val teacherHours: TextView = itemView.findViewById(R.id.teacherperyeartext)

        fun bind(
            teacherWithSpeciality: TeacherWithSpeciality,

        ) {
            teacherNameTextView.text = "Преподаватель: ${teacherWithSpeciality.teacher.name}"
            teacherHours.text = "Количество часов: ${teacherWithSpeciality.teacher.hoursPerYear}"
            teacherSpeciality.text = "Специальность: ${teacherWithSpeciality.teacher.speciality}"

        }
    }
}