package neobis.alier.poputchik.ui.list

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_tabs.*
import neobis.alier.poputchik.R
import neobis.alier.poputchik.ui.BaseActivity

class ListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.adapter = adapter
        adapter.addFragment(ListFragment.getInstance(true), getString(R.string.drivers))
        adapter.addFragment(ListFragment.getInstance(false), getString(R.string.riders))
        tabs.setupWithViewPager(viewpager)
    }
}