package com.arc.cinexpert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.arc.cinexpert.map.PlacesService

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val placesService = PlacesService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val alicante = LatLng(38.3452, -0.4810)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alicante, 12f))

        mMap.addMarker(MarkerOptions()
            .position(alicante)
            .title("Ubicación Actual")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        placesService.fetchNearbyCinemas(38.3452, -0.4810, "AIzaSyCA6FJywjXZuMouunqU8n0n-nwY7vWYRKQ") { cinemas ->
            activity?.runOnUiThread {
                cinemas.forEach { cinema ->
                    mMap.addMarker(MarkerOptions()
                        .position(cinema.location)
                        .title(cinema.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                }
            }
        }
    }
}
