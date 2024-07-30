package cn.tw.sar.easylauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.text.format.DateFormat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cn.tw.sar.easylauncher.beam.DesktopIcon
import java.util.ArrayList

fun getInstalledApps(
    context: Context
): Set<DesktopIcon>
{
    val pm = context.packageManager
    // 仅获取用户安装的能启动的应用
    var launcherIconPackageList = ArrayList<ResolveInfo>()
    var desktopUtil = DesktopUtil(
        context
    )
    var intent = Intent();
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);

    //set MATCH_ALL to prevent any filtering of the results
    var resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);

    var res = HashSet<DesktopIcon>()
    var sp = context.getSharedPreferences("desktop", Context.MODE_PRIVATE)

    // 如果数目小于 resolveInfos 的数目，说明有新的应用安装
    for ( info in resolveInfos) {
        // 删除 cn.tw.sar.easylauncher
        if (info.activityInfo.packageName == "cn.tw.sar.easylauncher") {
            //  修改启动 页面 为 SettingsActivity
            info.activityInfo.name = "cn.tw.sar.easylauncher.SettingsActivity"
            continue
        }
        var bool = sp.getBoolean(
            info.activityInfo.packageName+"_showDesktop",
            false
        )
        if (
            bool
        ) {
            var desktopIcon = DesktopIcon(
                type = 0,
                packageName = info.activityInfo.packageName,
                showDesktop = true,
                showDock = false
            )
            res.add(desktopIcon)
        }






    }


    return res

}


fun getAllInstallApps(
    context: Context
): Set<DesktopIcon> {
    val pm = context.packageManager
    // 仅获取用户安装的能启动的应用

    var intent = Intent();
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);

    //获取可以启动的应用

    var resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
    Log.d("resolveInfos", resolveInfos.size.toString())
    var res = HashSet<DesktopIcon>()

    var sp = context.getSharedPreferences("desktop", Context.MODE_PRIVATE)

    // 获取数据库中的应用
    for (info in resolveInfos) {
        // 删除 cn.tw.sar.easylauncher
        if (info.activityInfo.packageName == "cn.tw.sar.easylauncher") {
            Log.d("resolveInfos", "cn.tw.sar.easylauncher")
            //  修改启动 页面 为 SettingsActivity
            info.activityInfo.name = "cn.tw.sar.easylauncher.SettingsActivity"
            continue
        }
        // 如果应用名是 文件管理、文件、浏览器、电话、相机、图库、短信、信息、微信、支付宝、设置、
        val mainapps = arrayOf(
            "com.android.filemanager",
            "com.android.documentsui",
            "com.android.browser",
            "com.android.dialer",
            "com.android.camera",
            "com.android.gallery3d",
            "com.android.mms",
            "com.tencent.mm",
            "com.eg.android.AlipayGphone",
            "com.android.settings",
            "cn.tw.sar.easylauncher",
            "com.android.contacts",
            "com.android.calendar",
            "com.android.music",
            "com.android.deskclock",
            "com.android.email",
        )
        if (mainapps.contains(info.activityInfo.packageName)) {
            // 将
            sp.edit().putBoolean(
                info.activityInfo.packageName+"_showDesktop",
                true
            ).apply()

        }

        res.add(DesktopIcon(
            type = 0,
            packageName = info.activityInfo.packageName,
            showDesktop = sp.getBoolean(
                info.activityInfo.packageName+"_showDesktop",
                false
            ),
            showDock = false
        ))
    }


    return res

}

fun timeFormat(
    time: String?
): String {
    // 转为 long
    val timeLong = time?.toLongOrNull()!!
    val hour = timeLong / 3600
    val minute = (timeLong % 3600) / 60
    val second = timeLong % 60
    return if (hour > 0) {
        String.format("%02d时%02d分%02d秒", hour, minute, second)
    } else if (minute > 0) {
        String.format("%02d分%02d秒", minute, second)
    } else {
        String.format("%02d秒", second)
    }
}
fun dataFormat(
    time: Long
): String {
    // 将时间辍转换为日期 2021-01-01 12:00:00
    return DateFormat.format("yyyy-MM-dd HH:mm:ss", time).toString()

}


fun isDarkMode(
    context: Context
): Boolean {
    val mode = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
    return mode == android.content.res.Configuration.UI_MODE_NIGHT_YES

}


fun getDarkModeTextColor(
    context: Context
): Color {
    return if (isDarkMode(context)) {
        Color.White
    } else {
        Color.Black
    }
}

fun getUnDarkModeTextColor(
    context: Context
): Color {
    return if (!isDarkMode(context)) {
        Color.White
    } else {
        Color.Black
    }
}
@Composable
fun getDarkModeBackgroundColor(
    context: Context,
    level : Int
): Color {
    return if (isDarkMode(context)) {
        if (level == 0) {
            Color.Black
        } else if (level == 1) {
            Color.DarkGray

        } else if (level == 2) {
            MaterialTheme.colorScheme.onTertiaryContainer
        } else {
            Color.Black
        }
    } else {
        if (level == 0) {
            Color.White
        } else if (level == 1) {
            Color(0xFFDDDDDD)
        } else if (level == 2) {
            MaterialTheme.colorScheme.tertiaryContainer

        } else {
            Color.White
        }
    }
}


fun getYesOrNo(
    value: Boolean
): ImageVector {
    return if (value) {
        Icons.Filled.Check
    } else {
        Icons.Filled.Close
    }
}

fun getYesOrNoColor(
    value: Boolean
): Color {
    return if (value) {
        Color.Green
    } else {
        Color.Red
    }
}