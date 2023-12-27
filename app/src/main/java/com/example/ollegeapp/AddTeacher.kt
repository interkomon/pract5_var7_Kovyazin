package com.example.ollegeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import android.R
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.ollegeapp.databinding.ActivityAddTeacherBinding
import com.example.ollegeapp.db.Teacher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTeacher : AppCompatActivity() {
    private lateinit var teachernameEditText:EditText
    private lateinit var specialitiesSpinner:Spinner
    private lateinit var hourstechersr:EditText
    private lateinit var addTecherButton:Button
    private lateinit var databaseDao: DatabaseDao
    private lateinit var binding: ActivityAddTeacherBinding
    private lateinit var error:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseDao = CollegeDatabase.getDatabase(this).databaseDao()


        val groupAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            emptyArray<String>()
        ).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
        binding.specialitiesSpinner.adapter = groupAdapter
        loadGroups()



        binding.addTecherButton.setOnClickListener{
            try{


            if(binding.teachernameEditText.text.isNotBlank() && binding.hourstechers.text.isNotBlank())
            {
                if(binding.hourstechers.text.isNotBlank() && binding.hourstechers.text.toString().toInt() <= 2 || binding.hourstechers.text.toString().toInt() > 4000)
                {
                    Toast.makeText(this,"Введите корректные часы",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    addTeacher()
                    Toast.makeText(this,"Вы добавили преподавателя",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,AdmissionCommittee::class.java)
                    startActivity(intent)
                }
            }
            else
            {
                Toast.makeText(this,"Введите все данные",Toast.LENGTH_SHORT).show()
            }
            }catch (ex:Exception){
                Toast.makeText(this,"Такой преподаватель c такой же специальностью уже есть",Toast.LENGTH_SHORT).show()
            }
        }
    }




         fun loadGroups() {
            lifecycleScope.launch(Dispatchers.IO) {
                val groupsFromDb = databaseDao.getAllSpecialities()
                val groupNames = groupsFromDb.map { it.speciality }


                val groupNamesList = ArrayList<String>(groupNames)


                withContext(Dispatchers.Main) {
                    val newAdapter = ArrayAdapter(
                        this@AddTeacher,
                        R.layout.simple_spinner_item,
                        groupNamesList
                    ).apply {
                        setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    }
                    binding.specialitiesSpinner.adapter = newAdapter
                }
            }
        }

        private fun addTeacher() {
            try {
                if (binding.specialitiesSpinner.selectedItemPosition == -1 || binding.teachernameEditText.text.toString()
                        .isEmpty() || binding.hourstechers.text.isEmpty()
                ) {
                    Toast.makeText(
                        this@AddTeacher,
                        "Вы не заполнили все поля",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return
                }
                if (binding.hourstechers.text.toString().toInt() < 0) {
                    Toast.makeText(
                        this@AddTeacher,
                        "Количество часов должно быть положительным",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    val teacher = Teacher(
                        name = binding.teachernameEditText.text.toString(),
                        speciality = binding.specialitiesSpinner.selectedItem.toString(),
                        salary = 0.0,
                        hoursPerYear = binding.hourstechers.text.toString().toInt()
                    )
                    if (isTeacherUnique(teacher)) {
                        databaseDao.insertTeacher(teacher)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddTeacher, error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddTeacher, "Вы ввели не число", Toast.LENGTH_SHORT).show()
            }

        }

        private suspend fun isTeacherUnique(teacher: Teacher): Boolean {
            val teacherList = databaseDao.searchTeachersByName(teacher.name)
            for (item in teacherList) {
                if (item.speciality == teacher.speciality) {
                    error = "Преподаватель уже ведет на эктой специальности"
                    return false
                }
            }
            return true;
        }
    }
