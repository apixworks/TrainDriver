package com.example.traindriver.models

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName

class User {

    @SerializedName("id")
    var id: Int = 0
    @SerializedName("name")
    var name: String? = null
    @SerializedName("email")
    var email: String? = null
}

class UserResponse {

    @SerializedName("status")
    var status: String?= null
    @SerializedName("user")
    var user: User? = null
}