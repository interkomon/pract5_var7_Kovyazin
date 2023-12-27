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
import com.example.ollegeapp.db.Speciality
import com.example.ollegeapp.db.Teacher

class TeachersAdapter:RecyclerView.Adapter<TeachersAdapter.TeacherViewHolder>() {

    private var teachersList: List<Teacher> = emptyList()
    private var onDeleteClickListener: ((Int) -> Unit)? = null
    private var onEditClickListener:((Int) ->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacheritem, parent, false)
        return TeacherViewHolder(itemView,onDeleteClickListener,onEditClickListener)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(teachersList[position] ,position,teachersList)
    }

    override fun getItemCount(): Int = teachersList.size

    fun setData(newTeachers: List<Teacher>) {
        teachersList = newTeachers
        notifyDataSetChanged()
    }
    fun setOnDeleteClickListener(listener: (Int) -> Unit) {
        onDeleteClickListener = listener
    }
    fun setOnEditClickListener(listener: (Int) -> Unit) {
        onEditClickListener = listener
    }
    fun getTeachersList(): List<Teacher> {
        return teachersList
    }
    class TeacherViewHolder(
        itemView: View,
        private val onDeleteClickListener: ((Int) -> Unit)? = null,
        private val onEditClickListener: ((Int) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {
        private val teacherNameTextView: TextView = itemView.findViewById(R.id.teachernametext)
        private val teacherSalaryTextView: TextView = itemView.findViewById(R.id.teachersalarytext)
        private val teacherHours: TextView = itemView.findViewById(R.id.teacherperyeartext)
        private val teacherSpeciality: TextView = itemView.findViewById(R.id.teacherspecialitutext)
        private val deleteButton: Button = itemView.findViewById(R.id.delbuttonteacher)
        private val editButton: Button = itemView.findViewById(R.id.editbuttonteacher)

        fun bind(
            teacher: Teacher,
            position: Int,
            allTeachers: List<Teacher> // Добавляем список всех преподавателей
        ) {
            teacherNameTextView.text = "Преподаватель: ${teacher.name}"

            var totalHours = 0
            for(item in allTeachers){
                if(teacher.name==item.name)
                    totalHours+=teacher.hoursPerYear;
            }
            if(totalHours>1440){
                teacherSalaryTextView.text = "Зарплата : "+((totalHours*150)+((totalHours-1440)*150)).toString()
            }
            else{
                teacherSalaryTextView.text = "Зарплата :" +(totalHours*150).toString()
            }

            teacherHours.text = "Количество часов: ${teacher.hoursPerYear}"
            teacherSpeciality.text = "Специальность: ${teacher.speciality}"


            deleteButton.setOnClickListener {
                onDeleteClickListener?.invoke(position)
            }


            editButton.setOnClickListener {
                onEditClickListener?.invoke(position)
            }
        }
    }
}