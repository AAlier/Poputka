package neobis.alier.poputchik.ui

import android.app.ListFragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_filter.*
import neobis.alier.poputchik.R

class FilterActivity : AppCompatActivity()/*, View.OnClickListener */{

/*
    override fun onClick(v: View?) {
        when (v) {
            tvFromTimeFilter -> Toast.makeText(this, "1", Toast.LENGTH_LONG).show()
            tvToTimeFilter -> Toast.makeText(this, "2", Toast.LENGTH_LONG).show()
        }
    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
//        init()

    }

    /*fun init() {
        initView()
        initToolbar()
    }

    private fun initView() {
        tvFromTimeFilter.setOnClickListener(this)
        tvToTimeFilter.setOnClickListener(this)
    }*/

    private fun initToolbar() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}
