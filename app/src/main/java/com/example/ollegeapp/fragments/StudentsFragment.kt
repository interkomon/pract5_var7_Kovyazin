package com.example.ollegeapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ollegeapp.AddSpecialities
import com.example.ollegeapp.AddStudent
import com.example.ollegeapp.R
import com.example.ollegeapp.SignInActivity
import com.example.ollegeapp.adapters.StudentsAdapter
import com.example.ollegeapp.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StudentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
    class StudentsFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var addStudents: Button
    private lateinit var databaseDao: DatabaseDao
    private lateinit var adapter: StudentsAdapter
    private lateinit var buttonexit:Button

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_students, container, false)
        recycleView = view.findViewById(R.id.recycleviewstudents)
        addStudents = view.findViewById(R.id.addstudent)
        databaseDao = CollegeDatabase.getDatabase(requireContext()).databaseDao()
        adapter = StudentsAdapter()
        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter = adapter
        buttonexit = view.findViewById(R.id.buttonexit)

        lifecycleScope.launch(Dispatchers.IO) {
            val studentsList = databaseDao.getStudentsWithSpeciality()
            withContext(Dispatchers.Main) {
                adapter.setData(studentsList)
            }
        }
        adapter.setOnDeleteClickListener { position ->
            onDeleteStudent(position)
        }

        adapter.setOnEditClickListener { position ->
            val student = adapter.getStudentsList()[position]
            showEditDialog(student)

        }

        buttonexit.setOnClickListener {
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
        }


        addStudents.setOnClickListener {
            val intent = Intent(requireContext(), AddStudent::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun onDeleteStudent(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val student = adapter.getStudentsList().getOrNull(position)?.student
            student?.let {
                databaseDao.deleteStudent(it)
                val updatedStudents = databaseDao.getStudentsWithSpeciality()
                withContext(Dispatchers.Main) {
                    adapter.setData(updatedStudents)
                }
            }
        }
    }


    private fun showEditDialog(student: StudentWithSpeciality) {
        val builder = AlertDialog.Builder(requireContext())
        val specialityNames = databaseDao.getAllSpecialityNames()
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        var name = student.student.name
        var photo = student.student.photo
        var birthday = student.student.birthday
        var curs = student.student.course
        var budjet = student.student.isBudget
        var speciality = student.student.speciality

        builder.setTitle("Редактор данных")
        var ed1 = EditText(requireContext())
        var ed2 = EditText(requireContext())
        var ed3 = EditText(requireContext())
        var ed4 = EditText(requireContext())
        var checkbox = CheckBox(requireContext())
        var spinner = Spinner(requireContext())

        ed3.inputType = InputType.TYPE_DATETIME_VARIATION_DATE
        checkbox.text = "Бюджет"
        ed1.hint = "Введите ФИО студента"
        val specialityAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, specialityNames
        )
        specialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = specialityAdapter
        ed2.hint = "Введите ссылка на фото"
        ed3.hint = "Введите дату рождения"
        ed4.hint = "Введите курс"
        ed1.setText("${name}")
        ed2.setText("${photo}")
        ed3.setText("${birthday}")
        ed4.setText("${curs}")
        layout.addView(ed1)
        layout.addView(ed2)
        layout.addView(ed3)
        layout.addView(ed4)
        layout.addView(checkbox)
        layout.addView(spinner)
        builder.setView(layout)
        builder.setPositiveButton("Сохранить") { _, _ ->
            val newinf1 = ed1.text.toString()
            val newinf2 = ed2.text.toString()
            val newinf3 = ed3.text.toString()
            val newinf4 = ed4.text.toString().toInt()
            val budjetinfo = checkbox.isChecked
            val spinerinfo = spinner.selectedItem

            if (newinf1.isNotEmpty() && newinf2.isNotEmpty() && newinf3.isNotEmpty()) {
                if (isValidURL(newinf2)) {
                    val updatedStudent = student.student.copy(
                        name = newinf1,
                        photo = newinf2,
                        birthday = newinf3,
                        course = newinf4,
                        speciality = spinerinfo.toString(),
                        isBudget = budjetinfo
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        val existingStudent = databaseDao.getStudentsWithSpecialityByName(newinf1)
                        if (existingStudent.isEmpty() || existingStudent.first().student.studentId == student.student.studentId) {
                            databaseDao.updateStudent(updatedStudent)
                            val students = databaseDao.getStudentsWithSpeciality()
                            withContext(Dispatchers.Main) {
                                adapter.setData(students)
                            }
                        }
                    }
                } else {
                    // В случае неправильной ссылки, используем заглушку для фото из интернета
                    val placeholderURL = "https://cdn-icons-png.flaticon.com/512/4054/4054617.png"
                    val updatedStudent = student.student.copy(
                        name = newinf1,
                        photo = placeholderURL,
                        birthday = newinf3,
                        course = newinf4,
                        speciality = spinerinfo.toString(),
                        isBudget = budjetinfo
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        val existingStudent = databaseDao.getStudentsWithSpecialityByName(newinf1)
                        if (existingStudent.isEmpty() || existingStudent.first().student.studentId == student.student.studentId) {
                            databaseDao.updateStudent(updatedStudent)
                            val students = databaseDao.getStudentsWithSpeciality()
                            withContext(Dispatchers.Main) {
                                adapter.setData(students)
                            }
                        }
                    }

                    Toast.makeText(
                        requireContext(),
                        "Неправильная ссылка на фото. Использована заглушка",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                Toast.makeText(
                    requireContext(),
                    "Пожалуйста, заполните все поля",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()

    }

    fun isValidURL(url: String): Boolean {
        return try {
            URL(url).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StudentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}