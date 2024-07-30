package cn.tw.sar.easylauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import cn.tw.sar.easylauncher.MainActivity
import cn.tw.sar.easylauncher.R
import cn.tw.sar.easylauncher.activities.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.beam.DesktopIcon
import cn.tw.sar.easylauncher.utils.PermissionUtils
import cn.tw.sar.easylauncher.utils.getAllInstallApps
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlin.concurrent.thread

@Composable
fun WebView(
    url : String = "file:///android_asset/yhxy.html",
    modifier: Modifier = Modifier.fillMaxSize()
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.domStorageEnabled = true
            webView.settings.loadsImagesAutomatically = true
            webView.settings.mediaPlaybackRequiresUserGesture = false

            webView.webViewClient = WebViewClient()
            webView.loadUrl(url)
            webView
        })
}

class InitActivity : ComponentActivity() {
    var pages = mutableStateOf(0)
    var isHaveFlout = mutableStateOf(false)
    var isHavePermission = mutableStateOf(false)
    @RequiresApi(Build.VERSION_CODES.R)
    var permissionsNow = PermissionUtils.permissions
    var allList: Set<DesktopIcon> = mutableSetOf()
    var isOk = mutableStateOf(false )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()



        isHaveFlout.value = PermissionUtils.checkShowOnLockScreen(this@InitActivity)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsNow = listOf(
                android.Manifest.permission.QUERY_ALL_PACKAGES,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.INTERNET,
            )
            for (i in 0 until permissionsNow.size){
                Log.d("MainActivity", "permissionsNow: ${permissionsNow[i]}")
            }
        } else {

        }
        isHavePermission.value = PermissionUtils.checkPermissions(this@InitActivity, permissionsNow.toTypedArray())


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in 0 until permissions.size) {
            Log.d("MainActivity", "permissions: ${permissions[i]} grantResults: ${grantResults[i]}")
            if (grantResults[i] == 0) {
                // pages.value += 1
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        for (i in 0 until permissions.size){
            Log.d("MainActivity", "permissions: ${permissions[i]} grantResults: ${grantResults[i]}")
            if (grantResults[i] == 0){
                // pages.value += 1
            }
        }

    }

    @Composable
    fun Greeting() {
        var sp = getSharedPreferences("desktop", MODE_PRIVATE)
        for (app in allList) {
            var showDesktop = remember {
                mutableStateOf(
                    sp.getBoolean(
                        app.packageName + "_showDesktop",
                        false
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(
                        color = getDarkModeBackgroundColor(
                            this@InitActivity,
                            1
                        ), shape = MaterialTheme.shapes.medium
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // 获取应用图标
                    // 匹配屏幕最佳字体大小
                    val icon = packageManager.getApplicationIcon(app.packageName)
                    // 获取应用名
                    val label = packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(
                            app.packageName,
                            PackageManager.GET_META_DATA
                        )
                    ).toString()

                    Image(
                        painter = rememberDrawablePainter(drawable = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(50.dp)
                            .height(50.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.extraLarge
                            )

                    )
                    Text(
                        text = label,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start,
                        color = getDarkModeTextColor(
                            this@InitActivity
                        )
                    )



                }

                Switch(
                    checked = showDesktop.value,
                    onCheckedChange = {
                        // 更新设置和状态
                        showDesktop.value = it
                        var sharepre = getSharedPreferences("desktop", MODE_PRIVATE)
                        sharepre.edit().putBoolean(app.packageName + "_showDesktop", it).apply()

                    }
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("init", MODE_PRIVATE)


        enableEdgeToEdge()
        setContent {
           EasyLauncherTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (pages.value == 0){
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth()
                                .background(
                                    Color.White
                                )
                                .fillMaxHeight()

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .fillMaxHeight(0.1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_public_privacy),
                                    contentDescription = "public privacy",
                                    modifier = Modifier.size(60.dp)
                                )

                            }

                            WebView(
                                "file:///android_asset/ysxy.html",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f),)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {


                                            val intent =
                                                Intent(this@InitActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                ) {
                                    Text("拒绝", color = Color.Blue)
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {
                                            pages.value += 1
                                        }
                                ) {
                                    Text("同意", color = Color.Blue)
                                }

                            }

                        }

                    }
                    else if (pages.value==1){
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth()
                                .background(
                                    Color.White
                                )
                                .fillMaxHeight()

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .fillMaxHeight(0.1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_public_privacy),
                                    contentDescription = "public privacy",
                                    modifier = Modifier.size(60.dp)
                                )

                            }

                            WebView(
                                "file:///android_asset/yhxy.html",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f),)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {


                                            val intent =
                                                Intent(this@InitActivity, MainActivity::class.java)
                                            startActivity(intent)

                                            finish()

                                        }
                                ) {
                                    Text("拒绝", color = Color.Blue)
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {
                                            pages.value += 1
                                        }
                                ) {
                                    Text("同意", color = Color.Blue)
                                }

                            }

                        }
                    }
                    else if (pages.value==2){
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth()
                                .background(
                                    Color.White
                                )
                                .fillMaxHeight()

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .fillMaxHeight(0.1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_public_privacy),
                                    contentDescription = "public privacy",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "我们使用的权限", color = Color.Black, fontSize = 30.sp, modifier = Modifier.padding(10.dp))
                                for (i in 0 until  PermissionUtils.permissions_usages.size){
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .background(
                                                Color(0xFFE0E0E0),
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(text = PermissionUtils.permissions_usages[i],
                                            color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(10.dp))
                                        Text(text = PermissionUtils.permissions_descriptions[i],
                                            color = Color.Black, fontSize = 15.sp,
                                            modifier = Modifier.padding(10.dp))

                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {

                                            val intent =
                                                Intent(this@InitActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()

                                        }
                                ) {
                                    Text("拒绝", color = Color.Blue)
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {
                                            Log.d("MainActivity", "start service")
                                            if (PermissionUtils.checkPermissions(
                                                    this@InitActivity,
                                                    permissionsNow.toTypedArray()
                                                )
                                            ) {
                                                Log.d("MainActivity", "start service2")
                                                pages.value += 1
                                                thread {
                                                    allList = getAllInstallApps(this@InitActivity)
                                                    runOnUiThread {
                                                        isOk.value = true
                                                    }
                                                }
                                            } else {
                                                Log.d("MainActivity", "start service3")
                                                requestPermissions(
                                                    permissionsNow.toTypedArray(),
                                                    1
                                                )

                                            }
                                        }
                                ) {
                                    Text("同意并授权", color = Color.Blue)
                                }

                            }

                        }
                    }
                    else if (pages.value==3){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(1f)
                                .background(
                                    Color.White
                                )
                                .verticalScroll(
                                    rememberScrollState()
                                )
                                .padding(10.dp)
                        ) {
                            Text(text = resources.getString(R.string.appshow), fontSize = 30.sp,
                                modifier = Modifier.padding(10.dp,30.dp,10.dp,30.dp).fillMaxHeight(0.2f)
                                , color = Color.Black)
                            AnimatedVisibility(visible = isOk.value) {
                                Column {
                                    Column(
                                        modifier = Modifier.fillMaxHeight(0.7f)
                                    ) {
                                        Greeting()
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .clickable {

                                                    val intent =
                                                        Intent(
                                                            this@InitActivity,
                                                            MainActivity::class.java
                                                        )
                                                    startActivity(intent)
                                                    finish()

                                                }
                                        ) {
                                            Text("拒绝", color = Color.Blue)
                                        }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .clickable {
                                                    pages.value += 1
                                                }
                                        ) {
                                            Text("下一步", color = Color.Blue)
                                        }

                                    }
                                }

                            }
                        }

                    }
                    else if (pages.value==4){
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth()
                                .background(
                                    Color.White
                                )
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "enjoy",
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(180.dp)
                                    .padding(10.dp)
                                    .clickable {
                                        val editor = sharedPreferences.edit()
                                        editor.putBoolean("isFirst", false)
                                        editor.apply()
                                        editor
                                            .putBoolean("agent", true)
                                            .apply()

                                        val intent =
                                            Intent(this@InitActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .background(
                                        getDarkModeBackgroundColor(
                                            context = this@InitActivity,
                                            level = 1
                                        ),
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                                    .padding(
                                        8.dp
                                    )
                                ,
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "欢迎使用",
                                    color = Color.Blue,
                                    fontSize = 30.sp,
                                    modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
