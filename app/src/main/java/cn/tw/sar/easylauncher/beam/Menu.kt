package cn.tw.sar.easylauncher.beam

data class Menu(
    var name : String,
    var type : Int, // 0：Switch 1：Submenu 2：Action
    var id : Int,
)
