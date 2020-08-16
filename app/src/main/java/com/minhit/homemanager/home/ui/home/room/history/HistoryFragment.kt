package com.minhit.homemanager.home.ui.home.room.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.adapter.HistoryAdapter
import com.minhit.homemanager.model.HistoryModel
import com.minhit.homemanager.model.RoomModel

class HistoryFragment : Fragment() {

    private lateinit var gvListHistory:GridView
    lateinit var historyAdapter: HistoryAdapter
    lateinit var listHistory:ArrayList<HistoryModel>
    lateinit var roomModel:RoomModel
    lateinit var database: DatabaseReference
    lateinit var txtHistoryIsEmpty:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_history, container, false)
        addControls(view)
        getData()
        return view
    }

    private fun getData() {
        database.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                listHistory.clear()
                for (i in p0.children){
                    val historyModel = i.getValue<HistoryModel>()
                    if(historyModel!!.id == null){
                        historyModel.id = i.key
                        database.child("${i.key}/id").setValue("${i.key}")
                    }
                    listHistory.add(historyModel!!)
                    historyAdapter.notifyDataSetChanged()
                }
                if(p0.exists()){
                    if(listHistory.isEmpty()){
                        txtHistoryIsEmpty.visibility = View.VISIBLE
                    }else{
                        txtHistoryIsEmpty.visibility = View.INVISIBLE
                    }
                }
            }

        })
    }

    private fun addControls(view: View) {
        gvListHistory = view.findViewById(R.id.gvListHistory)
        listHistory = ArrayList()
        historyAdapter = HistoryAdapter(activity,listHistory)
        gvListHistory.adapter = historyAdapter
        txtHistoryIsEmpty = view.findViewById(R.id.txtHistoryIsEmpty)
        if(arguments!=null){
            roomModel = requireArguments().getParcelable<RoomModel>("InforRoom")!!
        }
        database = Firebase.database.getReference("UserHomeManager/" +
                "${Firebase.auth.currentUser!!.uid}/room/${roomModel.id}/history")
    }

}