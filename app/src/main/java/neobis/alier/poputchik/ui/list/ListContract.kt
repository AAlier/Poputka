package neobis.alier.poputchik.ui.list

import neobis.alier.poputchik.model.Info

/**
 * Created by Alier on 03.04.2018.
 */
interface ListContract {

    interface View {
        fun onSuccess(list: MutableList<Info>)
        fun onResumeError(message: String)
    }

    interface Presenter {
        fun loadDrivers()
        fun loadRiders()
    }
}