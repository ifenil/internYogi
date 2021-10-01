package com.intern.yogi.data

data class Track(
    val artistImage: String, val artistName: String, val image: String,
    val id: String,val trackUrl: String
) {
    constructor() : this("", "", "", "","")
}