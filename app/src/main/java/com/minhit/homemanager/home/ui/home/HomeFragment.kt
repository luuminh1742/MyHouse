package com.minhit.homemanager.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.adapter.RoomAdapter
import com.minhit.homemanager.home.ui.home.room.NewRoomActivity
import com.minhit.homemanager.home.ui.home.room.RoomActivity
import com.minhit.homemanager.model.RoomModel


class HomeFragment : Fragment(){

    private lateinit var database: DatabaseReference
    private lateinit var fab: FloatingActionButton
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var listRoom:ArrayList<RoomModel>
    private lateinit var gvListRoom:GridView
    private lateinit var txtIsEmpty:TextView
    private lateinit var progressBarHome:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        addControls(view)
        readData()
        addEvents()
        return view
    }

    private fun readData() {
        val readDB = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                listRoom.clear()
                for(i in p0.children){
                    var room = i.getValue<RoomModel>()!!
                    if(room.id == null ){
                        room.id = i.key
                        database.child("${i.key}/id").setValue("${i.key}")
                    }
                    listRoom.add(room!!)
                    roomAdapter.notifyDataSetChanged()
                }
                if(p0.exists()){
                    if (listRoom.isEmpty()){
                        txtIsEmpty.visibility = View.VISIBLE
                    }else{
                        txtIsEmpty.visibility = View.INVISIBLE
                    }
                    progressBarHome.visibility = View.INVISIBLE
                }
            }

        }
        database.addValueEventListener(readDB)
        progressBarHome.visibility = View.INVISIBLE
    }


    private fun addEvents() {
        fab.setOnClickListener { view ->
            startActivity(Intent(activity,NewRoomActivity::class.java))
        }
        gvListRoom.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(activity,RoomActivity::class.java)
            intent.putExtra("room",listRoom.get(position))
            startActivity(intent)
        }
    }

    private fun addControls(view: View) {
        progressBarHome = view.findViewById(R.id.progressBarHome)
        txtIsEmpty = view.findViewById(R.id.txtIsEmpty)
        fab = view.findViewById(R.id.fab)
        gvListRoom = view.findViewById(R.id.gvListRoom)
        listRoom = ArrayList<RoomModel>()
        roomAdapter = RoomAdapter(activity,listRoom)
        gvListRoom.adapter = roomAdapter

        database = Firebase.database.
        getReference("UserHomeManager/${Firebase.auth.currentUser!!.uid}/room")
    }
    fun showToast(s:String){
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show()
    }
}