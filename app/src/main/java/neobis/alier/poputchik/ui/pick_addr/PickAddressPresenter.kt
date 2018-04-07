package neobis.alier.poputchik.ui.pick_addr

import android.location.Geocoder
import android.text.TextUtils
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import java.io.IOException


/**
 * Created by Alier on 03.04.2018.
 */
class PickAddressPresenter(val view: PickAddrContact.View?,
                           val geocoder: Geocoder) : PickAddrContact.Presenter {

    override fun getAddress(latLng: LatLng?) {
        try {
            val data = geocoder.getFromLocation(latLng!!.latitude, latLng.longitude, 1)
            var currentAddress = ""
            if (data.size > 0) {
                for (i in 0 until data.get(0).maxAddressLineIndex) {
                    currentAddress += data.get(0).getAddressLine(i) + if (i == 0) "\n" else " "
                }
                if (TextUtils.isEmpty(currentAddress) && data.get(0).getAddressLine(0) != null)
                    currentAddress = data.get(0).getAddressLine(0)
            }
            if (isViewAttached()) view!!.setAddress(currentAddress)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getPlaces(places: AutocompletePrediction, mGoogleApiClient: GoogleApiClient) {
        val placeId = places.placeId
        val placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId)
        placeResult.setResultCallback { place ->
            if (!place.status.isSuccess) {
                place.release()
                return@setResultCallback
            }
            if (isViewAttached()) view!!.moveMap(place.get(0).latLng)
            place.release()
        }
    }

    private fun isViewAttached() = view != null
}