package com.intern.yogi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class Main : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            Navigation(navController)
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationBar.Home.route) {
            composable(NavigationBar.Home.route) {
                HomeScreen()
            }
            composable(NavigationBar.Favourite.route) {
                FavouriteScreen()
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            NavigationBar.Home,
            NavigationBar.Favourite,
        )
        BottomNavigation(
            backgroundColor = Black,
            contentColor = White
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(text = item.title) },
                    selectedContentColor = White,
                    unselectedContentColor = White.copy(0.4f),
                    alwaysShowLabel = true,
                    selected = false,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}