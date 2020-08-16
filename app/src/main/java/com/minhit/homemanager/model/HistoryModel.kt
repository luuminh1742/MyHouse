package com.minhit.homemanager.model

data class HistoryModel(
    var idRoom:String? = null,
    var id:String? = null,
    var roomMoney:Int? = null,
    var electricityMoney:Int? = null,
    var lastElectricnumber:Int? = 0,
    var waterMoney:Int? = null,
    var lastWaterNumber:Int? = 0,
    var internetMoney:Int?= null,
    var otherMoney:Int? = null,
    var sum:Int? = null,
    var date:String? = null,
    var note:String?= null,
    var status:Boolean? = false

){
    fun totalMoney():Int{
        return (roomMoney!! + this!!.electricityMoney!! +
                this!!.waterMoney!! + this!!.internetMoney!! +
                this!!.otherMoney!!)
    }

    override fun toString(): String {
        return "Update day : $date\n" +
                "Room money : ${roomMoney} VND\n" +
                "Electricity money : ${electricityMoney} VND\n" +
                "Water money : ${waterMoney} VND\n" +
                "Internet money : ${internetMoney} VND\n" +
                "Other money : ${otherMoney} VND\n" +
                "Total money : ${sum} VND\n" +
                "Note : \n${note}"
    }
}