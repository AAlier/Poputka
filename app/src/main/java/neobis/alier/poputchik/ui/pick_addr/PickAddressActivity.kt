package neobis.alier.poputchik.ui.pick_addr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.android.synthetic.main.activity_addr.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.ui.map.MapViewActivity
import neobis.alier.poputchik.util.Const.MAP_LOCATION
import neobis.alier.poputchik.util.Const.MAP_RESULT
import java.util.*
import android.widget.Toast
import com.google.android.gms.maps.model.*
import java.io.IOException


class PickAddressActivity : MapViewActivity(), PickAddrContact.View {

    private lateinit var presenter: PickAddressPresenter
    private var mAdapter: PlaceAutoCompleteAdapter? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var startLocation = LatLng(42.8746, 74.5698)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addr)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
    }

    private fun initView() {
        initApiClient()
        initPresenter()
        initAutoComplete()
        place_clear_button.setOnClickListener {
            place_autocomplete_input.setText("")
        }
    }

    private fun initPresenter() {
        presenter = PickAddressPresenter(this, Geocoder(this, Locale.getDefault()))
    }

    override fun onMapClick(latLng: LatLng?) {
        super.onMapClick(latLng)
        moveMap(latLng)
        presenter.getAddress(latLng)
//        getAddressFromLocation(latLng!!)
    }

/*
    private fun getAddressFromLocation(point: LatLng) {

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        if(addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            val city = addresses[0].locality
            val state = addresses[0].adminArea

            Toast.makeText(this, "" + point.latitude + point.longitude, Toast.LENGTH_LONG).show()
            place_autocomplete_input.setText(address + ", " + city + ", " + state)
        }
    }
*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_check) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (place_autocomplete_input != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(place_autocomplete_input.windowToken, 0)
            }
            if (!TextUtils.isEmpty(place_autocomplete_input.text)) {
                setPickedAddress(place_autocomplete_input.text.toString())
            } else {
                showWarningMessage("Type your address")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun moveMap(latLng: LatLng?) {
        if (mMap != null && latLng != null) {
            startLocation = latLng
            mMap!!.clear()
            mMap!!.addMarker(MarkerOptions().icon(
                    BitmapDescriptorFactory
                            .fromResource(R.mipmap.rider_marker))
                    .anchor(0.0f, 1.0f)
                    .position(startLocation))
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                    .target(startLocation).zoom(15f).build()))
        }
    }


    override fun setAddress(address: String) {
        place_autocomplete_input.setText(address)
    }

    private fun setPickedAddress(address: String) {
        setResult(Activity.RESULT_OK, Intent()
                .putExtra(MAP_RESULT, address)
                .putExtra(MAP_LOCATION, startLocation))
        finish()
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()
    }

    override fun onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected)
            mGoogleApiClient!!.disconnect()
        super.onStop()
    }


    private fun initApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build()
    }

    private fun initAutoComplete() {
        if (mGoogleApiClient != null) {
            place_autocomplete_input.setOnItemClickListener({ parent, view, position, id ->
                presenter.getPlaces(mAdapter?.getItem(position)!!, mGoogleApiClient!!)
            })
            mAdapter = PlaceAutoCompleteAdapter(this, mGoogleApiClient, null, null)
            place_autocomplete_input.setAdapter(mAdapter)
        }
    }
}