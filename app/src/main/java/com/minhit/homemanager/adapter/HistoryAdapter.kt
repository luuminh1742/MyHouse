package com.minhit.homemanager.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.model.HistoryModel

class HistoryAdapter(var context: FragmentActivity?, var listHistory:ArrayList<HistoryModel>):BaseAdapter() {

    class ViewHolder(view:View){
        var txtStatus:TextView
        var infor:TextView
        var checkStatus:CheckBox
        init {
            txtStatus = view.findViewById(R.id.txtStatus)
            infor = view.findViewById(R.id.txtInformationHistory)
            checkStatus = view.findViewById(R.id.checkStatus)
        }

        fun check(
            context: FragmentActivity?,
            historyModel: HistoryModel
        ) {
            checkStatus.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setCancelable(false)
                alertDialog.setTitle("Confirm")
                alertDialog.setMessage("This month has paid.")
                alertDialog.setPositiveButton("Yes"){_,_->
                    val database: DatabaseReference  = Firebase.database.getReference("UserHomeManager/" +
                            "${Firebase.auth.currentUser!!.uid}/room/${historyModel.idRoom}/history/${historyModel.id}/status")
                    database.setValue(true)
                    historyModel.status = true
                    checkStatus.setText("Paid")
                }
                alertDialog.setNegativeButton("No"){_,_->
                    checkStatus.isChecked = false
                }
                alertDialog.create().show()
            }
        }

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View
        var viewHolder:ViewHolder
        if(convertView == null){
           var layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.custom_list_history,null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        var historyModel = getItem(position) as HistoryModel

        if(historyModel.status == true){
            viewHolder.txtStatus.setBackgroundColor(Color.GREEN)
        }else{
            viewHolder.txtStatus.setBackgroundColor(Color.RED)
        }
        viewHolder.infor.text = historyModel.toString()




        viewHolder.check(context,historyModel)

        if(historyModel.status == true){
            viewHolder.checkStatus.setTextColor(Color.GREEN)
            viewHolder.txtStatus.setBackgroundColor(Color.GREEN)
            viewHolder.checkStatus.isChecked = true
            viewHolder.checkStatus.setText("Paid")
            viewHolder.checkStatus.isEnabled = false
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return listHistory.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listHistory.size
    }
}