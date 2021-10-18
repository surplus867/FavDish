package com.example.favdish.utils

object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    fun dishTypes():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")
        return list
    }

    fun dishCategories(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Hoeddeok")
        list.add("Bulgogi")
        list.add("Samgyeopsal")
        list.add("Japchae")
        list.add("Kimchi")
        list.add("Ddukbokki")
        list.add("Sundubu-jjigae")
        list.add("Bibimbap")
        list.add("Juices")
        list.add("Sandwich")
        list.add("Tea & Coffee")
        list.add("Other")
        return list
    }

    fun dishCookingTime(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")
        return list
    }
}