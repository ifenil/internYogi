package com.intern.yogi

sealed class NavigationBar(var route: String, var icon: Int, var title: String){
    object Home : NavigationBar("home", R.drawable.ic_home, "Home")
    object Favourite : NavigationBar("favourite", R.drawable.ic_favourite, "Favourite")
}
