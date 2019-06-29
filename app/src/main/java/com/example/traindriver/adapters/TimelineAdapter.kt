package com.example.traindriver.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import androidx.recyclerview.widget.RecyclerView
import com.example.traindriver.R
import com.example.traindriver.models.StationObj
import com.example.traindriver.utils.DateTimeUtils
import com.example.traindriver.utils.LocationInterface
import com.example.traindriver.utils.VectorDrawableUtils
import com.github.vipulasri.timelineview.TimelineView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.net.HttpURLConnection
import java.net.URL


class TimelineAdapter (private val mFeedList: List<StationObj>, private val route_id:Int, private val context: Context) : RecyclerView.Adapter<TimelineAdapter.TimeLineViewHolder>() {

    val TAG = "TimelineAdapter"
    private var arrivalServerURL: String = context.getString(R.string.arrivalServerURL)

//    private var mGoogleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
//        .addConnectionCallbacks(this)
//        .addOnConnectionFailedListener(this)
//        .addApi(LocationServices.API)
//        .build()
//    private var mLocationManager: LocationManager? = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    lateinit var mLocation: Location
//    private var mLocationRequest: LocationRequest? = null
//    private val listener: com.google.android.gms.location.LocationListener? = null
//    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
//    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
//
//    lateinit var locationManager: LocationManager

    var longitude:Double = 0.0
    var latitude:Double = 0.0

//    override fun onViewAttachedToWindow(holder: TimeLineViewHolder) {
//        super.onViewAttachedToWindow(holder)
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect()
//            Log.i(TAG, "Imefika1")
//        }
//
//        MultiDex.install(context)
//
////        mGoogleApiClient = GoogleApiClient.Builder(context)
////            .addConnectionCallbacks(this)
////            .addOnConnectionFailedListener(this)
////            .addApi(LocationServices.API)
////            .build()
////
////        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        checkLocation()
//    }
//
//    override fun onViewDetachedFromWindow(holder: TimeLineViewHolder) {
//        super.onViewDetachedFromWindow(holder)
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect()
//        }
//    }
//
//    override fun onConnectionSuspended(p0: Int) {
//
//        Log.i(TAG, "Connection Suspended")
//        mGoogleApiClient.connect()
//    }
//
//    override fun onConnectionFailed(connectionResult: ConnectionResult) {
//        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
//    }
//
//    override fun onLocationChanged(location: Location) {
//
//        var msg = "Updated Location: Latitude " + location.longitude.toString() + location.longitude;
//        latitude = location.latitude
//        longitude = location.longitude
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//
//
//    }
//
//    override fun onConnected(p0: Bundle?) {
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//
//        Log.i(TAG, "Imefika2")
//        startLocationUpdates()
//
//        var fusedLocationProviderClient :
//                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
//        fusedLocationProviderClient .getLastLocation()
//            .addOnSuccessListener(activity, OnSuccessListener<Location> { location ->
//                // Got last known location. In some rare situations this can be null.
//                if (location != null) {
//                    // Logic to handle location object
//                    mLocation = location;
//                    latitude = mLocation.latitude
//                    longitude = mLocation.longitude
//
//                    Log.d(TAG,"Updated Location: Longitude Latitude " + longitude + latitude)
//                }
//            })
//    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        view = layoutInflater.inflate(R.layout.item_layout, parent, false)
        return TimeLineViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val station = mFeedList[position]

        when {
            station.status == "inactive" -> {
                holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.inactive_marker, ContextCompat.getColor(context, R.color.colorGrey500))
            }
            station.status == "active" -> {
                holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.active_marker,  ContextCompat.getColor(context, R.color.colorGrey500))
            }
            else -> {
                holder.timeline.setMarker(ContextCompat.getDrawable(holder.itemView.context, R.drawable.complete_marker), ContextCompat.getColor(context, R.color.colorGreen))
            }
        }

        if (station.date.isNotEmpty()) {
            holder.date.visibility = View.VISIBLE
            holder.date.text = station.date
        } else
            holder.date.visibility = View.GONE

        holder.name.text = station.name

        holder.timeline_item.setOnClickListener{
            if(position!=(mFeedList.size-1) ){
                if(station.status=="active" && station.longitude.substring(0,6)==longitude.toString().substring(0,6) && station.latitude.substring(0,6)==latitude.toString().substring(0,6)){
                    Log.d(TAG,"imefika")
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val time:String = DateTimeUtils.parseDateTime(simpleDateFormat.format(Date()),"yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy")
                    mFeedList[position].date = time
                    mFeedList[position].status = "complete"
                    if(position!=0)
                        mFeedList[position-1].status = "active"
                        notifyItemChanged(position-1)
                    notifyItemChanged(position)

                    val gson = Gson()
                    notifyUsers(station.name,time,gson.toJson(mFeedList))
                }else if(station.longitude.substring(0,6)!=longitude.toString().substring(0,6) || station.latitude.substring(0,6)!=latitude.toString().substring(0,6)){
                    Toast.makeText(context, "Location not reached", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        val date = itemView.text_timeline_date
        val name = itemView.text_timeline_title
        val timeline = itemView.timeline
        val timeline_item = itemView.timeline_item

        init {
            timeline.initLine(viewType)

        }
    }

    private fun notifyUsers(station_name:String, time:String,route_Details:String){
        Log.i("EditText values: ",station_name+" "+time+" "+route_Details+" "+route_id)

        var url = URL(arrivalServerURL+"arrival/arrival.php?station="+station_name+"&time="+time+"&route_details="+route_Details+"&route_id="+route_id)
        val notifyTask = NotifyAsyncTask(url)
        notifyTask.execute()
    }

//    private fun checkLocation(): Boolean {
//        if(!isLocationEnabled())
//            showAlert();
//        return isLocationEnabled();
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }
//
//    private fun showAlert() {
//        val dialog = AlertDialog.Builder(context)
//        dialog.setTitle("Enable Location")
//            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
//            .setPositiveButton("Location Settings", DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
//                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                context.startActivity(myIntent)
//            })
//            .setNegativeButton("Cancel", DialogInterface.OnClickListener { paramDialogInterface, paramInt -> })
//        dialog.show()
//    }
//
//    protected fun startLocationUpdates() {
//        Log.i(TAG, "Imefika3")
//        // Create the location request
//        mLocationRequest = LocationRequest.create()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(UPDATE_INTERVAL)
//            .setFastestInterval(FASTEST_INTERVAL);
//        // Request location updates
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//            mLocationRequest, this);
//    }

    fun getLongitudeLatitude(longitude:Double, latitude: Double){
        this.latitude = latitude
        this.longitude = longitude
    }

    private inner class NotifyAsyncTask: AsyncTask<Void, Void, Void>{

        var url:URL

        constructor(url:URL) : super(){
            this.url = url
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
//            val `in` = BufferedInputStream(urlConnection.inputStream)
                val inputAsString = urlConnection.inputStream.bufferedReader().use { it.readText() }
                Log.i("EditText values: ",inputAsString)
            } finally {
                urlConnection.disconnect()
            }
            return null
        }
    }

}