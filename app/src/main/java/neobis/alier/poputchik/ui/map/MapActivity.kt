package neobis.alier.poputchik.ui.map

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_map_view.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.ui.enter_data.PostActivity
import neobis.alier.poputchik.ui.list.ListActivity
import neobis.alier.poputchik.util.Client

class MapActivity : MapViewActivity(), MapContract.View {
    private lateinit var presenter: MapPresenter

    companion object val TAG = "MAP ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
        init()
    }

    private fun init() {
        initPresenter()
    }

    private fun initPresenter() {
        presenter = MapPresenter(this, app.service, this)
        presenter.loadDrivers()
        presenter.loadRiders()
    }

    override fun showProgress() {
        showProgressBar()
    }

    override fun hideProgress() {
        hideProgressBar()
    }

    private fun onLoadRetry() {
        Snackbar.make(coordinator, "Произошла ошибка", Snackbar.LENGTH_INDEFINITE)
                .setAction("Повторить", {
                    reload()
                }).show()
    }

    private fun reload() {
        if (presenter != null) {
            presenter.loadDrivers()
            presenter.loadRiders()
        }
    }

    override fun onLoadList(list: MutableList<Info>, type: Client) {
        if (list.size > 0) {
            drawList(list, type)
        } else {
            showWarningMessage("Запросов ещё нет")
        }
    }

    override fun onResumeError(message: String) {
        onLoadRetry()
        Log.e(TAG, message)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showList ->
                startActivity(Intent(this, ListActivity::class.java))
            R.id.add ->
                startActivity(Intent(this, PostActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        //reload()
        Log.i(TAG, "Resume is called")
    }

    override fun onMyLocationButtonClick(): Boolean {
        return super.onMyLocationButtonClick()
    }
}