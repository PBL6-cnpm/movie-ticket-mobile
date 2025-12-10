package com.pbl6.pbl6_cinestech.ui.map

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.databinding.FragmentMapsBinding
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.utils.singleClick
import androidx.core.net.toUri
import hoang.dqm.codebase.base.activity.popBackStack

class MapsFragment : BaseFragment<FragmentMapsBinding, MapViewModel>() {
    private val address by lazy { arguments?.getString("address")?:"245/26 Le Duan, Da nang" }

    private val callback = OnMapReadyCallback { googleMap ->
        if (!address.isNullOrEmpty()) {
            try {
                val geocoder = android.location.Geocoder(requireContext())
                val addresses = geocoder.getFromLocationName(address, 1)

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(address)
                    )

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        var mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit()
        }

        mapFragment.getMapAsync(callback)
        binding.address.text = address
    }

    override fun initListener() {
        binding.btnOpenMap.singleClick {
            if (address.isNotEmpty()) {
                try {
                    val geocoder = android.location.Geocoder(requireContext())
                    val addresses = geocoder.getFromLocationName(address, 1)

                    if (!addresses.isNullOrEmpty()) {
                        val location = addresses[0]
                        val latLng = "${location.latitude},${location.longitude}"

                        val gmmIntentUri = "geo:$latLng?q=$latLng(${Uri.encode(address)})".toUri()
                        val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.btnBack.singleClick {
            popBackStack()
        }
    }

    override fun initData() {}
}
