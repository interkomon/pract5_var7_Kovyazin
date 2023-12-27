package com.example.ollegeapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var signin: Button
    private lateinit var spinner: Spinner
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        signin = findViewById(R.id.buttonsignin)
        spinner = findViewById(R.id.spinner)

        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        // Сохранение логина и пароля для админа
        editor.putString("admin_login", "admin")
        editor.putString("admin_password", "admin")

        // Сохранение логина и пароля для преподавателя
        editor.putString("teacher_login", "teacher")
        editor.putString("teacher_password", "teacher")

        // Сохранение логина и пароля для студента
        editor.putString("student_login", "student")
        editor.putString("student_password", "student")

        editor.apply()





        signin.setOnClickListener {
            val enteredLogin = login.text.toString()
            val enteredPassword = password.text.toString()
            //var name = ""

            val userType = spinner.selectedItem.toString()
//            when(userType)
//            {
//                "Приемная комиссия" -> name = "admin"
//                "Преподаватель" -> name = "teacher"
//                "Студент" -> name = "student"
//            }
            val savedLogin = sharedPref.getString("${enteredLogin}_login", "")?.trim()
            val savedPassword = sharedPref.getString("${enteredPassword}_password", "")?.trim()


            if(enteredLogin.isNotBlank() || enteredPassword.isNotBlank()) {
                if (enteredLogin.trim() == savedLogin && enteredPassword.trim() == savedPassword) {
                    val lastUserType = sharedPref.getString("last_user_type", "")

                    when (enteredLogin) {
                        "admin" -> {
                            // Для администратора разрешаем доступ к любому типу активности
                            val intent = when (userType) {
                                "Приемная комиссия" -> Intent(this, AdmissionCommittee::class.java)
                                "Преподаватель" -> Intent(this, TeacherActivity::class.java)
                                "Студент" -> Intent(this, StudentActivity::class.java)
                                else -> null
                            }
                            if (intent != null) {
                                startActivity(intent)
                                editor.putString("last_user_type", userType)
                                editor.apply()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Ошибка в выборе режима",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                        "teacher" -> {
                            // Для преподавателя разрешаем доступ только к экрану преподавателя
                            if (userType == "Преподаватель") {
                                startActivity(Intent(this, TeacherActivity::class.java))
                                editor.putString("last_user_type", userType)
                                editor.apply()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Ошибка в выборе режима",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                        "student" -> {
                            // Для студента разрешаем доступ только к экрану студента
                            if (userType == "Студент") {
                                startActivity(Intent(this, StudentActivity::class.java))
                                editor.putString("last_user_type", userType)
                                editor.apply()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Ошибка в выборе режима",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                        else -> {
                            // Предыдущий тип пользователя не найден
                            Toast.makeText(
                                this,
                                "Ошибка в определении предыдущего типа пользователя",
                                Toast.LENGTH_SHORT
                            ).show()


                        }
                    }
                } else {
                    Toast.makeText(this, "Введен неправильный логин или пароль", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                Toast.makeText(this, "Введите все данные", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }
}
