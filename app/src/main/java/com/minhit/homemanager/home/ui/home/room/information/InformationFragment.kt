package com.minhit.homemanager.home.ui.home.room.information

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.adapter.MemberAdapter
import com.minhit.homemanager.home.HomeActivity
import com.minhit.homemanager.model.MemberModel
import com.minhit.homemanager.model.RoomModel
import java.lang.NumberFormatException

class InformationFragment : Fragment() {


    lateinit var addMember: FloatingActionButton
    lateinit var txtInforRoom:TextView
    lateinit var roomModel:RoomModel
    lateinit var gvInforMember:GridView
    lateinit var memberAdapter:MemberAdapter
    lateinit var listMember:ArrayList<MemberModel>
    lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View? {
        val view:View = inflater.inflate(R.layout.fragment_information, container, false)
        addControls(view)
        getData()
        addEvents()
        return view
    }

    private fun getData() {
        val readDB = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                listMember.clear()
                for(i in p0.children){
                    val memberModel = i.getValue<MemberModel>()
                    if(memberModel!!.id == null){
                        memberModel!!.id = i.key
                        database.child("person/${i.key}/id").setValue("${i.key}")
                    }
                    listMember.add(memberModel!!)
                    memberAdapter.notifyDataSetChanged()
                }
                roomModel.member = p0.childrenCount.toInt()
                if(p0.exists()){
                    showInforRoom(roomModel)
                }
            }

        }
        database.child("person").addValueEventListener(readDB)



    }

    private fun addEvents() {
        addMember.setOnClickListener {
            val dialog = activity?.let { it1 -> Dialog(it1) }
            dialog!!.setContentView(R.layout.dialog_add_member)
            dialog.setCancelable(false)
            var edtNameMember = dialog.findViewById<EditText>(R.id.edtNewNameMember)
            var edtYearOfBirth = dialog.findViewById<EditText>(R.id.edtNewYearOfBirth)
            var edtPhone = dialog.findViewById<EditText>(R.id.edtNewPhone)
            var edtAddress = dialog.findViewById<EditText>(R.id.edtNewAddress)
            var addMember = dialog.findViewById<Button>(R.id.btnAddMember)
            var cancel = dialog.findViewById<Button>(R.id.btnCancelAdd)
            cancel.setOnClickListener {dialog.dismiss()}
            addMember.setOnClickListener {
                if(edtNameMember.text.toString().trim().isEmpty()||
                    edtYearOfBirth.text.toString().trim().isEmpty()||
                    edtPhone.text.toString().trim().isEmpty()||
                    edtAddress.text.toString().trim().isEmpty()){
                    showToast("You need check information again!")
                }else{

                    try {
                        var year:Int = Integer.parseInt(edtYearOfBirth.text.toString())
                        val memberModel = MemberModel(
                            null,
                            edtNameMember.text.toString(),
                            year,
                            edtPhone.text.toString(),
                            edtAddress.text.toString()
                        )
                        database.child("person").push().setValue(memberModel)
                            .addOnSuccessListener {
                                showToast("Add success")
                                dialog.dismiss()
                            }
                            .addOnFailureListener {
                                showToast("Add fail")
                            }
                        roomModel.member++
                       database.child("member").setValue(roomModel.member)
                        showInforRoom(roomModel)
                        dialog.dismiss()
                    }catch (e:NumberFormatException){
                        showToast("Error")
                    }
                }
            }

            dialog.show()
        }

    }


    private fun addControls(view: View) {

        addMember = view.findViewById(R.id.fabAddPerson)
        txtInforRoom = view.findViewById(R.id.txtInforRoom)
        if(arguments!=null){
            roomModel = requireArguments().getParcelable<RoomModel>("InforRoom")!!
        }
        showInforRoom(roomModel)
        gvInforMember = view.findViewById(R.id.gvInforMember)
        listMember = ArrayList<MemberModel>()
        memberAdapter = MemberAdapter(activity,listMember,roomModel)
        gvInforMember.adapter = memberAdapter
        database = Firebase.database.getReference("UserHomeManager/" +
                "${Firebase.auth.currentUser!!.uid}/room/${roomModel.id}")
    }

    private fun showInforRoom(roomModel: RoomModel) {
        txtInforRoom.text = ""
        txtInforRoom.append("Member : ${roomModel.member}\n" +
                "Room money : ${roomModel.roomMoney} VND/Room\n" +
                "Electricity money : ${roomModel.electricityMoney} VND/Number\n" +
                "Water money : ${roomModel.waterMoney} VND/")

        var temp = ""
        when(roomModel.checkWaterMoney){
            0->temp = "Number\n"
            1->temp = "Person\n"
            2->temp = "Room\n"
        }
        txtInforRoom.append("$temp" +
                "Internet money : ${roomModel.internetMoney} VND/")
        when(roomModel.checkInternetMoney){
            0->temp = "Room\n"
            1->temp = "Person\n"
        }
        txtInforRoom.append("$temp" +
                "Other money : ${roomModel.otherMoney} VND\n" +
                "Note : ${roomModel.notes}")

    }

    fun showToast(s:String){
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show()
    }

}