package neobis.alier.poputchik.ui.map

import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.util.Client
import neobis.alier.poputchik.util.IProgressBar

interface MapContract {

    interface View : IProgressBar {
        fun onLoadList(list: MutableList<Info>, type: Client)
        fun onResumeError(message: String)
    }

    interface Presenter {
        fun loadDrivers()
        fun loadRiders()
        fun filterBy(start: String?, end: String?, type: String)
    }
}