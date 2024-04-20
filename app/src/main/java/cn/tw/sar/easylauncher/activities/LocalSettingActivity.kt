package cn.tw.sar.easylauncher.activities

import android.content.Intent
import android.icu.text.IDNA.Info
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.easylauncher.beam.CityBean
import cn.tw.sar.easylauncher.beam.Result
import cn.tw.sar.easylauncher.beam.openWeatherMapApi.OpenWeatherCityBean
import cn.tw.sar.easylauncher.beam.openWeatherMapApi.OpenWeatherCityBeanItem
import cn.tw.sar.easylauncher.dao.OpenWeatherMapApi
import cn.tw.sar.easylauncher.dao.WeatherApi
import cn.tw.sar.easylauncher.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread


class LocalSettingActivity : ComponentActivity() {
    var allList : Set<Result> = mutableSetOf()
    var allList2 : List<OpenWeatherCityBeanItem> = mutableListOf()
    var isOk = mutableStateOf(false)
    fun  search(cityName: String,apiNmae: String = "心知天气") {
        var sp = getSharedPreferences("settings", MODE_PRIVATE)
        var baseUrl = ""
        if (apiNmae == "心知天气") {
            baseUrl = "https://api.seniverse.com/"
        } else if (apiNmae == "OpenWeatherMap") {
            baseUrl = "https://api.openweathermap.org/"
        }

        val apiKey = sp.getString(apiNmae, "").toString()
        if (apiKey == "") {
            Toast.makeText(this, "请先设置API Key", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        var retrofit =  Retrofit.Builder()
            //设置网络请求BaseUrl地址
            .baseUrl(baseUrl)
            //设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        //创建网络请求接口实例

        if (apiNmae == "心知天气") {
            val api: WeatherApi = retrofit.create(WeatherApi::class.java)

            val dataCall: Call<CityBean> = api.getCity(apiKey, cityName)
            thread {
                val data: Response<CityBean> = dataCall.execute()
                val result = data.body()
                if (result != null) {

                    runOnUiThread {
                        // 将List转换为Set
                        allList = result.results.toSet()
                        Log.d("LocalSettingActivity", "search: $allList")
                        isOk.value = true

                    }
                }

            }
        } else if (apiNmae == "OpenWeatherMap") {
            val api: OpenWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

            val dataCall: Call<OpenWeatherCityBean> = api.getCity(cityName, 20,apiKey)
            thread {
                try {
                    val data: Response<OpenWeatherCityBean> = dataCall.execute()
                    val result = data.body()
                    if (result != null) {

                        runOnUiThread {
                            // 将List转换为Set
                            Log.d("LocalSettingActivity", "search: $result")
                            allList2 = result
                            isOk.value = true

                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "搜索失败", Toast.LENGTH_SHORT).show()
                    Log.e("LocalSettingActivity", "search: ", e)
                }


            }

        }



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
                                        fraction = 0.7f
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
                                placeholder = { Text(text = "请输入城市名称") },
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
                                   search(inputCityName.value,apiNmae)
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(
                                        fraction = 1f
                                    )
                            ) {
                                Text(text = "搜索")
                                
                            }
                        }
                        AnimatedVisibility(visible = isOk.value) {
                            Column {
                               if (apiNmae == "心知天气") {
                                   for (item in allList) {
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
                                                   var sp = getSharedPreferences("weather", MODE_PRIVATE)
                                                   sp.edit().putString("city", item.name).apply()
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
                                                   text = item.path,
                                                   color = getDarkModeTextColor(this@LocalSettingActivity),
                                                   fontSize = 15.sp

                                               )
                                           }
                                       }
                                   }
                               } else if (apiNmae == "OpenWeatherMap") {
                                   for (item in allList2) {
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
                                                   var sp = getSharedPreferences("weather", MODE_PRIVATE)
                                                   sp.edit().putString("city", item.lat.toString() + "-" + item.lon.toString()).apply()
                                                   finish()

                                               }
                                       ){
                                           Column(
                                               modifier = Modifier.padding(15.dp)
                                           ) {
                                               // 获取当前语言
                                               Text(
                                                   text = item.local_names.zh,
                                                   color = getDarkModeTextColor(this@LocalSettingActivity),
                                                   fontSize = 20.sp

                                               )
                                               Text(
                                                   text = item.name + " " + item.state + " " + item.country,
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
}


