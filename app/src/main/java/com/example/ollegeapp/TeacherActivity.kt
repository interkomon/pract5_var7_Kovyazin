package com.example.ollegeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ollegeapp.adapters.StudentsInfoAdapter
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeacherActivity : AppCompatActivity() {
    private lateinit var edittext: EditText
    private lateinit var teacherInfo: RecyclerView
    private lateinit var databaseDao: DatabaseDao
    private lateinit var adapter: StudentsInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        edittext = findViewById(R.id.editTextfiopoiskstudent)
        teacherInfo = findViewById(R.id.teacherInfo)


        databaseDao = CollegeDatabase.getDatabase(this).databaseDao()
        adapter = StudentsInfoAdapter()
        teacherInfo.layoutManager = LinearLayoutManager(this)
        teacherInfo.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val studentsList = databaseDao.getStudentsWithSpeciality()
            withContext(Dispatchers.Main) {
                adapter.setData(studentsList)
            }
        }

    }

    fun searchstudent(view: View) {
        val fullName = edittext.text.toString().trim()

        if (!fullName.isNullOrEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val studentsList = databaseDao.getStudentsWithSpecialityByName(fullName.toString())

                withContext(Dispatchers.Main) {
                    adapter.setData(studentsList)
                }
            }
        } else {
            Toast.makeText(this@TeacherActivity, "Такого студента нет", Toast.LENGTH_SHORT).show()
        }

        if (!fullName.isNotBlank()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val studentsList = databaseDao.getStudentsWithSpeciality()
                withContext(Dispatchers.Main) {
                    adapter.setData(studentsList)
                }
            }



        }
    }

    fun exit(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}
