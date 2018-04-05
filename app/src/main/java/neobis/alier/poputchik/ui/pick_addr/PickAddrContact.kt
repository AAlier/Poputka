package neobis.alier.poputchik.ui.pick_addr

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Alier on 03.04.2018.
 */
interface PickAddrContact {
    interface View{
        fun moveMap(latLng: LatLng?)
        fun setAddress(address: String)
    }

    interface Presenter{
        fun getAddress(latLng: LatLng?)
    }
}