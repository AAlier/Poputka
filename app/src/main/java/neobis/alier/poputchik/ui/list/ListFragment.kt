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
import neobis.alier.poputchik.ui.map.MapContract
import neobis.alier.poputchik.ui.map.MapPresenter
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.FileUtils
import java.text.SimpleDateFormat
import java.util.*


class ListFragment : Fragment(), MapContract.View {
    private lateinit var presenter: MapPresenter
    private lateinit var adapter: ListAdapter
    val TAG = "LIST FRAGMENT"
    private var startTime: String? = null

    companion object {
        fun getInstance(isDriver: Boolean): ListFragment {
            val bundle = Bundle()
            bundle.putBoolean("isDriver", isDriver)
            val fragment = ListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.setHasOptionsMenu(true);
        return inflater!!.inflate(R.layout.activity_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadList()
        val activity = activity as ListActivity
        presenter = MapPresenter(this, activity.app.service, context)

        refreshLayout.setOnRefreshListener {
            retry()
        }
    }

    override fun onResume() {
        super.onResume()
        retry()
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
        val type = arguments.getBoolean("isDriver", false)
        if (type) presenter.loadDrivers() else presenter.loadRiders()
    }

    private fun loadList() {
        val type = arguments.getBoolean("isDriver", false)
        val list = FileUtils.readCache(context, if (type) Const.DIR_DRIVER else Const.DIR_RIDER)
        onLoadList(list, type)
    }

    override fun showProgress() {
        refreshLayout?.isRefreshing = true
    }

    override fun hideProgress() {
        refreshLayout?.isRefreshing = false
    }

    override fun onLoadList(list: MutableList<Info>, isDriver: Boolean) {
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
            showCalendarPicker()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCalendarPicker() {
        // Initialize
        val dateTimeFragment = SwitchDateTimeDialogFragment.newInstance("Title example", "OK", "Cancel");

        val cal = Calendar.getInstance()
        // Assign values
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.set24HoursMode(true);
        dateTimeFragment.minimumDateTime = GregorianCalendar(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).time;
        dateTimeFragment.maximumDateTime = GregorianCalendar(cal.get(Calendar.YEAR) + 1,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).time;

        dateTimeFragment.setDefaultDateTime(GregorianCalendar(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)).time);

        // Define new day and month format
        try {
            dateTimeFragment.simpleDateMonthAndDayFormat = SimpleDateFormat("dd MMMM", Locale.getDefault());
        } catch (e: SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException) {
            Log.e(TAG, e.message);
        }

        // Set listener
        dateTimeFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date?) {
                val type = arguments.getBoolean("isDriver", false)
                val formatter = SimpleDateFormat("", Locale.getDefault())
                if (!TextUtils.isEmpty(startTime)) {
                    presenter.filterBy(startTime,
                            formatter.format(date?.time),
                            if (type) "driver" else "rider")
                    startTime = null
                }else{
                    startTime = formatter.format(date?.time)
                    showCalendarPicker()
                }
            }

            override fun onNegativeButtonClick(date: Date?) {
                startTime = null
            }
        })
        // Show
        dateTimeFragment.show(childFragmentManager, "dialog_time");
    }
}