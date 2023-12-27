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
import com.example.ollegeapp.db.Student
import com.example.ollegeapp.db.StudentWithSpeciality

import com.squareup.picasso.Picasso

class StudentsAdapter : RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder>() {
    private var studentsList: List<StudentWithSpeciality> = emptyList()
    private var onDeleteClickListener: ((Int) -> Unit)? = null
    private var onEditClickListener:((Int)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.studentitem, parent, false)
        return StudentsViewHolder(itemView, onDeleteClickListener,onEditClickListener)
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {

        holder.bind(studentsList[position], position)

    }

    override fun getItemCount(): Int = studentsList.size

    fun setData(newStudents: List<StudentWithSpeciality>) {
        studentsList = newStudents
        notifyDataSetChanged()
    }

    fun getStudentsList(): List<StudentWithSpeciality> {
        return studentsList
    }


    fun setOnDeleteClickListener(listener: (Int) -> Unit) {
        onDeleteClickListener = listener
    }
    fun setOnEditClickListener(listener: (Int) -> Unit){
        onEditClickListener = listener
    }
    class StudentsViewHolder(
        itemView: View,
        private val onDeleteClickListener: ((Int) -> Unit)? = null,
        private val onEditClickListener:((Int)->Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.studentnametext)
        private val studentBirthdayTextView: TextView = itemView.findViewById(R.id.studentbirthdaytext)
        private val studentCoursesTextView: TextView = itemView.findViewById(R.id.studentcurstext)
        private val studentSpeciality:TextView = itemView.findViewById(R.id.studentspecialitytext)
        private val budgetTextView:TextView = itemView.findViewById(R.id.byudjettext)
        private val studentImage:ImageView = itemView.findViewById(R.id.studentimage)
        private val deleteStudentButton: Button = itemView.findViewById(R.id.delbuttonstudent)
        private val editSpecialityButton:Button = itemView.findViewById(R.id.editbuttonstudent)





        fun bind(studentWithSpeciality: StudentWithSpeciality, position: Int) {
            studentNameTextView.text = "Студент: ${studentWithSpeciality.student.name}"
            studentBirthdayTextView.text = "Дата рождения:${studentWithSpeciality.student.birthday}"
            studentCoursesTextView.text = "Курс: ${studentWithSpeciality.student.course}"
            studentSpeciality.text = "Специальность: ${studentWithSpeciality.studentSpeciality}"
            if(studentWithSpeciality.student.isBudget){
                budgetTextView.text = "Бюджет"
            }
            else{
                budgetTextView.text = "Не бюджет"
            }
            Picasso.get()
                .load(studentWithSpeciality.student.photo)
                .into(studentImage)


            deleteStudentButton.setOnClickListener {
                onDeleteClickListener?.invoke(position)
            }
            editSpecialityButton.setOnClickListener{
                onEditClickListener?.invoke(position)
            }
        }
    }
}
