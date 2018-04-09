package neobis.alier.poputchik.ui.enter_data

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.activity_post.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.ui.BaseActivity
import neobis.alier.poputchik.ui.pick_addr.PickAddressActivity
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.Const.*
import neobis.alier.poputchik.util.FileUtils
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : BaseActivity(), PostContract.View, View.OnClickListener {

    private var isDriver: Boolean = false
    private lateinit var presenter: PostPresenter
    private var selectedStartLocation: LatLng? = null
    private var selectedEndLocation: LatLng? = null

    companion object {
        val TAG = "PostActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onClick(v: View?) {
        when (v) {
            sendBtn -> sendData()
            start_addrTxt -> goToMap(start_addrTxt.text.toString(), MAP_START, selectedStartLocation)
            end_addr_control -> goToMap(end_addr_control.text.toString(), MAP_END, selectedEndLocation)
            start_timeTxt -> showCalendarPicker(getString(R.string.msg_set_data))
            rider_check -> setUser(false)
            driver_check -> setUser(true)
        }

    }

    private fun setUser(isDriver: Boolean){
        this.isDriver = isDriver
        if (isDriver){
            setTextStyle(driver_check, rider_check  )
        }else{
            setTextStyle(rider_check, driver_check)
        }
    }
    private fun setTextStyle(checked: TextView, unchecked: TextView){
        checked.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        checked.typeface = Typeface.DEFAULT_BOLD
        unchecked.setTextColor(ContextCompat.getColor(this, android.R.color.tertiary_text_light))
        unchecked.typeface = Typeface.DEFAULT
    }
    private fun init() {
        initPresenter()
        sendBtn.setOnClickListener(this)
        start_addrTxt.setOnClickListener(this)
        end_addr_control.setOnClickListener(this)
        start_timeTxt.setOnClickListener(this)
        rider_check.setOnClickListener(this)
        driver_check.setOnClickListener(this)

    }

    private fun sendData() {
        val available = 0
        presenter.post(name_control.editText?.text.toString(), phone_control.editText?.text.toString(),
                start_addrTxt.text.toString(),
                end_addr_control.text.toString(),
                start_timeTxt.text.toString(),
                available,
                isDriver,
                selectedStartLocation,
                selectedEndLocation,
                description_control.editText?.text.toString())
    }

    private fun showCalendarPicker(title: String) {
        // Initialize
        val dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(title, "OK", "Cancel");

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
            Log.e(TAG, e.message)
        }

        // Set listener
        dateTimeFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date?) {
                start_timeTxt.text = SimpleDateFormat("yyyy-MM-dd'T'hh:mm", Locale.getDefault()).format(date?.time)
            }

            override fun onNegativeButtonClick(date: Date?) {

            }
        })
        // Show
        dateTimeFragment.show(supportFragmentManager, "dialog_time");
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

    fun goToMap(address: String, requestCode: Int, selectedLocation: LatLng?) {
        val intent = Intent(this, PickAddressActivity::class.java)
        intent.putExtra("name", address)
        intent.putExtra("location", selectedLocation)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                MAP_START -> {
                    start_addrTxt.text = data?.getStringExtra(Const.MAP_RESULT)
                    selectedStartLocation = data?.extras?.get(Const.MAP_LOCATION) as LatLng
                }
                MAP_END -> {
                    end_addr_control.text = data?.getStringExtra(Const.MAP_RESULT)
                    selectedEndLocation = data?.extras?.get(Const.MAP_LOCATION) as LatLng
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