package com.example.ollegeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import com.example.ollegeapp.db.Speciality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSpecialities : AppCompatActivity() {
    private lateinit var specialityEditText:EditText
    private lateinit var addSpecialitiybase: Button
    private lateinit var databaseDao: DatabaseDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_specialities)
        databaseDao = CollegeDatabase.getDatabase(this).databaseDao()
        specialityEditText = findViewById(R.id.specialityEditText)
        addSpecialitiybase = findViewById(R.id.addSpecialitybase)
        var specialityName = specialityEditText


        addSpecialitiybase.setOnClickListener{
            try {
                if (specialityName.text.isNotBlank()) {
                    var specialityName = specialityEditText.text.toString()
                    lifecycleScope.launch(Dispatchers.IO) {

                        val speciality = Speciality(specialityName)
                        databaseDao.insertSpeciality(speciality)
                        runOnUiThread {
                            Toast.makeText(
                                this@AddSpecialities,
                                "Вы добавили специальность",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@AddSpecialities, AdmissionCommittee::class.java)
                            startActivity(intent)
                        }

                    }

                }
            }catch (ex:Exception){
                Toast.makeText(this,"Ошибка в вводе",Toast.LENGTH_SHORT).show()
            }
        }
    }
}