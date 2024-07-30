package cn.tw.sar.easylauncher.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.tw.sar.easylauncher.entity.QuickContracts
import com.qweather.sdk.bean.base.Code


@Dao
interface QuickContractsDao {
    @Insert
    fun insert(item: QuickContracts)

    @Insert
    fun insertAll(items: List<QuickContracts>)




    @Query("SELECT * FROM quick_contracts")
    fun getAll(): List<QuickContracts>

    @Query("SELECT * FROM quick_contracts limit :limit ")
    fun getLimit(limit: Int): List<QuickContracts>


    @Query("SELECT * FROM quick_contracts WHERE id = :id")
    fun getById(id: Int): QuickContracts?

    @Query("SELECT * FROM quick_contracts WHERE phone = :phone")
    fun getByPhone(phone: String): QuickContracts?

    @Query("SELECT count(*) FROM quick_contracts WHERE phone = :phone")
    fun getCountByPhone(phone: String): Int

    @Update
    fun update(item: QuickContracts)


    @Query("DELETE FROM quick_contracts")
    fun deleteAll()

    @Query("DELETE FROM quick_contracts WHERE id = :id")
    fun deleteById(id: Long)









}