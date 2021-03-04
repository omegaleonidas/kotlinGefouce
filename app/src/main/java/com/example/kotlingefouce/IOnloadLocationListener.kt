package com.example.kotlingefouce

import com.google.android.gms.maps.model.LatLng

interface IOnloadLocationListener {
    fun onLocationLoadSuccess(latLng: List<MyLatLng>)
    fun onLocationLoadFailed(message:String)


}