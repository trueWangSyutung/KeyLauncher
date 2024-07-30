package cn.tw.sar.easylauncher.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.tw.sar.easylauncher.dao.CallHistoryDao
import cn.tw.sar.easylauncher.dao.QuickContractsDao
import cn.tw.sar.easylauncher.entity.CallHistory
import cn.tw.sar.easylauncher.entity.QuickContracts
import cn.tw.sar.easylauncher.entity.TimeConvert

private fun MIGRATION_1_2() = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        /**
         * @Entity(tableName = "call_history")
         * data class CallHistory(
         *     @PrimaryKey(autoGenerate = true)
         *     val id: Long = 0,
         *     var name:String,
         *     var phone:String,
         *     var type:Int = 0,
         *     var time:Date,
         *     var isDelete:Boolean = false
         * )
         */
         val sql = "CREATE TABLE IF NOT EXISTS `call_history` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`name` TEXT, " +
                "`phone` TEXT, " +
                "`type` INT, " +
                "`time` BIGINT, " +
                "`isDelete` INTEGER)"
        database.execSQL(sql)

    }
}


@Database(entities = [QuickContracts::class,CallHistory::class], version = 2, exportSchema = false)
@TypeConverters(TimeConvert::class)
abstract class QuickContractsDatabase : RoomDatabase() {

    abstract fun quickContractsDao() : QuickContractsDao
    abstract fun callHistoryDao() : CallHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QuickContractsDatabase? = null
        fun getDatabase(context: Context): QuickContractsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickContractsDatabase::class.java,
                    "code_database"
                ).addMigrations(MIGRATION_1_2())
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}