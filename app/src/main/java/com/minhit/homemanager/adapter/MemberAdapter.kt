package com.minhit.homemanager.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.home.HomeActivity
import com.minhit.homemanager.model.MemberModel
import com.minhit.homemanager.model.RoomModel


class MemberAdapter(var context: FragmentActivity?, var listMember:ArrayList<MemberModel>,var room:RoomModel):BaseAdapter() {

    class ViewHolder(view:View){
        var txtInfor:TextView
        var imgCall:ImageView
        var imgSendSMS:ImageView
        var imgEdit:ImageView
        var imgDelete:ImageView
        init {
            txtInfor = view.findViewById(R.id.txtInforMember)
            imgCall = view.findViewById(R.id.imgCall)
            imgSendSMS = view.findViewById(R.id.imgSendSMS)
            imgEdit = view.findViewById(R.id.imgEditMember)
            imgDelete = view.findViewById(R.id.imgDeleteMember)
        }
        fun event(context: FragmentActivity?, memberModel: MemberModel,room: RoomModel){

            val myRef = FirebaseDatabase.getInstance().getReference("UserHomeManager" +
                    "/${Firebase.auth.currentUser!!.uid}/room/${room.id}")

            imgEdit.setOnClickListener {
                val dialog = Dialog(context!!)
                dialog.setContentView(R.layout.dialog_add_member)
                dialog.setCancelable(false)
                var title = dialog.findViewById<TextView>(R.id.txtTitle)
                var name = dialog.findViewById<EditText>(R.id.edtNewNameMember)
                var yearOfBirth = dialog.findViewById<EditText>(R.id.edtNewYearOfBirth)
                var phone = dialog.findViewById<EditText>(R.id.edtNewPhone)
                var address = dialog.findViewById<EditText>(R.id.edtNewAddress)
                var save = dialog.findViewById<Button>(R.id.btnAddMember)
                var cancel = dialog.findViewById<Button>(R.id.btnCancelAdd)
                title.text = "Edit member"
                name.setText("${memberModel.name}")
                yearOfBirth.setText("${memberModel.yearOfBirth}")
                phone.setText("${memberModel.phone}")
                address.setText("${memberModel.address}")
                save.setText("Save")
                cancel.setOnClickListener { dialog.dismiss() }
                save.setOnClickListener {
                    if(name.text.toString().trim().isEmpty()||
                            yearOfBirth.text.toString().trim().isEmpty()||
                            phone.text.toString().trim().isEmpty()||
                            address.text.toString().trim().isEmpty()){
                        showToast("You need check information again.")
                    }else{

                        myRef.child("person/${memberModel.id}").setValue(MemberModel(memberModel.id,
                        name.text.toString(),Integer.parseInt(yearOfBirth.text.toString()),
                        phone.text.toString(),address.text.toString()))
                            .addOnSuccessListener {
                                showToast("Edit success")
                                dialog.dismiss()
                            }
                            .addOnFailureListener {
                                showToast("Edit fail")
                            }
                    }
                }
                dialog.show()
            }

            imgDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context!!)
                builder.setCancelable(false)
                builder.setIcon(R.drawable.ic_warring)
                builder.setTitle("Warring")
                builder.setMessage("Confirm delete of member ${memberModel.name}!")
                builder.setPositiveButton("Yes"){_,_->
                    myRef.child("person/${memberModel.id}").removeValue()
                    room.member--
                    myRef.child("member").setValue(room.member)
                }
                builder.setNegativeButton("No"){_,_->}
                builder.create().show()
            }

            imgSendSMS.setOnClickListener {
                try {
                    var number = memberModel.phone
                    val uri = Uri.parse("smsto:$number")
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra("sms_body", "Here goes your message...")
                    context!!.startActivity(intent)
                }catch (e:Exception){
                    showToast("Error send sms")
                }

            }

            imgCall.setOnClickListener {
                try {
                    var number = memberModel.phone
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
                    context!!.startActivity(intent)
                } catch (e: Exception) {
                    showToast("Error call phone")
                }
            }
        }

        private fun showToast(s: String) {

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?
        var viewHolder: ViewHolder
        if(convertView == null){
            var layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.custom_list_member,null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        var memberModel = getItem(position) as MemberModel
        viewHolder.txtInfor.text = ""
        viewHolder.txtInfor.append("Name : ${memberModel.name}\n" +
                "Year of birth : ${memberModel.yearOfBirth}\n" +
                "Phone : ${memberModel.phone}\n" +
                "Address : ${memberModel.address}")

        viewHolder.event(context,memberModel,room)
        return view!!
    }

    override fun getItem(position: Int): Any {
        return listMember.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listMember.size
    }
}