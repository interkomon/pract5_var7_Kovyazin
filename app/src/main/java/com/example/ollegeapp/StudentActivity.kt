package com.example.ollegeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ollegeapp.adapters.StudentsInfoAdapter
import com.example.ollegeapp.adapters.TeachersInfoAdapter
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentActivity : AppCompatActivity() {
    private lateinit var edittext: EditText
    private lateinit var studentInfo: RecyclerView
    private lateinit var databaseDao: DatabaseDao
    private lateinit var adapter: TeachersInfoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        edittext = findViewById(R.id.editextpoiskprepodovateley)
        studentInfo = findViewById(R.id.studentInfo)


        databaseDao = CollegeDatabase.getDatabase(this).databaseDao()
        adapter = TeachersInfoAdapter()
        studentInfo.layoutManager = LinearLayoutManager(this)
        studentInfo.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val studentsList = databaseDao.getTeachersWithSpeciality()
            withContext(Dispatchers.Main) {
                adapter.setData(studentsList)
            }
        }

    }


    fun searchbutton(view: View) {
        val fullName = edittext.text.toString().trim()

        if (!fullName.isNullOrEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val studentsList = databaseDao.getTeachersWithSpecialityByName(fullName.toString())

                withContext(Dispatchers.Main) {
                    adapter.setData(studentsList)
                }
            }
        } else {
            Toast.makeText(this, "Такого преподавателя нет", Toast.LENGTH_SHORT).show()
        }

        if (!fullName.isNotBlank()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val studentsList = databaseDao.getTeachersWithSpeciality()
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