package cn.tw.sar.easylauncher.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import cn.tw.sar.easylauncher.beam.Contract

object ContractUtils {

    fun checkQuickContractFull(
        context: Context
    ): Boolean {
        val sp = context.getSharedPreferences("quick", Context.MODE_PRIVATE)
        // 获取所有的key，value
        val all = sp.all
        // 统计值为true的个数
        var count = 0
        for (key in all.keys) {
            if (all[key] as Boolean) {
                count++
            }
        }
        return count >= 8

    }

    fun readQuickContract(
        context: Context
    ): Set<Contract> {
        //联系人应用的uri常量
        //Uri指明3件事: content:拿内容，包名:到哪个应用中拿，表名:拿哪个数据
        var uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //1.获取ContentResolver
        var contentResolver=context.contentResolver;
        //2.查询所有联系人
        var cursor=contentResolver.query(uri,null,null,null,null);
        val sharedPreferences = context.getSharedPreferences("quick", Context.MODE_PRIVATE)
        //3.通过游标遍历数据
        var list=convertCursor3(cursor,sharedPreferences);
        //5.关闭游标
        if (cursor != null) {
            cursor.close()
            return list
        }else{
            return HashSet()
        }

    }

    fun readContractByNumberStartWith(
        context: Context
    ): Set<Contract> {
        //联系人应用的uri常量
        //Uri指明3件事: content:拿内容，包名:到哪个应用中拿，表名:拿哪个数据
        var uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //1.获取ContentResolver
        var contentResolver=context.contentResolver;
        //2.查询所有联系人
        var cursor=contentResolver.query(uri,null,null,null,null);

        //3.通过游标遍历数据
        var list=convertCursor(cursor);
        //5.关闭游标
        if (cursor != null) {
            cursor.close()
            return list
        }else{
            return HashSet()
        }

    }

    fun getLogStartWithStr(
        number: String,
        context: Context
    ): ArrayList<Contract> {
        var cr = context.contentResolver;
        // 取最新的30条通话记录
        val limitedCallLogUri = CallLog.Calls.CONTENT_URI.buildUpon()
            .appendQueryParameter(CallLog.Calls.LIMIT_PARAM_KEY, "30").build()

        var cursor = cr.query(limitedCallLogUri, null, CallLog.Calls.NUMBER + " like ?",
            arrayOf("$number%"), CallLog.Calls.DEFAULT_SORT_ORDER )
        Log.d("getLogStartWithStr", "getLogStartWithStr: $cursor")
        // 转换游标为联系人列表
        if (cursor != null) {
            val list = convertCursor2(cursor)
            cursor.close()
            return list
        } else {
            return ArrayList()
        }


    }

    @SuppressLint("Range")
    private fun convertCursor2(cursor: Cursor?): ArrayList<Contract>{
        val list = ArrayList<Contract>()
        val name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val phone = ContactsContract.CommonDataKinds.Phone.NUMBER

        for (i in 0 until cursor!!.count) {
            cursor.moveToPosition(i)

            val contactName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))//获取联系人的名字
            val contactPhone = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))//获取联系人的电话号码

            val lastCallType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
            val lastCallTime = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))
            val lastCallDuration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
            list.add(Contract(contactName, contactPhone, lastCallType, lastCallTime, lastCallDuration))
        }
        return list
    }


    @SuppressLint("Range")
    private fun convertCursor(cursor: Cursor?): Set<Contract> {
        val list = HashSet<Contract>()
        val name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val phone = ContactsContract.CommonDataKinds.Phone.NUMBER
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val contactName = cursor.getString(cursor.getColumnIndex(name))
                val contactPhone = cursor.getString(cursor.getColumnIndex(phone))
                // 过滤调电话里面的空格
                val phone = contactPhone.replace(" ", "")

                list.add(Contract(contactName, phone, null, null, null))
            } while (cursor.moveToNext())
        }
        return list
    }

    @SuppressLint("Range")
    private fun convertCursor3(cursor: Cursor?,sharedPreferences: SharedPreferences): Set<Contract> {
        val list = HashSet<Contract>()
        val name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val phone = ContactsContract.CommonDataKinds.Phone.NUMBER
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val contactName = cursor.getString(cursor.getColumnIndex(name))
                val contactPhone = cursor.getString(cursor.getColumnIndex(phone))
                // 过滤调电话里面的空格
                val phone = contactPhone.replace(" ", "")
                if (sharedPreferences.getBoolean(contactName+"_"+phone, false)) {
                    list.add(Contract(contactName, phone, null, null, null))
                }
            } while (cursor.moveToNext())
        }
        return list
    }
}