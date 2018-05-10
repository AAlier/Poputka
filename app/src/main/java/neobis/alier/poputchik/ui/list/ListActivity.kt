package neobis.alier.poputchik.ui.list

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_tabs.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.ui.BaseActivity
import neobis.alier.poputchik.util.Client
import java.text.SimpleDateFormat

class ListActivity : BaseActivity() {
    private lateinit var adapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.list)
        adapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.adapter = adapter

        adapter.addFragment(ListFragment.getInstance(Client.DRIVER), getString(R.string.drivers))
        adapter.addFragment(ListFragment.getInstance(Client.RIDER), getString(R.string.riders))
        tabs.setupWithViewPager(viewpager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (adapter.getItem(viewpager.currentItem) as ListFragment).refresh()

    }

}