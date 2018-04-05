package neobis.alier.poputchik.ui.enter_data

import android.text.TextUtils
import com.google.android.gms.maps.model.LatLng
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.ForumService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Alier on 03.04.2018.
 */
class PostPresenter(val view: PostContract.View?,
                    val service: ForumService) : PostContract.Presenter {

    override fun post(name: String?,
                      phoneNum: String?,
                      startAddr: String?,
                      endAddr: String?,
                      startTime: String?,
                      available: Int?,
                      isDriver: Boolean,
                      selectedStartLocation: LatLng?,
                      selectedEndLocation: LatLng?,
                      description: String?) {

        if (check(name, phoneNum, startAddr, endAddr, startTime, selectedStartLocation, description)) {
            val model = Info(null, name, phoneNum, isDriver, startAddr, endAddr,
                    selectedStartLocation?.latitude, selectedStartLocation?.longitude,
                    selectedEndLocation?.latitude, selectedEndLocation?.longitude,
                    startTime, description, available)
            request(model)
        }
    }

    private fun request(model: Info) {
        service.postData(model).enqueue(object : Callback<Info> {
            override fun onResponse(call: Call<Info>?, response: Response<Info>?) {
                if (isViewAttached()) {
                    if (response!!.isSuccessful && response.body() != null) {
                        view!!.onSuccess()
                    } else {
                        view!!.onResumeError(Const.ERROR_LOAD)
                    }
                    view.hideProgress()
                }
            }

            override fun onFailure(call: Call<Info>?, t: Throwable?) {
                if (isViewAttached()) {
                    view!!.onResumeError(Const.ERROR_LOAD)
                    view.hideProgress()
                }
                t?.printStackTrace()
            }

        })
    }

    private fun isViewAttached() = view != null

    private fun check(name: String?,
                      phoneNum: String?,
                      startAddr: String?,
                      endAddr: String?,
                      startTime: String?,
                      selectedLocation: LatLng?,
                      description: String?): Boolean {
        if (isViewAttached()) {
            when {
                TextUtils.isEmpty(name) -> {
                    view!!.onResumeError("Укажите имя")
                    return false
                }
                TextUtils.isEmpty(phoneNum) -> {
                    view!!.onResumeError("Добавтье телефонный номер")
                    return false
                }
                TextUtils.isEmpty(startAddr) -> {
                    view!!.onResumeError("Добавтье точку отбытия")
                    return false
                }
                TextUtils.isEmpty(endAddr) -> {
                    view!!.onResumeError("Добавтье конечный пункт назначения")
                    return false
                }
                TextUtils.isEmpty(startTime) -> {
                    view!!.onResumeError("Укажите начало времени")
                    return false
                }
                selectedLocation == null -> {
                    view!!.onResumeError("Укажите местоположение")
                    return false
                }
                TextUtils.isEmpty(description) -> {
                    view!!.onResumeError("Добавьте описание")
                    return false
                }
            }
        }
        return true
    }
}