package neobis.alier.poputchik.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_list.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.ui.list.ListAdapter
import neobis.alier.poputchik.ui.list.ListFragment
import neobis.alier.poputchik.ui.map.MapContract
import neobis.alier.poputchik.ui.map.MapPresenter
import neobis.alier.poputchik.util.Client
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.FileUtils
import java.text.SimpleDateFormat
import java.util.*
import android.widget.DatePicker
import android.app.DatePickerDialog



class FilterActivity : BaseActivity(), View.OnClickListener, MapContract.View {

    lateinit var adapter: ListAdapter
    lateinit var mPresenter: MapPresenter
    var startTime: String =""
    val TAG ="FilterActivity"
    override fun onClick(v: View?) {
        when (v) {
            tvFromTimeFilter -> Toast.makeText(this, "1", Toast.LENGTH_LONG).show()
            tvToTimeFilter -> Toast.makeText(this, "2", Toast.LENGTH_LONG).show()
            btFilter -> parseFragment()
        }
    }

    private fun parseFragment() {
        var type: Client = Client.RIDER
        val list = FileUtils.readCache(this, if (type == Client.DRIVER) Const.DIR_DRIVER else Const.DIR_RIDER)
        adapter.setList(list)
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
    }

    private fun initRecyclerView() {
        rvFilteredList.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter(ArrayList(), object : ListAdapter.Listener {
            override fun onClick(model: Info?) {
            }

            override fun onCallUser(phone: String) {
            }
        })
        recyclerView.adapter = adapter
    }

    private fun showCalendarPicker(title: String) {

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
                val type = intent.getSerializableExtra(ListFragment.CLIENT_TYPE) as? Client ?: Client.DRIVER
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm", Locale.getDefault())
                if (!TextUtils.isEmpty(startTime)) {
                    mPresenter.filterBy(startTime,
                            formatter.format(date?.time),
                            if (type == Client.DRIVER) "drivers" else "rider")
//                    startTime = null
                } else {
                    startTime = formatter.format(date?.time)
                    showCalendarPicker(resources.getString(R.string.msg_end))
                }
            }

            override fun onNegativeButtonClick(date: Date?) {
//                startTime = null
            }
        })
        // Show
        dateTimeFragment.show(fm, "dialog_time");
    }
    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onLoadList(list: MutableList<Info>, type: Client) {
    }

    override fun onResumeError(message: String) {
    }


}
