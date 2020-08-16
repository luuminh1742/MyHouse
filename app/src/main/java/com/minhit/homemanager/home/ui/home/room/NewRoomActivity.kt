package com.minhit.homemanager.home.ui.home.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.home.ui.home.HomeFragment
import com.minhit.homemanager.model.RoomModel
import kotlinx.android.synthetic.main.activity_new_room.*
import java.lang.NumberFormatException

class NewRoomActivity : AppCompatActivity() {
    val user = Firebase.auth.currentUser
    val myRef = FirebaseDatabase.getInstance().
                getReference("UserHomeManager/${user!!.uid}/room")


    var roomName:Int = -1
    var roomMoney:Int = -1
    var electricityMoney:Int = -1
    var numberElectricFirst:Int = -1
    var optionWater:Int = 0;
    var numberWaterFirst:Int = -1
    var waterMoney:Int = -1
    var optionInternet:Int = 0
    var internetMoney:Int = -1
    var otherMoney:Int = -1
    var note:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_room)
        addControls()
        addEvents()
    }

    private fun addEvents() {
        spinnerWaterMoney.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                optionWater = position
                if(optionWater == 0) {
                    typeWaterMoney.text = "VND/N"
                    edtNewNumberWater.isEnabled = true
                }
                else if(optionWater == 1) {
                    typeWaterMoney.text = "VND/P"
                    edtNewNumberWater.isEnabled = false
                }
                else {
                    typeWaterMoney.text = "VND/R"
                    edtNewNumberWater.isEnabled = false
                }
            }

        }
        spinnerInternetMoney.onItemSelectedListener = object :
             AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                optionInternet = position
                if(optionInternet == 0) typeInternetMoney.text = "VND/R"
                else typeInternetMoney.text = "VND/P"
            }

        }

    }


    private fun addControls() {
        val checkWater = arrayOf("By number","By person","By room")
        val checkInternet = arrayOf("By room","By person")
        val waterAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,checkWater)
        val internetAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,checkInternet)
        spinnerWaterMoney.adapter = waterAdapter
        spinnerInternetMoney.adapter = internetAdapter
    }

    fun clickCancel(view: View) {finish()}
    fun clickSave(view: View) {
        try {
            roomName = Integer.parseInt(edtNewRoomName.text.trim().toString())
            roomMoney = Integer.parseInt(edtNewRoomMoney.text.trim().toString())
            electricityMoney = Integer.parseInt(edtNewElectricityMoney.text.trim().toString())
            numberElectricFirst = Integer.parseInt(edtNewNumberElectric.text.trim().toString())
            waterMoney = Integer.parseInt(edtNewWaterMoney.text.trim().toString())
            internetMoney = Integer.parseInt(edtNewInternetMoney.text.trim().toString())
            otherMoney = Integer.parseInt(edtNewOtherMoney.text.trim().toString())
            note = edtNewNotes.text.toString()
            if(optionWater!=0) numberWaterFirst = 0
            else numberWaterFirst = Integer.parseInt(edtNewNumberWater.text.trim().toString())
        }catch (ex:NumberFormatException){
            showToast("Error")
        }



        if(roomName ==-1|| electricityMoney==-1||numberElectricFirst==-1||numberWaterFirst==-1
            ||waterMoney==-1||internetMoney==-1||otherMoney==-1){
            showToast("You need check infor again!")
        }else{
            myRef.push().setValue(RoomModel(null,roomName,0,roomMoney,
                electricityMoney,numberElectricFirst, waterMoney,numberWaterFirst,optionWater,
                internetMoney,optionInternet,otherMoney,note,false))
                .addOnSuccessListener {
                    showToast("Add success")
                    finish()
                }
                .addOnFailureListener {
                    showToast("Add fail")
                }
        }





    }
    private fun showToast(s:String){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }
}