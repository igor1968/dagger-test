package com.igordanilchik.daggertest.flows.location.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import butterknife.BindView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.igordanilchik.daggertest.R
import com.igordanilchik.daggertest.common.mvp.view.BaseFragment
import com.igordanilchik.daggertest.flows.location.builder.LocationModule
import com.igordanilchik.daggertest.flows.location.presenter.LocationPresenter
import timber.log.Timber

/**
 * @author Igor Danilchik
 */
class LocationFragment : BaseFragment(), LocationView {

    @BindView(R.id.map_view)
    lateinit var mapView: MapView
    @BindView(R.id.address)
    lateinit var address: TextView

    @InjectPresenter
    lateinit var presenter: LocationPresenter

    private var map: GoogleMap? = null

    override val layoutResID: Int = R.layout.fragment_location

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        MapsInitializer.initialize(activity)
        mapView.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED
                || activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } == PackageManager.PERMISSION_GRANTED) {

            map?.isMyLocationEnabled = false
        }
    }

    override fun requestMap() =
            mapView.getMapAsync {
                map = it
                presenter.onMapReady()
            }


    @SuppressLint("MissingPermission")
    override fun updateMap(location: Location, address: String) {
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.isMyLocationEnabled = true

        updateContent(location, address)
    }

    private fun updateContent(location: Location, addressString: String) {
        Timber.d("updateContent")
        cameraSettings(LatLng(location.latitude, location.longitude))
        address.text = addressString
    }

    private fun cameraSettings(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        map?.animateCamera(cameraUpdate)
        setMarker(latLng)
    }

    private fun setMarker(latLng: LatLng) {
        map?.addMarker(MarkerOptions().position(latLng).draggable(false))?.title = getString(R.string.marker_title)
    }

    override fun showError(e: Throwable) {
        Timber.e(e, "Error: ")

        activity?.let {
            Snackbar.make(it.findViewById(android.R.id.content), "Error: " + e.message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
        }
    }

    @ProvidePresenter
    fun providePresenter(): LocationPresenter {
        return appComponent().plusLocationComponent(LocationModule()).presenter()
    }


}