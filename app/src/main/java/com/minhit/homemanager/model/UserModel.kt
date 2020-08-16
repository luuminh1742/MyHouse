package com.minhit.homemanager.model

data class UserModel(
    var id:String? = null,
    var name:String = "",
    var email:String = "",
    var password:String? = ""
)