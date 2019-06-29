package com.example.traindriver.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DriverResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("assign_status")
    @Expose
    var assignStatus: Boolean? = null
    @SerializedName("response")
    @Expose
    var response: Response? = null
}

class Response {

    @SerializedName("route_id")
    @Expose
    var routeId: Int? = null
    @SerializedName("stations")
    @Expose
    var stations: List<Station>? = null

}

class Station {

    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("longitude")
    @Expose
    var longitude: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

}