package com.minhit.homemanager.adapter

import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.minhit.homemanager.R
import com.minhit.homemanager.model.HistoryModel
import com.minhit.homemanager.model.RoomModel

class UpdateAdapter(val context: FragmentActivity?,
                    val listUpdate:ArrayList<RoomModel>,
                    var listHistory:ArrayList<HistoryModel>):BaseAdapter() {

    class ViewHolder(view:View){
        var roomName:TextView
        var memberInRoom:TextView
        var status:TextView
        var seeUpdate:ImageView
        var layout_item:LinearLayout
        init {
            roomName = view.findViewById(R.id.txtRoomNameU)
            memberInRoom = view.findViewById(R.id.txtMemberInRoomU)
            status = view.findViewById(R.id.txtStatus)
            seeUpdate = view.findViewById(R.id.imgSeeUpdate)
            layout_item = view.findViewById(R.id.layout_item)
        }
        fun setEvents(
            context: FragmentActivity?,
            roomModel: RoomModel,
            listHistory: ArrayList<HistoryModel>
        ){
            seeUpdate.setOnClickListener {
                val dialog = context?.let { it1 -> Dialog(it1) }
                dialog!!.setContentView(R.layout.dialog_show_update)
                var txtInforHistory = dialog.findViewById<TextView>(R.id.txtInforHistory)
                var txtShowRoomName = dialog.findViewById<TextView>(R.id.txtShowRoomName)
                txtShowRoomName.append("${roomModel.name}")

                for(i in listHistory){
                    if(roomModel.id!!.equals(i.idRoom)){

                        txtInforHistory.text = "Room money : ${i.roomMoney} VND\n" +
                                "Electricity money : ${i.electricityMoney} VND\n" +
                                "Water money : ${i.waterMoney} VND\n" +
                                "Internet money : ${i.internetMoney} VND\n" +
                                "Other money : ${i.otherMoney} VND\n" +
                                "Total money : ${i.sum} VND\n" +
                                "Note : \n${i.note}"
                        break
                    }
                }

                dialog.show()
            }

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View
        var viewHolder:ViewHolder
        if(convertView == null){
            var layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.list_update_money,null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        var roomModel = getItem(position) as RoomModel
        viewHolder.roomName.text = "Room : ${roomModel.name}"
        viewHolder.memberInRoom.text = "Member : ${roomModel.member}"
        if(roomModel.member == 0){
            viewHolder.status.text = "Room is empty"
            viewHolder.seeUpdate.setImageResource(R.drawable.ic_eye_off)
            viewHolder.seeUpdate.isEnabled = false
            viewHolder.status.setTextColor(Color.BLACK)
        }
        if(roomModel.status!! && roomModel.member != 0){
            viewHolder.status.text = "Updated"
            viewHolder.status.setTextColor(Color.BLUE)
        }


        viewHolder.setEvents(context,roomModel,listHistory)
        return view
    }

    override fun getItem(position: Int): Any {
        return listUpdate.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listUpdate.size
    }
}