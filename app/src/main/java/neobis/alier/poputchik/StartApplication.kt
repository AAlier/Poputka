package neobis.alier.poputchik

import android.app.Application
import neobis.alier.poputchik.util.ForumService
import neobis.alier.poputchik.util.Network

class StartApplication : Application() {
    lateinit var service: ForumService

    override fun onCreate() {
        super.onCreate()
        service = Network.initService()
    }

}