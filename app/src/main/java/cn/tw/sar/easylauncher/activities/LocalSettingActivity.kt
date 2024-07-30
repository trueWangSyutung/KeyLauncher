package cn.tw.sar.easylauncher.activities

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cn.tw.sar.easylauncher.R
import cn.tw.sar.easylauncher.beam.Result
import cn.tw.sar.easylauncher.entity.QuickContracts

import cn.tw.sar.easylauncher.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import cn.tw.sar.easylauncher.weight.MyDialog
import com.qweather.sdk.bean.geo.GeoBean
import com.qweather.sdk.view.QWeather
import kotlin.concurrent.thread


class LocalSettingActivity : ComponentActivity() {
    var allList = mutableStateListOf<GeoBean. LocationBean>()
    var isOk = mutableStateOf(false)

    fun  search(cityName: String) {
        isOk.value = false
        allList.clear()
        // 显示加载动画

        QWeather.getGeoCityLookup(this@LocalSettingActivity, cityName,
            object : QWeather.OnResultGeoListener {
            override fun onError(p0: Throwable?) {
                Log.e("LocalSettingActivity", "onError: ${p0?.message}")
            }

            override fun onSuccess(p0: GeoBean?) {
                if (p0 != null) {
                    Log.e("LocalSettingActivity", "onError: ${p0.locationBean}")
                }

                if (p0 != null) {
                    allList.addAll(p0.locationBean)
                    runOnUiThread {
                        Toast.makeText(this@LocalSettingActivity, "搜索成功", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        });


    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sp = getSharedPreferences("settings", MODE_PRIVATE)
        val apiNmae = sp.getString("api", "心知天气").toString()

        var inputCityName = mutableStateOf("")
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
                                    this@LocalSettingActivity,
                                    0
                                )
                            )
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier

                                .padding(10.dp)

                        ) {
                            TextField(
                                value = inputCityName.value, onValueChange = {
                                    inputCityName.value = it
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(
                                        fraction = 0.6f
                                    )
                                    .border(
                                        0.dp, MaterialTheme.colorScheme.onSurface,
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                                    .background(
                                        color = Color.Transparent,
                                        shape = MaterialTheme.shapes.extraLarge
                                    ),
                                maxLines = 1,
                                placeholder = { Text(text =  resources.getString(R.string.input_city_name)) },
                                // 不显示下方的横线
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            Button(
                                onClick = {
                                   search(inputCityName.value)
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(
                                        fraction = 1f
                                    )
                            ) {
                                Text(text = resources.getString(R.string.search_btn))
                                
                            }
                        }
                        AnimatedVisibility(visible = allList.size > 0) {
                            Column {
                                for (item in allList) {
                                    Log.d("LocalSettingActivity", "onCreate: ${item.toString()}")
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp)
                                            .background(
                                                color = getDarkModeBackgroundColor(
                                                    this@LocalSettingActivity,
                                                    1
                                                ), shape = MaterialTheme.shapes.medium
                                            )
                                            .clickable {
                                                var sp = getSharedPreferences("weather",
                                                    MODE_PRIVATE)

                                                sp.edit().putString("city", item.id).apply()
                                                finish()

                                            }
                                    ){
                                        Column(
                                            modifier = Modifier.padding(15.dp)
                                        ) {
                                            Text(
                                                text = item.name,
                                                color = getDarkModeTextColor(this@LocalSettingActivity),
                                                fontSize = 20.sp

                                            )
                                            Text(
                                                text = item.country+item.adm1+item.adm2,
                                                color = getDarkModeTextColor(this@LocalSettingActivity),
                                                fontSize = 15.sp

                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}


