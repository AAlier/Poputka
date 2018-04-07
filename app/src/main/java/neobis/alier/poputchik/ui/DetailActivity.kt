package neobis.alier.poputchik.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_detail.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.model.Info
import neobis.alier.poputchik.util.FileUtils

/**
 * Created by Alier on 03.04.2018.
 */
class DetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val model = intent.getSerializableExtra("data") as Info

        title = getString(if (model.isDriver) R.string.driver else R.string.rider)

        if (!TextUtils.isEmpty(model.name)) {
            name.text = getString(R.string.name, model.name)
            name.visibility = View.VISIBLE
        } else {
            name.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(model.start_time)) {
            time.text = getString(R.string.time, FileUtils.convertStringToDate(model.start_time))
            time.visibility = View.VISIBLE
        } else time.visibility = View.GONE
        if (!TextUtils.isEmpty(model.start_address)) {
            from.visibility = View.VISIBLE
            from.text = getString(R.string.start_addr, model.start_address)
        } else from.visibility = View.GONE
        if (!TextUtils.isEmpty(model.end_address)) {
            toWhere.visibility = View.VISIBLE
            toWhere.text = getString(R.string.end_addr, model.end_address)
        } else toWhere.visibility = View.GONE
        if (!TextUtils.isEmpty(model.description)) {
            description.visibility = View.VISIBLE
            description.text = getString(R.string.descr, model.description)
        } else description.visibility = View.GONE

        if (!TextUtils.isEmpty(model.phone)) {
            call.visibility = View.VISIBLE
            call.tag = model.phone
            call.setOnClickListener { v ->
                val phone = v.tag as? String
                if (!TextUtils.isEmpty(phone)) {
                    val phoneNum = Uri.fromParts("tel", phone, null)
                    val intent = Intent(Intent.ACTION_DIAL, phoneNum)
                    startActivity(intent)
                }
            }
        } else {
            call.visibility = View.GONE
        }
    }
}