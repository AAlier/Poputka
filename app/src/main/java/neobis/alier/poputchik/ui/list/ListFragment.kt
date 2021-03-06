package neobis.alier.poputchik.ui.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.activity_list.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.ui.DetailActivity
import neobis.alier.poputchik.ui.FilterActivity
import neobis.alier.poputchik.ui.map.MapContract
import neobis.alier.poputchik.ui.map.MapPresenter
import neobis.alier.poputchik.util.Client
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.FileUtils
import java.text.SimpleDateFormat
import java.util.*


class ListFragment : Fragment(), MapContract.View {
    private lateinit var presenter: MapPresenter
    private lateinit var adapter: ListAdapter
    private var startTime: String? = null
    var type: Client = Client.RIDER
    private val TAG = "ListFragment"

    companion object {
        fun getInstance(type: Client): ListFragment {
            val bundle = Bundle()
            bundle.putSerializable(CLIENT_TYPE, type)
            val fragment = ListFragment()
            fragment.arguments = bundle
            return fragment
        }

        const val CLIENT_TYPE: String = "type_client"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.activity_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    override fun onResume() {
        super.onResume()
        retry()
    }

    private fun init() {
        initRecyclerView()
        loadList()
        initPresenter()
        refreshLayout.setOnRefreshListener {
            retry()
        }
    }

    private fun initPresenter() {
        val activity = activity as ListActivity
        presenter = MapPresenter(this, activity.app.service, context)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ListAdapter(ArrayList(), object : ListAdapter.Listener {
            override fun onClick(model: Info?) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra("data", model)
                startActivity(intent)
            }

            override fun onCallUser(phone: String) {
                val phoneNum = Uri.fromParts("tel", phone, null)
                val intent = Intent(Intent.ACTION_DIAL, phoneNum)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
    }

    private fun retry() {
        val type = arguments.getSerializable(CLIENT_TYPE) as? Client ?: Client.DRIVER
        if (type == Client.DRIVER) presenter.loadDrivers() else presenter.loadRiders()
    }

    private fun loadList() {
        var type: Client = Client.DRIVER
        if (arguments != null) {
            type = arguments.getSerializable(CLIENT_TYPE) as? Client ?: Client.DRIVER
        }

        val list = FileUtils.readCache(context, if (type == Client.DRIVER) Const.DIR_DRIVER else Const.DIR_RIDER)
        if (list != null) {
            onLoadList(list, type)
        } else {
            retry()
        }
    }

    override fun showProgress() {
        refreshLayout?.isRefreshing = true
    }

    override fun hideProgress() {
        refreshLayout?.isRefreshing = false
    }

    override fun onLoadList(list: MutableList<Info>, type: Client) {
        if (list.size > 0) {
            adapter.setList(list)
            empty_view.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
        } else {
            empty_view.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
    }

    override fun onResumeError(message: String) {
        Snackbar.make(refreshLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Повторить", {
                    retry()
                })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.filter) {
            val intent = Intent(activity, FilterActivity::class.java)
            intent.putExtra(CLIENT_TYPE, Client.DRIVER)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun refresh() {
        val list = FileUtils.readCache(context, if (type == Client.DRIVER) Const.DIR_DRIVER else Const.DIR_RIDER)
        if (list != null) {
            onLoadList(list, type)
        } else {
            retry()
        }
    }
}