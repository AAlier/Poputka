package neobis.alier.poputchik.ui.enter_data

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.activity_post.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.ui.BaseActivity
import neobis.alier.poputchik.ui.pick_addr.PickAddressActivity
import neobis.alier.poputchik.util.Const
import neobis.alier.poputchik.util.Const.*
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : BaseActivity(), PostContract.View{
    private lateinit var presenter: PostPresenter
    private var selectedLocation: LatLng? = null
    companion object {
        val TAG = "PostACtiviyt"
    }
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
//            if(isDriverSw.isChecked)
            /*try {
                available = free_place_control.editText?.text.toString().toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }*/
            presenter.post(
                    name_control.editText?.text.toString(),
                    phone_control.editText?.text.toString(),
                    start_addrTxt.text.toString(),
                    end_addr_control.text.toString(),
                    start_timeTxt.text.toString(),
                    available,
                    false,
                    selectedLocation,
                    description_control.editText?.text.toString())
        }

       /* isDriverSw.setOnCheckedChangeListener { _, b ->
            isDriverSw.text = getString(if (b) R.string.driver else R.string.rider)
            free_view.visibility = if (b) View.VISIBLE else View.GONE
        }*/

        start_addrTxt.setOnClickListener {
            val intent = Intent(this, PickAddressActivity::class.java)
            intent.putExtra("name", start_addrTxt.text.toString())
            intent.putExtra("location", selectedLocation)
            startActivityForResult(intent, MAP_START)
        }
        end_addr_control.setOnClickListener{
            val intent = Intent(this, PickAddressActivity::class.java)
            intent.putExtra("name", end_addr_control.text.toString())
            intent.putExtra("location", selectedLocation)
            startActivityForResult(intent, MAP_END)
        }
        start_timeTxt.setOnClickListener { v ->
            showCalendarPicker(getString(R.string.msg_set_data))
        }

    }


    private fun showCalendarPicker(title: String ) {
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
            Log.e(TAG, e.message);
        }

        // Set listener
        dateTimeFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date?) {
                start_timeTxt.text = SimpleDateFormat("MMMM-dd-yyyy hh:mm", Locale.getDefault()).format(date?.time)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                MAP_START -> {
                    start_addrTxt.text = data?.getStringExtra(Const.MAP_RESULT)
                    selectedLocation = data?.extras?.get(Const.MAP_LOCATION) as LatLng
                }
                MAP_END-> {
                    end_addr_control.text = data?.getStringExtra(Const.MAP_RESULT)
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