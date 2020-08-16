package com.minhit.homemanager.model

data class MemberModel(
    var id:String? = null,
    var name:String = "",
    var yearOfBirth:Int = 0,
    var phone:String = "",
    var address:String = ""
)