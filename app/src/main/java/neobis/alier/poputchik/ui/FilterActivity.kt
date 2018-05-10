package neobis.alier.poputchik.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.activity_filter.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.ui.list.ListAdapter
import neobis.alier.poputchik.ui.list.ListFragment
import neobis.alier.poputchik.ui.map.MapContract
import neobis.alier.poputchik.ui.map.MapPresenter
import neobis.alier.poputchik.util.Client
import java.text.SimpleDateFormat
import java.util.*


class FilterActivity : BaseActivity(), View.OnClickListener, MapContract.View {

    lateinit var adapter: ListAdapter
    lateinit var mPresenter: MapPresenter
    var startTime: String? = null
    val TAG = "FilterActivity"
    override fun onClick(v: View?) {
        when (v) {
            tvFromTimeFilter -> {
                showCalendarPicker(getString(R.string.msg_start), tvFromTimeFilter)
            }
            tvToTimeFilter -> {
                showCalendarPicker(getString(R.string.msg_end), tvToTimeFilter)
            }
            btFilter -> parseFragment()
        }
    }

    private fun parseFragment() {
        var type: Client = intent.getSerializableExtra(ListFragment.CLIENT_TYPE) as? Client ?: Client.DRIVER
        mPresenter.filterBy(tvFromTimeFilter.text.toString(), tvToTimeFilter.text.toString(),
                "both"/*if (type == Client.DRIVER) "drivers" else "rider" */)
//        val list = FileUtils.readCache(this, if (type == Client.DRIVER) Const.DIR_DRIVER else Const.DIR_RIDER)
//        adapter.setList(list)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        init()
    }

    fun init() {
        mPresenter = MapPresenter(this, app.service, applicationContext)
        initView()
        initToolbar()
        initRecyclerView()
    }

    private fun initView() {
        tvFromTimeFilter.setOnClickListener(this)
        tvToTimeFilter.setOnClickListener(this)
        btFilter.setOnClickListener(this)
    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.filter)
    }

    private fun initRecyclerView() {
        rvFilteredList.layoutManager = LinearLayoutManager(applicationContext)
        adapter = ListAdapter(ArrayList(), object : ListAdapter.Listener {
            override fun onClick(model: Info?) {
                val intent = Intent(applicationContext, DetailActivity::class.java)
                intent.putExtra("data", model)
                startActivity(intent)
            }

            override fun onCallUser(phone: String) {
                val phoneNum = Uri.fromParts("tel", phone, null)
                val intent = Intent(Intent.ACTION_DIAL, phoneNum)
                startActivity(intent)
            }
        })
        rvFilteredList.adapter = adapter
    }

    private fun showCalendarPicker(title: String, view: TextView) {

        val fm = supportFragmentManager
        // Initialize
        val dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(title, "OK", "Cancel");

        val cal = Calendar.getInstance()
        // Assign values
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.set24HoursMode(true);
        dateTimeFragment.minimumDateTime = GregorianCalendar(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).time;
        dateTimeFragment.maximumDateTime = GregorianCalendar(
                cal.get(Calendar.YEAR) + 1,
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
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm", Locale.getDefault())
                startTime = formatter.format(date?.time)
                view.text = startTime
            }

            override fun onNegativeButtonClick(date: Date?) {
//                startTime = null
            }
        })
        // Show

        dateTimeFragment.show(fm, "dialog_time");

    }

    override fun showProgress() {
        showProgressBar()

    }

    override fun hideProgress() {
        hideProgressBar()
    }

    override fun onLoadList(list: MutableList<Info>, type: Client) {
        adapter.setList(list)
    }

    override fun onResumeError(message: String) {
    }


}
