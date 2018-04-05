package neobis.alier.poputchik.ui.enter_data

import com.google.android.gms.maps.model.LatLng
import neobis.alier.poputchik.util.IProgressBar
import java.util.*

/**
 * Created by Alier on 03.04.2018.
 */
interface PostContract {

    interface View : IProgressBar {
        fun onSuccess()
        fun onResumeError(message: String)
    }

    interface Presenter {
        fun post(name: String?,
                 phoneNum: String?,
                 startAddr: String?,
                 endAddr: String?,
                 startTime: String?,
                 available: Int?,
                 isDriver: Boolean,
                 selectedLocation: LatLng?,
                 description: String?)
    }
}