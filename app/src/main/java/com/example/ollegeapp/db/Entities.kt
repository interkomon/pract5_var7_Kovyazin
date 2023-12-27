package com.example.ollegeapp.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "speciality")
data class Speciality(
    @PrimaryKey(autoGenerate = false)
    var speciality: String
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val studentId: Long = 0,
    val name: String,
    val birthday: String,
    var speciality: String,
    val course: Int,
    val isBudget: Boolean,
    val photo: String
)
@Entity(
    tableName = "teachers",
    foreignKeys = [
        ForeignKey(
            entity = Speciality::class,
            parentColumns = ["speciality"],
            childColumns = ["speciality"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    val teacherId: Long = 0,
    val name: String,
    val salary: Double,
    val speciality: String, //связываем по этому полю с таблицей Speciality
    val hoursPerYear: Int //добавляем новое поле
)


data class StudentWithSpeciality(
    @Embedded val student: Student,
    val studentSpeciality: String
)
data class TeacherWithSpeciality(
    @Embedded val teacher: Teacher,
    val teacherSpeciality: String
)