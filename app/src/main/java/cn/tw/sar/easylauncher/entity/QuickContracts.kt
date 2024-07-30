package cn.tw.sar.easylauncher.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "quick_contracts")
data class QuickContracts(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name:String,
    var phone:String,
)

