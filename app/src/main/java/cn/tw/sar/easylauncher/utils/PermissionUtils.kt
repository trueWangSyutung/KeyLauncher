package cn.tw.sar.easylauncher.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object PermissionUtils {
    //     <uses-permission android:name="android.permission.RECEIVE_SMS" /> <!-- 发送消息 -->
    //    <uses-permission android:name="android.permission.SEND_SMS" /> <!-- 阅读消息 -->
    //    <uses-permission android:name="android.permission.READ_SMS" /> <!-- 写入消息 -->
    //    <uses-permission android:name="android.permission.WRITE_SMS" />

    // <p>1. 电话通信记录权限：我们将用于查找输入的电话号码！！</p>
    // <p>2.拨号权限，通过桌面的按钮，我们可以快捷拨号！！</p>
    @RequiresApi(Build.VERSION_CODES.R)
    val permissions = listOf(
        android.Manifest.permission.QUERY_ALL_PACKAGES,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.CALL_PHONE,
        android.Manifest.permission.INTERNET,

    )
    var permissions_usages = arrayOf(
        "获取您的应用列表",
        "读取电话状态",
        "拨打电话",
    )
    var permissions_descriptions = arrayOf(
        "用于快捷启动应用。",
        "读取当前电话状态。",
        "拨号权限，通过桌面的按钮，我们可以快捷拨号。",
    )
    fun checkShowOnLockScreen(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!checkPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    fun requestPermissions(context: Context, permissions: Array<String>, requestCode: Int) {
        if (!checkPermissions(context, permissions)) {
            // 请求权限


        }
    }

}