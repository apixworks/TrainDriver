package com.example.traindriver

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.traindriver.models.DriverResponse
import com.example.traindriver.models.StationObj
import com.example.traindriver.utils.ServerApi
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class LoginActivity : AppCompatActivity() {

    lateinit var dialog: AlertDialog
    private lateinit var serverURL: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        serverURL = getString(R.string.serverURL)

        loginBtn.setOnClickListener{
            val emailString = email.text.toString()
            val passwordString= password.text.toString()
            if(emailString.isNotEmpty() and passwordString.isNotEmpty()){
                if(verifyAvailableNetwork(this@LoginActivity)){
                    login(emailString,passwordString)
                }else{
                    Toast.makeText(this@LoginActivity,"Please check Connection!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@LoginActivity,"Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email:String, password:String){
        Log.i("EditText values: ",email+" "+password)
        dialog = SpotsDialog.Builder()
            .setContext(this@LoginActivity)
            .setCancelable(false)
            .setTheme(R.style.CustomDialog)
            .build()
            .apply {
                show()
            }

        val gson = Gson()

        val retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ServerApi::class.java)

        val call = api.loginUser(email,password)

        call.enqueue(object : Callback<DriverResponse> {

            override fun onResponse(call: Call<DriverResponse>, response: Response<DriverResponse>) {
                Log.i("ResponseString", gson.toJson(response.body()))
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", gson.toJson(response.body()))
                        parseLoginData(response.body()!!)
                        dialog.hide()
                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response")
                        dialog.hide()
                        Toast.makeText(this@LoginActivity,"Login Unsuccessful!",Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Log.i(
                        "onEmptyResponse",
                        "Returned empty response")
                    Toast.makeText(this@LoginActivity,"Login Unsuccessful!",Toast.LENGTH_SHORT).show()
                    dialog.hide()
                }
            }

            override fun onFailure(call: Call<DriverResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity,"Login Unsuccessful!",Toast.LENGTH_SHORT).show()
                Log.i("onFailure", t.message)
                dialog.hide()
            }
        })
    }

    private fun parseLoginData(response: DriverResponse){
        val gson = Gson()
        try {
            if(response.status!!){
                email.setText("")
                password.setText("")
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                if(response.assignStatus!!){
                    val mDataList = ArrayList<StationObj>()
                    var stations = response.response!!.stations
                    for(i in 0 until stations!!.size){
                        if((i != stations.size-1) and (i != stations.size-2))
                            mDataList.add(StationObj(stations[i].name!!, stations[i].longitude!!, stations[i].latitude!!,"","inactive"))
                        else if(i == stations.size-2)
                            mDataList.add(StationObj(stations[i].name!!, stations[i].longitude!!, stations[i].latitude!!,"","active"))
                        else
                            mDataList.add(StationObj(stations[i].name!!, stations[i].longitude!!, stations[i].latitude!!,"","complete"))
                    }
                    intent.putExtra("route_details",gson.toJson(mDataList))
                    intent.putExtra("route_id",response.response!!.routeId)
                    startActivity(intent)
                }else{
                    startActivity(intent)
                }
                finish()
            }else{
                Toast.makeText(this@LoginActivity,"Login Unsuccessful!",Toast.LENGTH_SHORT).show()
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }

    private fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
