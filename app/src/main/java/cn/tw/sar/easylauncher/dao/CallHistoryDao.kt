package cn.tw.sar.easylauncher.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.tw.sar.easylauncher.entity.CallHistory
import com.qweather.sdk.bean.base.Code


@Dao
interface CallHistoryDao {
    @Insert
    fun insert(item: CallHistory)

    @Insert
    fun insertAll(items: List<CallHistory>)


    @Query("SELECT * FROM call_history")
    fun getAll(): List<CallHistory>

    @Query("SELECT * FROM call_history limit :limit ")
    fun getLimit(limit: Int): List<CallHistory>

    @Query("SELECT * FROM call_history WHERE phone like :phone order by time desc limit :limit ")
    fun getLimitByPhone(phone: String, limit: Int): List<CallHistory>


    @Query("SELECT * FROM call_history WHERE id = :id")
    fun getById(id: Int): CallHistory?

    @Query("SELECT count(*) FROM call_history WHERE phone = :phone")
    fun getCountByPhone(phone: String): Int

    @Update
    fun update(item: CallHistory)


    @Query("DELETE FROM call_history")
    fun deleteAll()

    @Query("DELETE FROM call_history WHERE id = :id")
    fun deleteById(id: Long)









}