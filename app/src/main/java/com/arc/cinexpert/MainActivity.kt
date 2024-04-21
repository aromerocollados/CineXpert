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

        val viewNavegation = findViewById<BottomNavigationView>(R.id.interfaz)
        NavigationUI.setupWithNavController(viewNavegation, navController)

        // Desactivar navegación para elementos no deseados
        viewNavegation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicio -> {
                    // Navegar al fragmento de inicio si no está ya seleccionado
                    if (navController.currentDestination?.id != R.id.inicio) {
                        navController.navigate(R.id.inicio)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
