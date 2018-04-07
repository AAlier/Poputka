package neobis.alier.poputchik.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.ui.BaseActivity
import neobis.alier.poputchik.ui.DetailActivity
import neobis.alier.poputchik.util.Permissions
import java.util.*

open class MapViewActivity : BaseActivity(), OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener {
    protected var mMap: GoogleMap? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        init()
    }

    private fun init() {
        initGoogleMap()
    }

    private fun initGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
        // По умолчанию Ориентир Бишкек
        val startLatLng = LatLng(42.8746, 74.5698)
        val camPos = CameraPosition.Builder().target(startLatLng).zoom(12f).build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))

        if (Permissions.iPermissionLocation(this))
            setMyLocationEnable()
        mMap!!.setOnMapClickListener(this)
        mMap!!.setOnMarkerClickListener { v ->
            val data = v.tag as? Info?
            if (data != null) {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
                return@setOnMarkerClickListener true
            }
            return@setOnMarkerClickListener false
        }
    }

    override fun onMapClick(latLng: LatLng?) {}

    @SuppressLint("MissingPermission")
    private fun setMyLocationEnable() {
        mMap!!.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permissions.Request.ACCESS_FINE_LOCATION &&
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setMyLocationEnable()
        } else {
            Permissions.iPermissionLocation(this)
        }
    }

    protected fun drawList(list: MutableList<Info>, isDriver: Boolean) {
        if (mMap != null) {
            list.forEach { data ->
                if (data.start_latitude != null && data.start_longitude != null) {
                    val latLng = LatLng(42.8746 + randomWithRange(0.0, 0.02), 74.5698 + randomWithRange(0.0, .02))
                    //LatLng(data.start_latitude!!, data.start_longitude!!)
                    mMap!!.addMarker(MarkerOptions()
                            .title(data.name)
                            .snippet(data.phone)
                            .icon(BitmapDescriptorFactory.fromResource(getBitmap(isDriver)))
                            .anchor(0.0f, 1.0f)
                            .position(latLng)).tag = data
                }
            }
        }
    }

    fun randomWithRange(min: Double, max: Double): Double {
        val r = Random()
        return min + (max - min) * r.nextDouble()
    }

    private fun getBitmap(isDriver: Boolean): Int {
        return if (isDriver) R.mipmap.driver_marker else R.mipmap.rider_marker
    }

    protected fun clearMap() {
        if (mMap != null) {
            mMap!!.clear()
        }
    }

    override fun onMyLocationButtonClick(): Boolean = true
}