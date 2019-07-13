package com.ucsdextandroid2.petfinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PetsActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_REQUEST_CODE = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
//        val adapter = PetsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        recyclerView.adapter = adapter

        //LivePagedListBuilder of the PetsDataSourceFactory

        checkForLocationPermission(true)
//        val showPermissionRation:Boolean = showPermissionRationalIfAble()
//        if(!showPermissionRation){
//            getLocation()
//        }
    }


    fun checkForLocationPermission(showRational: Boolean){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            toast("Location Permission Granted")
            getLocation()
        }else{
            toast("Location Permission Denied")

            if(showRational && showPermissionRationalIfAble()){
                //Don't need anything here
            }else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }
    private fun showPermissionRationalIfAble(): Boolean{
        val ableToShowRational = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(ableToShowRational){
            showPermissionRational()
            return true
        }else{
            return false
        }
    }
    private fun showPermissionRational(){
        AlertDialog.Builder(this)
            .setTitle("Location")
            .setMessage("We need your location in order to show you pets in your area")
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                if(which == DialogInterface.BUTTON_POSITIVE){
                    checkForLocationPermission(false)
                }
            })
            .setNegativeButton("No Thanks", DialogInterface.OnClickListener { dialog, which ->
                if(which == DialogInterface.BUTTON_NEGATIVE) {
                    getLocationFailed()
                }
            })
            .show()
    }
    private fun getLocation(){
        toast("Getting Location")
    }
    fun toast(toastMessage: String){
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    }
    private fun getLocationFailed(){
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            toast("Getting Location Failed, go to settings to enable the permission")
        }else {
            toast("Getting Location Failed")
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }else{
                getLocationFailed()
            }
        }
    }


    private class PetCardViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = itemView.findViewById(R.id.vnc_image)
        val titleView: TextView = itemView.findViewById(R.id.vnc_title)
        val textView: TextView = itemView.findViewById(R.id.vnc_text)

        companion object {
            fun inflate(parent: ViewGroup): PetCardViewHolder = PetCardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_note_card, parent, false)
            )
        }

        fun bind(note: PetModel?) {
            image.isVisible = note?.imageUrl != null
//            image.loadImageUrl(note?.imageUrl)
            titleView.text = note?.name
            textView.text = note?.breed
        }

    }

}
