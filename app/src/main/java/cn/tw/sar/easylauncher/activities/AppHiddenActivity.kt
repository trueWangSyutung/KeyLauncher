package cn.tw.sar.easylauncher.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.easylauncher.R
import cn.tw.sar.easylauncher.SettingsActivity
import cn.tw.sar.easylauncher.activities.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.beam.DesktopIcon
import cn.tw.sar.easylauncher.utils.getAllInstallApps
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlin.concurrent.thread

class AppHiddenActivity : ComponentActivity() {
    var allList: Set<DesktopIcon> = mutableSetOf()
    var isOk = mutableStateOf(false )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread {
            allList = getAllInstallApps(this)
            runOnUiThread {
                isOk.value = true
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
                                this@AppHiddenActivity,
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
                                this@AppHiddenActivity
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

        setContent {
            EasyLauncherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@AppHiddenActivity,
                                    0
                                )
                            )
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(10.dp)
                    ) {
                        Text(text = resources.getString(R.string.appshow), fontSize = 30.sp,
                            modifier = Modifier.padding(10.dp,30.dp,10.dp,30.dp)
                            , color = getDarkModeTextColor(this@AppHiddenActivity))
                        AnimatedVisibility(visible = isOk.value) {
                            Column {
                                Greeting()
                            }
                        }
                    }
                }
            }
        }
    }
}

