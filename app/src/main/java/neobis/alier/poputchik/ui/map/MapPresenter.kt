package neobis.alier.poputchik.ui.map

import android.content.Context
import android.text.TextUtils
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.FileUtils
import neobis.alier.poputchik.util.ForumService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapPresenter(val view: MapContract.View?,
                   val service: ForumService,
                   val context: Context) : MapContract.Presenter {

    override fun loadDrivers() {
        if (isViewAttached())
            view!!.showProgress()
        service.getListDrivers().enqueue(object : Callback<MutableList<Info>> {

            override fun onResponse(call: Call<MutableList<Info>>?, response: Response<MutableList<Info>>?) {
                if (isViewAttached()) {
                    if (response!!.isSuccessful && response.body() != null) {
                        FileUtils.writeCache(context, Const.DIR_DRIVER, response.body())
                        view!!.onLoadList(response.body()!!, true)
                    } else
                        view!!.onResumeError(Const.ERROR_LOAD)
                    view.hideProgress()
                }
            }

            override fun onFailure(call: Call<MutableList<Info>>?, t: Throwable?) {
                if (isViewAttached()) {
                    view!!.onResumeError(Const.ERROR_LOAD)
                    view.hideProgress()
                }
                t?.printStackTrace()
            }
        })
    }

    override fun loadRiders() {
        if (isViewAttached())
            view!!.showProgress()
        service.getListRiders().enqueue(object : Callback<MutableList<Info>> {

            override fun onResponse(call: Call<MutableList<Info>>?, response: Response<MutableList<Info>>?) {
                if (isViewAttached()) {
                    if (response!!.isSuccessful && response.body() != null) {
                        FileUtils.writeCache(context, Const.DIR_RIDER, response.body())
                        view!!.onLoadList(response.body()!!, false)
                    } else
                        view!!.onResumeError(Const.ERROR_LOAD)
                    view.hideProgress()
                }
            }

            override fun onFailure(call: Call<MutableList<Info>>?, t: Throwable?) {
                if (isViewAttached()) {
                    view!!.onResumeError(Const.ERROR_LOAD)
                    view.hideProgress()
                }
                t?.printStackTrace()
            }
        })
    }

    private fun isViewAttached(): Boolean = view != null

    override fun filterBy(start: String?, end: String?, type: String){
        if(isViewAttached()) {
            if (TextUtils.isEmpty(start)) {
                return view!!.onResumeError("Укажите начало времени")
            }
            else if (TextUtils.isEmpty(end)) {
                return view!!.onResumeError("Укажите конечное временя")
            }
            if(isViewAttached()) view!!.showProgress()
            service.filter(start!!, end!!, type).enqueue(object: Callback<MutableList<Info>> {
                override fun onResponse(call: Call<MutableList<Info>>?, response: Response<MutableList<Info>>?) {
                    if(isViewAttached()){
                        if(response!!.isSuccessful && response.body() != null){
                            view!!.onLoadList(response.body()!!, type == "drivers")
                        }else{
                            view!!.onResumeError(Const.ERROR_LOAD)
                        }
                        view.hideProgress()
                    }
                }

                override fun onFailure(call: Call<MutableList<Info>>?, t: Throwable?) {
                    if(isViewAttached()){
                        view!!.onResumeError(Const.ERROR_LOAD)
                        view.hideProgress()
                    }
                    t?.printStackTrace()
                }

            })
        }
    }
}