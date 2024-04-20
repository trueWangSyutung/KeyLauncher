package cn.tw.sar.easylauncher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.easylauncher.activities.AppHiddenActivity
import cn.tw.sar.easylauncher.activities.LocalSettingActivity
import cn.tw.sar.easylauncher.activities.QuickContractsActivity
import cn.tw.sar.easylauncher.beam.Menu
import cn.tw.sar.easylauncher.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class SettingsActivity : ComponentActivity() {
    var menus = listOf(
        Menu("应用显示", 1, 0),
        Menu("快捷联系人",1,4),
        Menu("天气城市设置", 1, 6),
        Menu("天气API设置", 3, 7),
        Menu("API_KEY", 4, 8),


        //Menu("显示更多应用", 0, 1),
        // Menu("显示主屏幕", 0, 2),
        // Menu("负一屏", 0, 3),
        Menu("时钟显秒", 0, 5),
        Menu("开源仓库",1,9)

        )

    var showMoreApps = mutableStateOf(false)
    var showDesktop = mutableStateOf(false)
    var showNegativeOneScreen =mutableStateOf(false)
    var showClockSecond = mutableStateOf(false)


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Greeting2(
        list:List<Menu>,
        background : Color = Color.Black,
        textColor : Color = Color.White,
        subBackground : Color = Color.DarkGray,

        ) {
        var dropDownMenuExpanded = remember {
            mutableStateOf(false)
        }

        var sp = getSharedPreferences("settings", MODE_PRIVATE)
        var api = remember {
            mutableStateOf(sp.getString("api", "心知天气").toString())
        }
        var apiKey = remember {
            mutableStateOf(sp.getString(api.value, "").toString())
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = background
                )
                .padding(10.dp),
        ) {
            for (menu in list) {
                if (menu.type == 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .background(
                                color = subBackground, shape = MaterialTheme.shapes.medium
                            )
                            .clickable {
                                when (menu.name) {
                                    "应用显示" -> {
                                        // 跳转到应用设置
                                        startActivity(
                                            Intent(
                                                this@SettingsActivity,
                                                AppHiddenActivity::class.java
                                            )
                                        )
                                    }

                                    "快捷联系人" -> {
                                        // 跳转到快捷联系人设置
                                        startActivity(
                                            Intent(
                                                this@SettingsActivity,
                                                QuickContractsActivity::class.java
                                            )
                                        )
                                    }

                                    "天气城市设置" -> {
                                        // 跳转到天气城市设置
                                        startActivity(
                                            Intent(
                                                this@SettingsActivity,
                                                LocalSettingActivity::class.java
                                            )
                                        )
                                    }
                                    "开源仓库" -> {

                                        // 跳转到开源仓库,通过浏览器
                                        var url = "https://github.com/trueWangSyutung/KeyLauncher"
                                        var intent = Intent()
                                        intent.action = Intent.ACTION_VIEW
                                        intent.data = android.net.Uri.parse(url)
                                        startActivity(intent)

                                    }

                                }
                            }
                    ){
                        Text(
                            text = menu.name,
                            color = textColor,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
                else if (menu.type == 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .background(
                                color = subBackground, shape = MaterialTheme.shapes.medium
                            )
                            .clickable {
                                when (menu.name) {
                                    "天气API设置" -> {
                                        // 跳转到天气API设置

                                    }
                                }
                            }
                    ){

                        Text(
                            text = menu.name , color = textColor,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(
                                    fraction = 0.4f
                                )
                        )
                        Text(
                            text = "${api.value} >",

                            color = textColor, textAlign = TextAlign.End,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(
                                    fraction = 1f
                                )
                                .clickable {
                                    dropDownMenuExpanded.value = !dropDownMenuExpanded.value
                                },
                        )
                        // 屏幕宽度
                        var width = resources.displayMetrics.widthPixels
                        var dpwidth = width / resources.displayMetrics.density
                        DropdownMenu(expanded = dropDownMenuExpanded.value,
                            offset = DpOffset((dpwidth*0.5-20).dp, 0.dp),
                            modifier = Modifier.background(
                                color = subBackground
                            ),
                            onDismissRequest = {
                                dropDownMenuExpanded.value = false

                            }) {
                            DropdownMenuItem(text = {
                                Text("心知天气", color = textColor, textAlign = TextAlign.End)
                            }, onClick = {
                                api.value = "心知天气"
                                sp.edit().putString("api", "心知天气").apply()
                                apiKey.value = sp.getString("心知天气", "").toString()

                            })
                            DropdownMenuItem(text = {
                                Text("OpenWeatherMap", color = textColor, textAlign = TextAlign.End)
                            }, onClick = {
                                api.value = "OpenWeatherMap"
                                sp.edit().putString("api", "OpenWeatherMap").apply()
                                apiKey.value = sp.getString("OpenWeatherMap", "").toString()
                            })
                        }
                    }
                }
                else if (menu.type == 4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .background(
                                color = subBackground, shape = MaterialTheme.shapes.medium
                            ).padding(10.dp)
                            .clickable {
                                when (menu.name) {
                                    "API_KEY设置" -> {
                                        // 跳转到API_KEY设置

                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){

                        Text(
                            text = menu.name,
                            color = textColor,
                            modifier = Modifier
                                .fillMaxWidth(
                                    fraction = 0.3f
                                )
                        )
                        TextField(value = apiKey.value, onValueChange = {
                            apiKey.value = it
                            sp.edit().putString(api.value, it).apply()
                        },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 1,
                            placeholder = { Text(text = "请输入API_KEY",
                                fontSize = 15.sp,) },
                            shape = MaterialTheme.shapes.extraLarge,
                            textStyle = TextStyle(
                                color = textColor,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Start

                            ),
                            modifier = Modifier
                                .fillMaxWidth(
                                    fraction = 1f
                                )
                        )

                    }
                }
                else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .background(
                                color = subBackground, shape = MaterialTheme.shapes.medium
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = menu.name,
                            color = textColor,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(
                                    fraction = 0.6f
                                )
                        )
                        var checked = false
                        when (menu.name) {
                            "显示更多应用" -> {
                                checked = showMoreApps.value
                            }
                            "显示主屏幕" -> {
                                checked = showDesktop.value
                            }
                            "负一屏" -> {
                                checked = showNegativeOneScreen.value
                            }
                            "时钟显秒" -> {
                                // 时钟显示秒
                                checked = showClockSecond.value
                            }
                        }
                        Switch(checked = checked,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(
                                    fraction = 1f
                                ),
                            onCheckedChange = {
                            // 更新设置和状态
                            when (menu.name) {
                                "显示更多应用" -> {
                                    showMoreApps.value = it
                                }
                                "显示主屏幕" -> {
                                    showDesktop.value = it
                                }
                                "负一屏" -> {
                                    showNegativeOneScreen.value = it
                                }
                                "时钟显秒" -> {
                                    // 时钟显示秒
                                    showClockSecond.value = it
                                }
                            }
                            var sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
                            var editor = sharedPreferences.edit()
                            editor.putBoolean(menu.id.toString(), it)
                            editor.apply()

                        })

                    }
                }
            }
        }
    }



    override fun onRestart() {
        super.onRestart()

    }
    override fun onResume() {
        super.onResume()

        var sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        showMoreApps.value = sharedPreferences.getBoolean("1", false)
        showDesktop.value = sharedPreferences.getBoolean("2", false)
        showNegativeOneScreen.value = sharedPreferences.getBoolean("3", false)
        showClockSecond.value = sharedPreferences.getBoolean("5", false)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        setContent {
            EasyLauncherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@SettingsActivity,
                                    0
                                ),

                                )
                            .padding(10.dp)
                    ) {
                        // 获取 packageName 为 cn.tw.sar.easylauncher 的应用图标
                        var icon =  packageManager.getApplicationIcon("cn.tw.sar.easylauncher")
                        var appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo("cn.tw.sar.easylauncher", 0))
                        var appVersion = packageManager.getPackageInfo("cn.tw.sar.easylauncher", 0).versionName
                        var appVersionCode = packageManager.getPackageInfo("cn.tw.sar.easylauncher", 0).versionCode
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberDrawablePainter(drawable = icon),
                                contentDescription = appName.toString(),
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(10.dp, 30.dp, 10.dp, 10.dp)
                            )
                            Text(
                                text = appName.toString(),
                                fontSize = 30.sp,
                                color =  getDarkModeTextColor(
                                    this@SettingsActivity
                                ),
                                modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 0.dp)
                            )
                            Text(
                                text = "$appVersion ($appVersionCode)",
                                fontSize = 15.sp,
                                color =  getDarkModeTextColor(
                                    this@SettingsActivity
                                ),
                                modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 30.dp)
                            )

                        }
                        val background = getDarkModeBackgroundColor(
                            this@SettingsActivity,
                            0
                        )
                        val subBackground = getDarkModeBackgroundColor(
                            this@SettingsActivity,
                            1
                        )
                        val textColor = getDarkModeTextColor(
                            this@SettingsActivity
                        )
                       Column {
                           Greeting2(
                               menus,
                               background = background,
                               subBackground = subBackground,
                               textColor = textColor
                           )

                       }
                    }

                }
            }
        }
    }
}


