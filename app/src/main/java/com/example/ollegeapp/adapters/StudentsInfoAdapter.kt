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

class StudentsInfoAdapter: RecyclerView.Adapter<StudentsInfoAdapter.StudentsInfoViewHolder>() {
    private var studentsList: List<StudentWithSpeciality> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsInfoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.studentinfoitem, parent, false)
        return StudentsInfoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentsInfoViewHolder, position: Int) {
        holder.bind(studentsList[position])
    }

    override fun getItemCount(): Int = studentsList.size

    fun setData(newStudents: List<StudentWithSpeciality>) {
        studentsList = newStudents
        notifyDataSetChanged()
    }

    fun getStudentsList(): List<StudentWithSpeciality> {
        return studentsList
    }
    class StudentsInfoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.studentnametext)
        private val studentBirthdayTextView: TextView = itemView.findViewById(R.id.studentbirthdaytext)
        private val studentCoursesTextView: TextView = itemView.findViewById(R.id.studentcurstext)
        private val studentSpeciality:TextView = itemView.findViewById(R.id.studentspecialitytext)
        private val budgetTextView:TextView = itemView.findViewById(R.id.byudjettext)
        private val studentImage:ImageView = itemView.findViewById(R.id.studentinfoimage)




        fun bind(studentWithSpeciality: StudentWithSpeciality) {
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
            Picasso.get().load(studentWithSpeciality.student.photo).into(studentImage)


        }


    }
}