package com.example.ollegeapp

import android.R
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.ollegeapp.databinding.ActivityAddStudentBinding
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import com.example.ollegeapp.db.Student
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddStudent : AppCompatActivity() {
    private lateinit var studentnameEditText:EditText
    private lateinit var imageEditText:EditText
    private lateinit var birthdayEditText:EditText
    private lateinit var calendarButton:Button
    private lateinit var groupSpinner:Spinner
    private lateinit var cursEditText:EditText
    private lateinit var byudjetcheckbox:CheckBox
    private lateinit var addStudentButton:Button
    private lateinit var databaseDao:DatabaseDao
    private lateinit var binding: ActivityAddStudentBinding
    private lateinit var error:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseDao = CollegeDatabase.getDatabase(this).databaseDao()

        val groupAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            emptyArray<String>()
        ).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
        binding.groupSpinner.adapter = groupAdapter
        loadGroups()


        binding.calendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { view, selectedYear, selectedMonth, selectedDay ->
                    // Проверяем, что выбранная дата входит в диапазон от 1990 до 2007 года
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, selectedYear)
                    selectedDate.set(Calendar.MONTH, selectedMonth)
                    selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay)

                    val minDate = Calendar.getInstance()
                    minDate.set(1990, 0, 1)

                    val maxDate = Calendar.getInstance()
                    maxDate.set(2007, 11, 31)

                    if (selectedDate.before(minDate) || selectedDate.after(maxDate)) {
                        Toast.makeText(
                            this,
                            "Пожалуйста, выберите дату между 1990 и 2007 годом",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.birthdayEditText.setText(
                            SimpleDateFormat(
                                "dd.MM.yyyy",
                                Locale.getDefault()
                            ).format(selectedDate.time)
                        )
                    }
                },
                year, month, day
            )
            datePicker.show()
        }


        suspend fun CheckImageAvailability(imageUrl: String): Boolean {
            val client = HttpClient()
            return try {
                val response: io.ktor.client.statement.HttpResponse = client.get(imageUrl)
                true
            } catch (e: Exception) {
                false
            } finally {
                client.close()
            }
        }

        binding.addStudentButton.setOnClickListener {
            try {


            val studentName = binding.studentnameEdiText.text.toString()
            val groupName = binding.groupSpinner.selectedItem.toString()
            val coursText = binding.cursEditText.text.toString()
            val birthday =  binding.birthdayEditText.text.toString()
            var image = binding.imageEditText.text.toString()
            val isBudget = binding.byudjetcheckbox.isChecked
            if (studentName.isNotBlank() && groupName.isNotBlank() && coursText.isNotBlank() && image.isNotBlank() && birthday != "Нажмите на кнопку справа, чтобы выбрать дату:")
             {
                val coursText = coursText.toInt()
                if (coursText > 1 && coursText < 5) {

                    val imageAvailable = runBlocking { CheckImageAvailability(image) }
                    if (!imageAvailable) {
                        image = "https://cdn-icons-png.flaticon.com/512/4054/4054617.png"
                    }
                    lifecycleScope.launch(Dispatchers.IO) {

                        val student = Student(name = studentName, birthday = birthday, speciality = groupName, course = coursText.toInt(), isBudget = isBudget, photo = image)
                        if(isStudentUnique(student)) {
                            databaseDao.insertStudent(student)
                            finish()
                        }
                        else{
                            runOnUiThread{
                                Toast.makeText(this@AddStudent,error,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    Toast.makeText(this@AddStudent,"Вы успешно добавили студента",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,AdmissionCommittee::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(
                        this,
                        "Курс должен быть больше нуля и не больше 4",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Введите все данные", Toast.LENGTH_SHORT).show()
            }

        }catch (ex:Exception)
            {
                Toast.makeText(this,"Проверьте правильность вводимых сиволов",Toast.LENGTH_SHORT).show()
            }
        }
    }
        private fun loadGroups() {
        lifecycleScope.launch(Dispatchers.IO) {
            val groupsFromDb = databaseDao.getAllSpecialities()
            val groupNames = groupsFromDb.map { it.speciality }

            val groupNamesList = ArrayList<String>(groupNames)

            withContext(Dispatchers.Main) {
                val newAdapter = ArrayAdapter(
                    this@AddStudent,
                    R.layout.simple_spinner_item,
                    groupNamesList
                ).apply {
                    setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                }
                binding.groupSpinner.adapter = newAdapter
            }

        }
    }

    private suspend fun isStudentUnique(student: Student): Boolean {
        val studentsList = databaseDao.searchStudentsByName(student.name)
        var hasBudgetPlace = false

        for (existingStudent in studentsList) {
            if(existingStudent.speciality==student.speciality) {
                error = "Студент уже учится на этой специальности"
                return false
            }
            if(existingStudent.isBudget)
                hasBudgetPlace=true;
        }
        if(student.isBudget && hasBudgetPlace){
          error = "У студента уже бюджетное место"
            return false
        }

        // Нет совпадений, студент уникален
        return true
    }

}


