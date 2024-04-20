package cn.tw.sar.easylauncher.utils

import android.content.Context

data class MyPermission(
    val permissions: Array<String>,
    val haved: Boolean
)
fun requestReadCallLogPermission(context: Context) : MyPermission {
    val permissions = arrayOf(
        android.Manifest.permission.READ_CALL_LOG,
        android.Manifest.permission.WRITE_CALL_LOG,
    )
    var hasPermission = true
    for (permission in permissions) {
        if (context.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasPermission = false
            return MyPermission(permissions, hasPermission)
        }
    }

    return MyPermission(permissions, hasPermission)

}


fun requestSMSPermission(context: Context) : MyPermission{
    val permissions = arrayOf(
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.SEND_SMS,
    )
    var hasPermission = true
    for (permission in permissions) {
        if (context.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasPermission = false
            return MyPermission(permissions, hasPermission)
        }
    }

    return MyPermission(permissions, hasPermission)
}


fun requestContactPermission(context: Context): MyPermission {
    val permissions = arrayOf(
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.WRITE_CONTACTS,
    )
    var hasPermission = true
    for (permission in permissions) {
        if (context.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasPermission = false
            return MyPermission(permissions, hasPermission)
        }
    }

    return MyPermission(permissions, hasPermission)


}

fun requestPhonePermission(context: Context)  : MyPermission{
    val permissions = arrayOf(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.CALL_PHONE,
    )
    var hasPermission = true
    for (permission in permissions) {
        if (context.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasPermission = false
            return MyPermission(permissions, hasPermission)
        }
    }

    return MyPermission(permissions, hasPermission)

}


fun requestBasePermission(
    context: Context,
) : MyPermission{
    val permissions = arrayOf(
        android.Manifest.permission.QUERY_ALL_PACKAGES,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.INTERNET,
    )
    var hasPermission = true
    for (permission in permissions) {
        if (context.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasPermission = false
            return MyPermission(permissions, hasPermission)
        }
    }

    return MyPermission(permissions, hasPermission)

}


