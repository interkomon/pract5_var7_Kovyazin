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
import com.example.ollegeapp.AddStudent
import com.example.ollegeapp.AddTeacher
import com.example.ollegeapp.R
import com.example.ollegeapp.SignInActivity
import com.example.ollegeapp.adapters.TeachersAdapter
import com.example.ollegeapp.adapters.TeachersAdapterNew
import com.example.ollegeapp.adapters.TeachersInfoAdapter
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import com.example.ollegeapp.db.StudentWithSpeciality
import com.example.ollegeapp.db.Teacher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [TeachersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TeachersFragment : Fragment() {
    private lateinit var recycleView:RecyclerView
    private lateinit var addTeacher:Button
    private lateinit var adapter: TeachersAdapter
    private lateinit var newadapter:TeachersAdapterNew

    private lateinit var database: DatabaseDao
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_teachers, container, false)
        recycleView = view.findViewById(R.id.recycleviewteachers)
        addTeacher = view.findViewById(R.id.addteacher)
        database = CollegeDatabase.getDatabase(requireContext()).databaseDao()
        adapter = TeachersAdapter()

        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val studentsList = database.getAllTeachers()
            withContext(Dispatchers.Main) {
                adapter.setData(studentsList)
            }



            adapter.setOnDeleteClickListener { position ->
                onDeleteTeacher(position)
            }
            adapter.setOnEditClickListener { position ->
                val teacher = adapter.getTeachersList()[position]
                showEditDialog(teacher)
            }
        }






        addTeacher.setOnClickListener{
            val intent = Intent(requireContext(), AddTeacher::class.java)
            startActivity(intent)
        }

        return view
    }
    private fun onDeleteTeacher(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val teacher = adapter.getTeachersList().getOrNull(position)
            teacher?.let {
                database.deleteTeacher(it)
                val updatedTeachers = database.getAllTeachers()
                withContext(Dispatchers.Main) {
                    adapter.setData(updatedTeachers)
                }
            }
        }
    }
    private fun showEditDialog(teacher: Teacher) {
        val builder = AlertDialog.Builder(requireContext())
        val specialityNames = database.getAllSpecialityNames()
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        var name = teacher.name
        var speciality = teacher.speciality
        var salary = teacher.salary
        var hours = teacher.hoursPerYear

        builder.setTitle("Редактор данных")
        var ed1 = EditText(requireContext())
        var spinner = Spinner(requireContext())
        var ed2 = EditText(requireContext())
        var ed3 = EditText(requireContext())

        ed2.inputType = InputType.TYPE_CLASS_NUMBER
        ed3.inputType = InputType.TYPE_CLASS_NUMBER

        ed1.hint = "Введите ФИО преподавателя"
        val specialityAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, specialityNames
        )
        specialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = specialityAdapter
        ed2.hint = "Введите заработную плату"
        ed3.hint = "Введите количество часов"
        ed1.setText("${name}")
        ed2.setText("${salary}")
        ed3.setText("${hours}")

        layout.addView(ed1)
        layout.addView(ed2)
        layout.addView(ed3)
        layout.addView(spinner)
        builder.setView(layout)
        builder.setPositiveButton("Сохранить") { _, _ ->
            val newinf1 = ed1.text.toString()
            val newinf2 = ed2.text.toString().toDouble()
            val newinf3 = ed3.text.toString().toInt()

            val spinerinfo = spinner.selectedItem

            if (newinf1.isNotEmpty() && newinf2.toString().isNotEmpty() && newinf3.toString().isNotEmpty()) {
                val updatedTeacher = teacher.copy(
                    name = newinf1,
                    salary = newinf2.toDouble(),
                    hoursPerYear = newinf3,
                    speciality = spinerinfo.toString(),
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    val existingTeacher = database.getTeachersWithSpecialityByName(newinf1)
                    if (existingTeacher.isEmpty() || existingTeacher.first().teacher.teacherId == teacher.teacherId) {

                        database.updateTeacher(updatedTeacher)


                        val teachers = database.getAllTeachers()
                        withContext(Dispatchers.Main) {
                            adapter.setData(teachers)
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Отмена") { dialog,_ ->
            dialog.dismiss()

        }
        builder.show()





    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TeachersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TeachersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun exit(view: View) {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
    }
}