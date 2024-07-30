package cn.tw.sar.easylauncher.entity


import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "call_history")
data class CallHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name:String,
    var phone:String,
    var type:Int = 0,
    var time:Date,
    var isDelete:Boolean = false
)



class TimeConvert {

    @TypeConverter
    fun dateToLong (date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun longToDate(value: Long?): Date? {
        if (value == null) {
            return null
        }else{
            return Date(value)
        }
    }

    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun dateToString(date: Date?): String {
        // 转为 日期
        val ca = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return ca.format(date)
        // 转为字符串
    }
}

