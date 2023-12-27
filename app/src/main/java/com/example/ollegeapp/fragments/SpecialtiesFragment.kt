package com.example.ollegeapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.*

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ollegeapp.AddSpecialities
import com.example.ollegeapp.R
import com.example.ollegeapp.SignInActivity
import com.example.ollegeapp.adapters.SpecialitiesAdapter
import com.example.ollegeapp.db.CollegeDatabase
import com.example.ollegeapp.db.DatabaseDao
import com.example.ollegeapp.db.Speciality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SpecialtiesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SpecialtiesFragment : Fragment() {
    private lateinit var recycleview:RecyclerView
    private lateinit var addSpecialities: Button
    private lateinit var adapter: SpecialitiesAdapter
    private lateinit var databaseDao: DatabaseDao
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
        var view =  inflater.inflate(R.layout.fragment_specialties, container, false)
        recycleview = view.findViewById(R.id.recycleviewspecialities)
        addSpecialities = view.findViewById(R.id.addspecialities)
        databaseDao = CollegeDatabase.getDatabase(requireContext()).databaseDao()
        adapter = SpecialitiesAdapter()
        recycleview.layoutManager = LinearLayoutManager(requireContext())
        recycleview.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val specialitiesList = databaseDao.getAllSpecialities()
            withContext(Dispatchers.Main) {
                adapter.setData(specialitiesList)
            }
        }

        adapter.setOnDeleteClickListener { position ->
            onDeleteSpeciality(position)
        }
        adapter.setOnEditClickListener { position ->
            val speciality = adapter.getSpecialitiesList()[position]
            showEditDialog(speciality)
        }


        addSpecialities.setOnClickListener{
            val intent = Intent(requireContext(),AddSpecialities::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun showEditDialog(speciality: Speciality) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val editText = EditText(requireContext())
        editText.setText(speciality.speciality)

        dialogBuilder
            .setTitle("Редактирование")
            .setView(editText)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val newSpecialityName = editText.text.toString()
                if (newSpecialityName.isNotEmpty() && newSpecialityName != speciality.speciality) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val existingSpeciality = databaseDao.getSpecialityByName(newSpecialityName)
                        if (existingSpeciality == null && existingSpeciality != speciality) {
                            // Если новой специальности нет в базе данных, обновляем
                            databaseDao.updateSpecialityName(speciality.speciality, newSpecialityName)
                            val updatedSpecialities = databaseDao.getAllSpecialities()
                            withContext(Dispatchers.Main) {
                                adapter.setData(updatedSpecialities)
                            }
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun onDeleteSpeciality(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val speciality = adapter.getSpecialitiesList().getOrNull(position)
            speciality?.let {
                databaseDao.deleteSpeciality(speciality)
                val updatedSpecialities = databaseDao.getAllSpecialities()
                withContext(Dispatchers.Main) {
                    adapter.setData(updatedSpecialities)
                }
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SpecialtiesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SpecialtiesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}