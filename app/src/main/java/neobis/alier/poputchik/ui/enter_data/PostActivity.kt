package neobis.alier.poputchik.ui.enter_data

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_post.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.ui.BaseActivity
import neobis.alier.poputchik.ui.pick_addr.PickAddressActivity
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.Const.MAP
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : BaseActivity(), PostContract.View, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var presenter: PostPresenter
    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    private fun init() {
        initPresenter()
        sendBtn.setOnClickListener {
            var available = 0
            if(isDriverSw.isChecked)
            try {
                available = free_place_control.editText?.text.toString().toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            val time = start_timeTxt.getTag(R.integer.date) as? Long?
            var date: Date? = null
            if (time != null) {
                date = Date()
                date.time = time
            }
            presenter.post(
                    name_control.editText?.text.toString(),
                    phone_control.editText?.text.toString(),
                    start_addrTxt.text.toString(),
                    end_addr_control.editText?.text.toString(),
                    SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault()).format(date?.time),
                    available,
                    isDriverSw.isChecked,
                    selectedLocation,
                    description_control.editText?.text.toString())
        }

        isDriverSw.setOnCheckedChangeListener { _, b ->
            isDriverSw.text = getString(if (b) R.string.driver else R.string.rider)
            free_view.visibility = if (b) View.VISIBLE else View.GONE
        }

        start_addrTxt.setOnClickListener {
            val intent = Intent(this, PickAddressActivity::class.java)
            intent.putExtra("name", start_addrTxt.text.toString())
            intent.putExtra("location", selectedLocation)
            startActivityForResult(intent, MAP);
        }

        start_timeTxt.setOnClickListener { v ->
            val curTime = setupCalendar(v.getTag(R.integer.year) as? Int?,
                    v.getTag(R.integer.month) as? Int?,
                    v.getTag(R.integer.day) as? Int?,
                    v.getTag(R.integer.hour) as? Int?,
                    v.getTag(R.integer.minute) as? Int?)

            DatePickerDialog(this, this,
                    curTime.get(Calendar.YEAR),
                    curTime.get(Calendar.MONTH),
                    curTime.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun setupCalendar(year: Int?, month: Int?, day: Int?, hour: Int?, minute: Int?): Calendar {
        val curTime = Calendar.getInstance()
        if (year != null) curTime.set(Calendar.YEAR, year)
        if (month != null) curTime.set(Calendar.MONTH, start_timeTxt.getTag(R.integer.month) as Int)
        if (day != null) curTime.set(Calendar.DAY_OF_MONTH, start_timeTxt.getTag(R.integer.day) as Int)
        if (hour != null) curTime.set(Calendar.HOUR, hour)
        if (minute != null) curTime.set(Calendar.MINUTE, minute)
        return curTime
    }

    private fun initPresenter() {
        presenter = PostPresenter(this, app.service)
    }

    override fun onSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
        this.finish()
    }

    override fun onResumeError(message: String) {
        showWarningMessage(message)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val curTime = Calendar.getInstance()
        start_timeTxt.setTag(R.integer.year, year)
        start_timeTxt.setTag(R.integer.month, month)
        start_timeTxt.setTag(R.integer.day, day)
        TimePickerDialog(this, this,
                curTime.get(Calendar.HOUR_OF_DAY),
                curTime.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)).show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        start_timeTxt.setTag(R.integer.hour, hour)
        start_timeTxt.setTag(R.integer.minute, minute)

        val curTime = setupCalendar(start_timeTxt.getTag(R.integer.year) as? Int?,
                start_timeTxt.getTag(R.integer.month) as? Int?,
                start_timeTxt.getTag(R.integer.day) as? Int?,
                hour, minute)
        if (Calendar.getInstance().timeInMillis > curTime.timeInMillis) {
            showWarningMessage("Время не может быть в прошедшем времени")
        } else {
            start_timeTxt.text = SimpleDateFormat("MMMM-dd-yyyy hh:mm", Locale.getDefault()).format(curTime.timeInMillis)
            start_timeTxt.setTag(R.integer.date, curTime.timeInMillis)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                MAP -> {
                    start_addrTxt.text = data?.getStringExtra(Const.MAP_RESULT)
                    selectedLocation = data?.extras?.get(Const.MAP_LOCATION) as LatLng
                }
            }
        }
    }

    override fun showProgress() {
        showProgressBar()
    }

    override fun hideProgress() {
        hideProgressBar()
    }

}