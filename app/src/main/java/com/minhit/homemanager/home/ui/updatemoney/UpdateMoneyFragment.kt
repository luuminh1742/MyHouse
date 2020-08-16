package com.minhit.homemanager.home.ui.updatemoney

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.adapter.UpdateAdapter
import com.minhit.homemanager.database.DatabaseHelper
import com.minhit.homemanager.model.HistoryModel
import com.minhit.homemanager.model.RoomModel
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpdateMoneyFragment : Fragment(),DatabaseHelper {

    private val database = Firebase.database.
    getReference("UserHomeManager/${Firebase.auth.currentUser!!.uid}/room")
    private lateinit var btnUpdateMoney:Button
    private lateinit var txtTime:TextView
    private lateinit var txtTotalRooms:TextView
    private lateinit var gvListUpdateMoney: GridView
    private lateinit var listHistory:ArrayList<HistoryModel>
    private lateinit var historyAdapter: UpdateAdapter
    private lateinit var listRooms:ArrayList<RoomModel>
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_money, container, false)
        addControls(view)
        getData()
        addEvents()
        return view
    }

    private fun addEvents() {
        gvListUpdateMoney.setOnItemClickListener { parent, view, position, id ->
            var roomModel = historyAdapter.getItem(position) as RoomModel

            if(roomModel.member == 0){
                showToast("This room is empty.")
            }else{
                updateMoney(roomModel)
            }
        }
        btnUpdateMoney.setOnClickListener {
            setData()
        }
    }

    private fun updateMoney(roomModel: RoomModel) {
        val dialog = activity?.let { Dialog(it) }
        dialog!!.setContentView(R.layout.dialog_update_money)
        dialog.setCancelable(false)
        var edtNumberElectric = dialog.findViewById<EditText>(R.id.edtNumberElectricOfMonth)
        var edtNumberWater = dialog.findViewById<EditText>(R.id.edtNumberWaterOfMonth)
        var btnSave = dialog.findViewById<Button>(R.id.btnSaveUpdate)
        var btnCancel = dialog.findViewById<Button>(R.id.btnCancelUpdate)
        if (roomModel.checkWaterMoney != 0){
            edtNumberWater.gravity = Gravity.CENTER
            edtNumberWater.setText("Overlook")
            edtNumberWater.isEnabled = false
        }

        btnSave.setOnClickListener {
            var numberWater:Int = -1
            var numberElectric:Int = -1
            try {
                if(roomModel.checkWaterMoney == 0){
                    numberWater = Integer.parseInt(edtNumberWater.text.toString().trim())
                }
                numberElectric = Integer.parseInt(edtNumberElectric.text.toString().trim())
                if(numberElectric<roomModel.numberElectricFirst || numberWater<roomModel.numberWaterFirst){
                    showToast("Error enter number.")
                }else{
                    roomModel.status = true
                    processerMoney(roomModel,numberWater,numberElectric)
                    dialog.dismiss()
                }

            }catch (e:NumberFormatException){
                showToast("Error enter number.")
            }

        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun processerMoney(roomModel: RoomModel, numberWater: Int, numberElectric: Int) {

        var historyModel = HistoryModel()
        historyModel.idRoom = roomModel.id
        historyModel.id = null
        historyModel.roomMoney = roomModel.roomMoney

        historyModel.electricityMoney =
            roomModel.electricityMoney* (numberElectric - roomModel.numberElectricFirst)

        if(roomModel.checkInternetMoney == 0){
            historyModel.internetMoney = roomModel.internetMoney
        }else{
            historyModel.internetMoney = roomModel.internetMoney * roomModel.member
        }

        historyModel.otherMoney = roomModel.otherMoney
        var s1 = " - Number electric : ${roomModel.numberElectricFirst} - $numberElectric\n"
        var s2 = ""
        if(roomModel.checkWaterMoney == 0){
            historyModel.waterMoney =
                roomModel.waterMoney * (numberWater - roomModel.numberWaterFirst)
            historyModel.lastWaterNumber = numberWater
            s2 = " - Number water : ${roomModel.numberWaterFirst} - $numberWater\n"
        }else if(roomModel.checkWaterMoney == 1){
            historyModel.waterMoney = roomModel.waterMoney * roomModel.member
        }else if(roomModel.checkWaterMoney == 2){
            historyModel.waterMoney = roomModel.waterMoney
        }

        historyModel.note = s1 + s2
            historyModel.lastElectricnumber = numberElectric
        historyModel.sum = historyModel.totalMoney()
        historyModel.date = "${formatter.format(calendar.time)}"
        historyModel.status = false
        listHistory.add(historyModel)
        historyAdapter.notifyDataSetChanged()

    }


    private fun addControls(view: View) {
        btnUpdateMoney = view.findViewById(R.id.btnUpdateMoney)
        txtTime = view.findViewById(R.id.txtTime)
        txtTotalRooms = view.findViewById(R.id.txtTotalRooms)
        gvListUpdateMoney = view.findViewById(R.id.gvListUpdateMoney)
        listHistory = ArrayList()
        listRooms = ArrayList()
        historyAdapter = UpdateAdapter(activity,listRooms,listHistory)
        gvListUpdateMoney.adapter = historyAdapter
        txtTime.append("${formatter.format(calendar.time)}")
    }

    override fun getData() {
        database.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(i in p0.children){
                    var roomModel = i.getValue<RoomModel>()
                    listRooms.add(roomModel!!)
                    historyAdapter.notifyDataSetChanged()
                }
                if (p0.exists()){
                    txtTotalRooms.text = "${listRooms.size}"
                }
            }
        })


    }

    override fun setData() {
        if(listHistory.isEmpty()){
            showToast("You are not update money.")
        }else{
            for(i in listHistory){
                database.child("${i.idRoom}/history").push().setValue(i)
                database.child("${i.idRoom}/numberWaterFirst").setValue(i.lastWaterNumber)
                database.child("${i.idRoom}/numberElectricFirst").setValue(i.lastElectricnumber)
            }

            // cần chỉnh sửa
            listRooms.clear()
            listHistory.clear()
            //
            showToast("Updated success")
        }
        /*historyAdapter.notifyDataSetChanged()*/
    }

    fun showToast(s:String){
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show()
    }

}