package com.example.ollegeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.ollegeapp.fragments.SpecialtiesFragment
import com.example.ollegeapp.fragments.StudentsFragment
import com.example.ollegeapp.fragments.TeachersFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdmissionCommittee : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admission_committee)

        bottomNavigation = findViewById(R.id.bottomNavigationView)
        loadFragment(StudentsFragment())
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_students -> loadFragment(StudentsFragment())
                R.id.navigation_teachers -> loadFragment(TeachersFragment())
                R.id.navigation_specialities->loadFragment(SpecialtiesFragment())
            }
            true
        }

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
