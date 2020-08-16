package com.minhit.homemanager.home.ui.home.room

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.minhit.homemanager.R
import com.minhit.homemanager.adapter.ViewPagerAdapter
import com.minhit.homemanager.home.ui.home.room.history.HistoryFragment
import com.minhit.homemanager.home.ui.home.room.information.InformationFragment
import com.minhit.homemanager.model.RoomModel
import kotlinx.android.synthetic.main.activity_room.*

class RoomActivity : AppCompatActivity() {
    lateinit var room:RoomModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        addControls()
    }

    private fun addControls() {
        room = intent.getParcelableExtra("room")
        txtRoomName.text = "Room : "+room.name
        addTabs(viewPager)
        tabLayout.setupWithViewPager(viewPager)

    }

    fun addTabs(viewPager: ViewPager) {
        val bundle = Bundle()
        bundle.putParcelable("InforRoom",room)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val inforFragment = InformationFragment()
        inforFragment.arguments = bundle
        val historyFragment = HistoryFragment()
        historyFragment.arguments = bundle
        adapter.add(inforFragment, "Information")
        adapter.add(historyFragment, "History")
        viewPager.adapter = adapter
    }

    fun clickBack(view: View) {finish()}
}