package com.minhit.homemanager.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomModel(
    var id:String? = null,
    var name:Int = 0,
    var member:Int = 0,
    var roomMoney:Int = 0,
    var electricityMoney:Int = 0,
    var numberElectricFirst:Int = 0,
    var waterMoney:Int = 0,
    var numberWaterFirst:Int = 0,
    var checkWaterMoney:Int = 0,
    var internetMoney:Int = 0,
    var checkInternetMoney:Int = 0,
    var otherMoney:Int = 0,
    var notes:String? = null,
    var status:Boolean? = null
): Parcelable