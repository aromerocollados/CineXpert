package com.arc.cinexpert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_contenedor) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.interfaz)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicio -> {
                    // Navegar al fragmento de inicio si no está ya seleccionado
                    if (navController.currentDestination?.id != R.id.inicio) {
                        navController.navigate(R.id.inicio)
                    }
                    true
                }
                R.id.mapa -> {
                    // Navegar al fragmento del mapa si no está ya seleccionado
                    if (navController.currentDestination?.id != R.id.mapa) {
                        navController.navigate(R.id.mapa)
                    }
                    true
                }
                else -> false
            }
        }
    }
}

