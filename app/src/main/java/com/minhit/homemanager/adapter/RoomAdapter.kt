package com.minhit.homemanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.minhit.homemanager.R
import com.minhit.homemanager.model.RoomModel

class RoomAdapter(val context: FragmentActivity?, val listRoom:ArrayList<RoomModel>):BaseAdapter(){

    class ViewHolder(view:View){
        var roomNumber:TextView
        var member:TextView
        init {
            roomNumber = view.findViewById(R.id.txtRoomNumber)
            member = view.findViewById(R.id.txtMemeber)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?
        var viewHolder:ViewHolder
        if(convertView == null){
            var layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.custom_list_room,null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        var roomModel :RoomModel = getItem(position) as RoomModel
        viewHolder.roomNumber.text = "Room : "+roomModel.name
        viewHolder.member.text = "Memeber : "+roomModel.member
        return view!!
    }

    override fun getItem(position: Int): Any {
        return listRoom.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listRoom.size
    }
}