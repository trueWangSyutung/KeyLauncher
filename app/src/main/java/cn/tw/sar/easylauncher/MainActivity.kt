package cn.tw.sar.easylauncher

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import cn.tw.sar.easylauncher.activities.LocalSettingActivity
import cn.tw.sar.easylauncher.beam.CityBean
import cn.tw.sar.easylauncher.beam.Contract
import cn.tw.sar.easylauncher.beam.DesktopIcon
import cn.tw.sar.easylauncher.beam.openWeatherMapApiW.OpenWeatherBean
import cn.tw.sar.easylauncher.beam.weather2.Result
import cn.tw.sar.easylauncher.beam.weather2.WeatherAPIBean
import cn.tw.sar.easylauncher.dao.OpenWeatherMapApi
import cn.tw.sar.easylauncher.dao.WeatherApi
import cn.tw.sar.easylauncher.utils.ContractUtils
import cn.tw.sar.easylauncher.utils.LunarCalender
import cn.tw.sar.easylauncher.utils.dataFormat
import cn.tw.sar.easylauncher.utils.getAllInstallApps
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import cn.tw.sar.easylauncher.utils.getInstalledApps
import cn.tw.sar.easylauncher.utils.timeFormat
import cn.tw.sar.easylauncher.weight.KeyBoard
import cn.tw.sar.easylauncher.weight.LineBar
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import kotlin.concurrent.thread
import kotlin.math.ceil
import kotlin.math.floor


class MainActivity : ComponentActivity() {
    var appList: Set<DesktopIcon> = mutableSetOf()
    var allList: Set<DesktopIcon> = mutableSetOf()
    var quickContract : Set<Contract> = mutableSetOf()
    fun requestPermission() {
        val permissions = arrayOf(
            android.Manifest.permission.QUERY_ALL_PACKAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.WRITE_CALL_LOG,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.INTERNET,

        )
        // 检查权限
        var hasPermission = true
        for (permission in permissions) {
            if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                hasPermission = false
                break
            }
        }
        // 请求权限
        if (!hasPermission){
            requestPermissions(permissions, 0)

        }

    }
    lateinit var locationManager: LocationManager
    var haveNetWorkPermission = mutableStateOf(false)




    @Preview(showBackground = true)
    @Composable
    fun DesktopIcon(
        app: DesktopIcon = DesktopIcon(
            type = 0,
            packageName = "cn.tw.sar.easylauncher",
            showDesktop = true,
            showDock = false
        ),
        width: Dp = 100.dp,
        isXuanzhong: Boolean = false,

        ) {
        if (app.type >= 96){
            Row(
                modifier = Modifier
                    .width(width)
                    .height(60.dp)
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .background(
                        color = getDarkModeBackgroundColor(
                            this@MainActivity,
                            1
                        ), shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 2.dp,
                        color = if (isXuanzhong) {
                            Color.Blue
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        if (app.packageName == "more") {
                            // 启动活动
                            showMoreApps.value = !showMoreApps.value
                            getPageAndPageSize()
                            currAppNumber.value = 0
                            page.value = 1
                        } else if (app.packageName == "settings") {
                            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                            startActivity(intent)
                        }

                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start


            ) {
                // 获取应用图标
                // 匹配屏幕最佳字体大小

                var drawable = if (app.type == 99) {
                    resources.getDrawable(R.drawable.apps)
                }else if (app.type == 98) {
                        // 从包管理器获取图标 ， mipMap 为 true
                    packageManager.getApplicationIcon("cn.tw.sar.easylauncher")


                } else {
                    resources.getDrawable(R.drawable.apps)
                }
                Image(
                    painter = rememberDrawablePainter(drawable = drawable),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(50.dp)
                        .height(50.dp)

                )
                var text = if (app.type == 99) {
                    "更多应用"
                }else if (app.type == 98) {
                    "桌面设置"
                } else {
                    "更多"
                }
                Text(
                    text = text,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start,
                    color = getDarkModeTextColor(
                        this@MainActivity
                    )
                )



            }
        }
        else if (app.type == 88) {
            var name = app.packageName.split("_")[0]
            var phone = app.packageName.split("_")[1]
            Row(
                modifier = Modifier
                    .width(width)
                    .height(80.dp)
                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
                    .background(
                        color = getDarkModeBackgroundColor(
                            this@MainActivity,
                            1
                        ), shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 2.dp,
                        color = if (isXuanzhong) {
                            Color.Blue
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        // 启动活动

                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = android.net.Uri.parse("tel:${phone}")
                        startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // 获取应用图标
                // 匹配屏幕最佳字体大小
                Image(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(50.dp)
                        .height(50.dp)

                )
                Column {
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start,
                        color = getDarkModeTextColor(
                            this@MainActivity
                        )
                    )
                    Text(
                        text = phone,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        color = getDarkModeTextColor(
                            this@MainActivity
                        )
                    )
                }
            }
        }
        else{
            Row(
                modifier = Modifier
                    .width(width)
                    .height(80.dp)
                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
                    .background(
                        color = getDarkModeBackgroundColor(
                            this@MainActivity,
                            1
                        ), shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 2.dp,
                        color = if (isXuanzhong) {
                            Color.Blue
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        // 启动活动
                        if (app.packageName == "cn.tw.sar.easylauncher") {
                            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                            startActivity(intent)
                        } else {
                            val launchIntent =
                                packageManager.getLaunchIntentForPackage(app.packageName)
                            startActivity(launchIntent)
                        }

                    },
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
                        this@MainActivity
                    )
                )



            }
        }

    }

    var showMoreApps = mutableStateOf(false)

    var page = mutableIntStateOf(0)
    var currAppNumber = mutableIntStateOf(0)
    var callPhoneMode = mutableStateOf(false)
    var callPhoneText = mutableStateOf("")
    var contracts = mutableListOf<Contract>()
    var isDown = mutableStateOf(false)
    var maxPage = mutableIntStateOf(0)
    var weatherStr = mutableStateOf("")

    fun getWeatherByXinZhi(city: String, apiKey: String) {
        // 如果 city可以被分割, 则是经纬度
        if (city.contains("-")) {
            weatherStr.value = "请重新设置地址"
            return
        }
        var retrofit =  Retrofit.Builder()
            //设置网络请求BaseUrl地址
            .baseUrl("https://api.seniverse.com/")
            //设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        //创建网络请求接口实例


        val api: WeatherApi = retrofit.create(WeatherApi::class.java)

        val dataCall: Call<WeatherAPIBean> = api.getWeatherByMoney(apiKey, city,"zh-Hans","c")
        thread {
            try {
                val data: Response<WeatherAPIBean> = dataCall.execute()
                val result = data.body()
                if (result != null) {

                    runOnUiThread {
                        // 将List转换为Set
                        weatherStr.value = result.results[0].now.text + " " + result.results[0].now.temperature + "℃"

                    }
                }
            }catch (e: Exception) {
                Log.d("MainActivity", "getWeather: ${e.message}")
                weatherStr.value = "接口返回异常"

            }

        }
    }

    fun getWeatherByOpenWeatherMap(city: String, apiKey: String) {
        var lat = 0.0
        var lon = 0.0
        try {
            lat = city.split("-")[0].toDouble()
            lon = city.split("-")[1].toDouble()

        }catch (e: Exception) {
            Log.d("MainActivity", "getWeather: ${e.message}")
            weatherStr.value = "请重新设置地址"
            return
        }

        var retrofit =  Retrofit.Builder()
            //设置网络请求BaseUrl地址
            .baseUrl("https://api.openweathermap.org/")
            //设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        //创建网络请求接口实例

        val api: OpenWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)



        val dataCall: Call<OpenWeatherBean> = api.getWeather(
            lat,lon,apiKey,"zh_cn","metric"
        )
        thread {
            try {
                val data: Response<OpenWeatherBean> = dataCall.execute()
                val result = data.body()
                if (result != null) {

                    runOnUiThread {
                        // 将List转换为Set
                        weatherStr.value = result.weather[0].description + " " + ceil(result.main.temp).toInt() + "℃"

                    }
                }
            }catch (e: Exception) {
                Log.d("MainActivity", "getWeather: ${e.message}")
                weatherStr.value = "接口返回异常"

            }

        }
    }
    fun getWeather(city: String) {
        val sp = getSharedPreferences("settings", MODE_PRIVATE)
        val apiName = sp.getString("api", "心知天气")
        val apiKey = sp.getString(apiName, "")
        if (apiKey == "") {
            weatherStr.value = "未设置天气API"
            return
        }
        if (apiName == "心知天气") {
            getWeatherByXinZhi(city, apiKey!!)
        }else if (apiName == "OpenWeatherMap") {

            getWeatherByOpenWeatherMap(city, apiKey!!)
        }




    }
    override fun onResume() {
        super.onResume()
        // 如果是其他页面返回到该页面的
        allList = getAllInstallApps(this@MainActivity)
        // 重新获取应用
        appList = getInstalledApps(this@MainActivity)

        quickContract = ContractUtils.readQuickContract(this@MainActivity)
        updateTime()
        updateDate()
        var settings =  getSharedPreferences("settings", MODE_PRIVATE)
        var showMore = settings.getBoolean("1", false)
        if (showMore) {
            appList.plus(
                DesktopIcon(
                    type = 99,
                    packageName = "more",
                    showDesktop = true,
                    showDock = false
                )
            )
        }

        var sp = getSharedPreferences("weather", MODE_PRIVATE)
        var city = sp.getString("city", "")
        if (city != "") {
            haveLocation.value = true
            thread {
                getWeather(city!!)
                // 20分钟更新一次
                Thread.sleep(20*60*1000)
            }

        }else{
            haveLocation.value = false
        }


        Log.d("showDesktop", "appList.size: ${appList.size}")
    }

    var pageSize = 0f
    var pageCount = 0

    var timeStr = mutableStateOf("")
    var rlStr = mutableStateOf("")
    var nlStr = mutableStateOf("")
    var yi = mutableStateOf("")
    var ji = mutableStateOf("")
    var nlYear = mutableStateOf("")

    var haveLocation = mutableStateOf(false)
    var city = mutableStateOf("")
    var weather = mutableStateOf("")



    fun updateTime() {
        var sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        var isShowSecond = sharedPreferences.getBoolean("5", false)

        // 获取当前时间
        val time = System.currentTimeMillis()
        // 获取当前日历
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        // 获取当前时间
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        var houtStr = "${hour}"
        val minute = calendar.get(Calendar.MINUTE)
        var minuteStr = "${minute}"
        if (isShowSecond) {
            val second = calendar.get(Calendar.SECOND)
            var secondStr = "${second}"
            if (hour < 10) {
                houtStr = "0${hour}"
            }
            if (minute < 10) {
                minuteStr = "0"+minute
            }
            if (second < 10) {
                secondStr = "0"+second
            }
            timeStr.value = "${houtStr}:${minuteStr}:${secondStr}"
        }else{
            // 将时间拼成2位数
            if (hour < 10) {
                houtStr = "0${hour}"
            }
            if (minute < 10) {
                minuteStr = "0"+minute
            }
            timeStr.value = "${houtStr}:${minuteStr}"

        }



    }

    fun updateDate() {
        val calendar = Calendar.getInstance()

        // 获取当前日期
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val week = calendar.get(Calendar.DAY_OF_WEEK)
        val weekStrs = arrayOf("日", "一", "二", "三", "四", "五", "六")
        rlStr.value = "${year}年${month}月${day}日 星期${weekStrs[week-1]}"
        // 获取农历
        // val lunarUtils = cn.tw.sar.easylauncher.utils.LunarUtils(year, month, day)
        val lunarCalender = LunarCalender()
        var lunarCyclical = lunarCalender.cyclical(year, month, day)
        var launrAnimal = lunarCalender.animalsYear(year)

        var lunarString = lunarCalender.getLunarString(year, month, day);
        var fartival = lunarCalender.getFestival(year, month, day)
        var yijiStr = lunarCalender.getyiji(year, month, day).split("-")

        val lunar = "${lunarString} ${fartival}"

        yi.value = yijiStr[0].replace("宜:", "")
        ji.value = yijiStr[1].replace("忌:", "")
        nlStr.value = lunar
        nlYear.value = "农历${lunarCyclical}${launrAnimal}"
    }
    fun getPageAndPageSize(){

        if (showMoreApps.value) {
            maxPage.value = floor((allList.size / pageCount).toDouble()).toInt()+1
        }else{
            maxPage.value = floor((appList.size / pageCount).toDouble()).toInt()+1

        }
        Log.d("MainActivity", "appList.size: ${(appList.size / pageCount).toDouble()}")
        Log.d("MainActivity", "allList.size: ${(allList.size / pageCount).toDouble()}")
        Log.d("MainActivity", "allList: ${allList.toString()}")
        Log.d("MainActivity", "pageCount: ${pageCount}")
        Log.d("MainActivity", "maxPage.value: ${maxPage.value}")

    }
    @Composable
    fun Clock()
    {
        var sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        var isShowSecond = sharedPreferences.getBoolean("5", false)


        // 采用子线程更新时间
        var calendar = Calendar.getInstance()
        var minute = calendar.get(Calendar.MINUTE)

        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        updateTime()
        updateDate()
        var second = calendar.get(Calendar.SECOND)

        thread {
            // 每 1min 更新一次
            while (true) {
                if (isShowSecond) {
                    var newSecond = Calendar.getInstance().get(Calendar.SECOND)
                    if (newSecond != second) {
                        second = newSecond
                        runOnUiThread {
                            updateTime()
                            Log.d(">>>", "updateTime")
                        }
                    }
                }else{
                    var newminute = Calendar.getInstance().get(Calendar.MINUTE)
                    if (newminute != minute) {
                        minute = newminute
                        runOnUiThread {
                            updateTime()
                            Log.d(">>>", "updateTime")
                        }
                    }
                }


                var newYear = Calendar.getInstance().get(Calendar.YEAR)
                var newMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                var newDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                if (newYear != year || newMonth != month || newDay != day) {
                    year = newYear
                    month = newMonth
                    day = newDay
                    runOnUiThread {
                        updateDate()
                        Log.d(">>>", "updateDate")
                    }
                }
                if (isShowSecond) {
                    Thread.sleep(1000)
                }else{
                    Thread.sleep(5000)
                }
            }
        }


    }

    @OptIn(ExperimentalFoundationApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        allList = getAllInstallApps(this@MainActivity)
        // 重新获取应用
        appList = getInstalledApps(this@MainActivity)

        quickContract = ContractUtils.readQuickContract(this@MainActivity)

        // 获取屏幕宽度 dp
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density


        val height = displayMetrics.heightPixels
        val dpHeight = height / displayMetrics.density

        val subHight = dpHeight - dpWidth - 100
        val conHight = dpHeight - dpWidth

        pageSize = 2 * subHight / (80)

        // 取整
        pageCount = floor(pageSize).toInt()
        if (pageCount % 2 != 0) {
            pageCount -= 1
        }

        getPageAndPageSize()

        setContent {
            val scrollerLocal = rememberScrollState()
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                    Scaffold(
                modifier = Modifier,
                        // 设置页面右划手势
                    ) {
                        Column(

                        ) {
                            AnimatedVisibility(visible = (!callPhoneMode.value)) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(
                                            (conHight).dp
                                        )
                                        .background(
                                            color = getDarkModeBackgroundColor(
                                                this@MainActivity,
                                                0
                                            )

                                        )
                                    ,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    if (page.value == 0) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    subHight.dp
                                                ),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp)
                                                    .background(
                                                        color = getDarkModeBackgroundColor(
                                                            this@MainActivity,
                                                            1
                                                        ), shape = MaterialTheme.shapes.medium
                                                    )
                                                    .padding(10.dp),

                                                verticalArrangement = Arrangement.SpaceBetween,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        ,

                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    // 显示时钟，日期
                                                    // 每 1min 更新一次
                                                    // 记录上次更新时间
                                                    Clock()
                                                    // 显示天气
                                                    Text(
                                                        text = if (haveLocation.value) {
                                                            weatherStr.value
                                                        }else {
                                                            "未设置城市"
                                                        },
                                                        fontSize = 20.sp,
                                                        modifier = Modifier.clickable {
                                                            val intent = Intent(this@MainActivity, LocalSettingActivity::class.java)
                                                            startActivity(intent)
                                                        },
                                                        color = getDarkModeTextColor(
                                                            this@MainActivity
                                                        )
                                                    )
                                                    // 显示日期
                                                    Text(
                                                        text = rlStr.value,
                                                        fontSize = 25.sp,
                                                        color = getDarkModeTextColor(
                                                            this@MainActivity
                                                        )
                                                    )
                                                    // 显示农历
                                                    Text(
                                                        text = nlYear.value+nlStr.value,
                                                        fontSize = 16.sp,
                                                        color = getDarkModeTextColor(
                                                            this@MainActivity
                                                        )
                                                    )
                                                    // 显示时钟
                                                    Text(
                                                        text = timeStr.value,
                                                        fontSize = 60.sp,
                                                        color = getDarkModeTextColor(
                                                            this@MainActivity
                                                        )
                                                    )
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),

                                                        ){
// 显示宜忌
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth(
                                                                    fraction = 1f
                                                                )
                                                                .padding(2.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                text = "宜",
                                                                fontSize = 15.sp,
                                                                textAlign = TextAlign.Center,
                                                                color = getDarkModeTextColor(
                                                                    this@MainActivity
                                                                ),
                                                                modifier = Modifier
                                                                    .size(20.dp)
                                                                    .background(
                                                                        color = Color(0xFFff9800),
                                                                        shape = MaterialTheme.shapes.small
                                                                    )

                                                            )
                                                            Text(
                                                                text = yi.value,
                                                                fontSize = 15.sp,
                                                                color = getDarkModeTextColor(
                                                                    this@MainActivity
                                                                ),


                                                                )
                                                        }
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth(
                                                                    fraction = 1f
                                                                )
                                                                .padding(2.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                text = "忌",
                                                                fontSize = 15.sp,
                                                                textAlign = TextAlign.Center,
                                                                color = getDarkModeTextColor(
                                                                    this@MainActivity
                                                                ),
                                                                modifier = Modifier
                                                                    .size(20.dp)
                                                                    .background(
                                                                        color = Color(0xFF757575),
                                                                        shape = MaterialTheme.shapes.small
                                                                    )
                                                            )

                                                            Text(
                                                                text = ji.value,
                                                                fontSize = 15.sp,
                                                                color = getDarkModeTextColor(
                                                                    this@MainActivity
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(0.dp, 10.dp, 0.dp, 0.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "快捷联系人",
                                                        fontSize = 20.sp,
                                                        modifier = Modifier.clickable {
                                                            page.value = -1
                                                        },
                                                        color = getDarkModeTextColor(
                                                            this@MainActivity
                                                        )
                                                    )
                                                    Text(
                                                        text = "应用",
                                                        fontSize = 20.sp,
                                                        modifier = Modifier.clickable {
                                                            page.value = 1
                                                            currAppNumber.value = 0
                                                        },
                                                        color = getDarkModeTextColor(
                                                            this@MainActivity
                                                        )
                                                    )

                                                }

                                            }


                                        }
                                    } else if (page.value == -1) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    subHight.dp
                                                )
                                                .verticalScroll(
                                                    rememberScrollState()
                                                ),
                                        ) {
                                            for (i in 0 until quickContract.size step 2) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                ) {
                                                    if (i < quickContract.size) {
                                                        // 获取第i个应用
                                                        DesktopIcon(
                                                            app =  DesktopIcon(
                                                                type = 88,
                                                                packageName = quickContract.elementAt(i).name!!+"_"+quickContract.elementAt(i).phone!!,
                                                                showDesktop = true,
                                                                showDock = false
                                                            ),
                                                            width = (dpWidth / 2 ).dp,
                                                            isXuanzhong = false
                                                        )
                                                        if (i + 1 < quickContract.size) {
                                                            DesktopIcon(
                                                                app =  DesktopIcon(
                                                                    type = 88,
                                                                    packageName = quickContract.elementAt(i+1).name!!+"_"+quickContract.elementAt(i+1).phone!!,
                                                                    showDesktop = true,
                                                                    showDock = false
                                                                ),
                                                                width = (dpWidth / 2).dp,
                                                                isXuanzhong = false
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    subHight.dp
                                                )
                                        ) {
                                            // 显示所有应用图标，一行显示 2 个
                                            Log.d("MainActivity", "page.value: ${pageCount *(page.value-1)}~${pageCount *(page.value)}")
                                            for (i in pageCount *(page.value-1) until pageCount *(page.value) step 2) {

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                ) {
                                                    var list = if (showMoreApps.value) {
                                                        allList
                                                    }else{
                                                        appList
                                                    }
                                                    if (i < list.size) {
                                                        // 获取第i个应用
                                                        DesktopIcon(
                                                            app =  list.elementAt(i),
                                                            width = (dpWidth / 2 ).dp,
                                                            isXuanzhong = currAppNumber.value == i
                                                        )
                                                        if (i + 1 < list.size) {
                                                            DesktopIcon(
                                                                app = list.elementAt(i+1),
                                                                width = (dpWidth / 2).dp,
                                                                isXuanzhong = currAppNumber.value == i + 1
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }


                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(
                                                100.dp
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            var app = DesktopIcon(
                                                type = 99,
                                                packageName = "more",
                                                showDesktop = true,
                                                showDock = false
                                            )
                                            DesktopIcon(
                                                app =  app,
                                                width = (dpWidth / 2 ).dp,
                                                isXuanzhong = false
                                            )
                                            DesktopIcon(
                                                app = DesktopIcon(
                                                    type = 98,
                                                    packageName = "settings",
                                                    showDesktop = true,
                                                    showDock = false
                                                ),
                                                width = (dpWidth / 2).dp,
                                                isXuanzhong = false
                                            )

                                        }
                                        LineBar(
                                            maxPage = maxPage.value+2,
                                            page = page.value+2,
                                            onLeftEnd = {

                                                var maxIds = if (showMoreApps.value) {
                                                    allList.size
                                                }else{
                                                    appList.size
                                                }

                                                if (page.value == -1) {
                                                    Log.d("MainActivity", "maxPage.value: ${maxPage.value}")
                                                    Log.d("MainActivity", "page.value: ${page.value}")
                                                    // 跳转到最后一页
                                                    page.value = maxPage.value
                                                    currAppNumber.value = maxIds - 1
                                                } else {
                                                    page.value -= 1
                                                    Log.d("MainActivity", "maxPage.value: ${maxPage.value}")
                                                    Log.d("MainActivity", "page.value: ${page.value}")
                                                    if (page.value > 0) {
                                                        currAppNumber.value = (page.value-1)*pageCount
                                                    }
                                                }
                                            },
                                            onRightEnd = {
                                                if (page.value == maxPage.value) {
                                                    // 跳转到第一页
                                                    page.value = 0
                                                    currAppNumber.value = 0

                                                } else {
                                                    page.value += 1
                                                    if (page.value > 0) {
                                                        currAppNumber.value = (page.value-1)*pageCount
                                                    }
                                                }
                                            },
                                            onDotsClick = {
                                                page.value = it-2
                                            }
                                        )
                                    }



                                }
                            }



                            AnimatedVisibility(visible = callPhoneMode.value) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(
                                            conHight.dp
                                        )
                                        .background(
                                            color = getDarkModeBackgroundColor(
                                                this@MainActivity,
                                                0
                                            )

                                        )
                                    ,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(
                                                subHight.dp
                                            )/// 垂直滚动
                                            .verticalScroll(scrollerLocal),


                                        ) {
                                        // 显示垂直滑动的联系人
                                        for (contract in contracts) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .padding(8.dp)
                                                    .background(
                                                        color = getDarkModeBackgroundColor(
                                                            this@MainActivity,
                                                            1
                                                        ), shape = MaterialTheme.shapes.medium
                                                    )
                                                    .padding(8.dp)
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color.Transparent,
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .clickable {
                                                        val intent = Intent(Intent.ACTION_CALL)
                                                        intent.data =
                                                            android.net.Uri.parse("tel:${contract.phone}")
                                                        startActivity(intent)
                                                    },
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    if (contract.name != null) {
                                                        Text(text = contract.name!!, fontSize = 30.sp, color = getDarkModeTextColor(this@MainActivity))
                                                    }
                                                    Text(text = contract.phone, fontSize = 20.sp, color = getDarkModeTextColor(this@MainActivity))
                                                }
                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.End
                                                ){
                                                    Text(text = dataFormat(contract.lastCallTime!!.toLong()), fontSize = 15.sp, color = getDarkModeTextColor(this@MainActivity))
                                                    Row {
                                                        if (contract.lastCallType == 1) {
                                                            // 获取drawable资源
                                                            var drawable = resources.getDrawable(R.drawable.callin)
                                                            Icon(

                                                                painter = rememberDrawablePainter(drawable = drawable),
                                                                modifier = Modifier.size(20.dp),
                                                                contentDescription = ""
                                                            )
                                                            Text(text = "最近呼入 ${timeFormat(contract.lastCallDuration)}", fontSize = 15.sp, color = getDarkModeTextColor(this@MainActivity))
                                                        }else if (contract.lastCallType == 2) {
                                                            // 获取drawable资源
                                                            var drawable = resources.getDrawable(R.drawable.callout)
                                                            Icon(

                                                                painter = rememberDrawablePainter(drawable = drawable),
                                                                modifier = Modifier.size(20.dp),
                                                                contentDescription = ""
                                                            )
                                                            Text(text = "最近呼出 ${timeFormat(contract.lastCallDuration)}", fontSize = 15.sp, color = getDarkModeTextColor(this@MainActivity))
                                                        } else if (contract.lastCallType == 3) {
                                                            // 获取drawable资源
                                                            var drawable = resources.getDrawable(R.drawable.unjie)
                                                            Icon(

                                                                painter = rememberDrawablePainter(drawable = drawable),
                                                                modifier = Modifier.size(20.dp),
                                                                contentDescription = ""
                                                            )
                                                            Text(
                                                                text = "未接响铃${
                                                                    timeFormat(
                                                                        contract.lastCallDuration
                                                                    )
                                                                }",
                                                                fontSize = 15.sp,
                                                                color = getDarkModeTextColor(this@MainActivity)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }



                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(
                                                40.dp
                                            )
                                    ) {
                                        // 输入框，不调起键盘,显示文字下方横线
                                        Text(
                                            text = callPhoneText.value,
                                            fontSize = 30.sp,
                                            textAlign = TextAlign.Center,
                                            color = getDarkModeTextColor(
                                                this@MainActivity
                                            ),
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier.fillMaxSize()


                                        )




                                    }



                                }
                            }



                            KeyBoard(
                                page= page.value,
                                mode= callPhoneMode.value,
                                func = {
                                    var maxSize = if (showMoreApps.value) {
                                        allList.size
                                    }else{
                                        appList.size
                                    }
                                    Log.d("MainActivity", it)
                                    when (it) {
                                        "up" -> {
                                            if (!callPhoneMode.value){
                                                if (currAppNumber.value - 2 >= 0) {
                                                    currAppNumber.value -= 2

                                                    if (currAppNumber.value < (page.value - 1) * pageCount ) {
                                                        page.value -= 1
                                                    }
                                                }else{
                                                    currAppNumber.value = maxSize - 1
                                                    page.value = maxPage.value
                                                }
                                            }else {
                                                Log.d("MainActivity", "scrollerLocal.value: ${scrollerLocal.value}")
                                                isDown.value = false
                                            }

                                        }
                                        "down" -> {
                                            if (!callPhoneMode.value){
                                                if (currAppNumber.value + 2 < maxSize) {
                                                    currAppNumber.value += 2

                                                    if (currAppNumber.value >= page.value * pageCount ) {
                                                        page.value += 1
                                                    }
                                                }else{
                                                    currAppNumber.value = 0
                                                    page.value = 1
                                                }
                                            }else{
                                                isDown.value = true
                                            }

                                        }
                                        "left" -> {
                                            if (!callPhoneMode.value){
                                                if (currAppNumber.value - 1 >= 0) {
                                                    currAppNumber.value -= 1

                                                    if (currAppNumber.value < (page.value - 1) * pageCount ) {
                                                        page.value -= 1
                                                    }
                                                }else{
                                                    currAppNumber.value = maxSize - 1
                                                    page.value = maxPage.value
                                                }
                                            }

                                        }
                                        "right" -> {
                                            if (!callPhoneMode.value){
                                                if (currAppNumber.value + 1 < maxSize) {
                                                    currAppNumber.value += 1

                                                    if (currAppNumber.value >= page.value * pageCount ) {
                                                        page.value += 1
                                                    }
                                                }else{
                                                    currAppNumber.value = 0
                                                    page.value = 1
                                                }
                                            }

                                        }
                                        "open" -> {
                                            if (!callPhoneMode.value) {
                                                if (page.value==0){
                                                    page.value = -1
                                                }else{
                                                    val launchIntent =
                                                        packageManager.getLaunchIntentForPackage(
                                                            appList.elementAt(currAppNumber.value).packageName
                                                        )
                                                    startActivity(launchIntent)
                                                }

                                            }
                                        }
                                        "home" -> {
                                            page.value = 0
                                        }
                                        "back" -> {
                                            if (callPhoneMode.value){
                                                callPhoneText.value =
                                                    callPhoneText.value.substring(
                                                        0,
                                                        callPhoneText.value.length - 1
                                                    )
                                                if (callPhoneText.value == "") {
                                                    callPhoneMode.value = false
                                                }
                                            }else{
                                                if (page.value==0){
                                                    page.value = 1
                                                }
                                            }

                                        }
                                        "call" -> {
                                            // 拨打电话
                                            if (callPhoneMode.value) {
                                                val intent = Intent(Intent.ACTION_CALL)
                                                intent.data =
                                                    android.net.Uri.parse("tel:${callPhoneText.value}")
                                                startActivity(intent)
                                                callPhoneMode.value = false
                                                callPhoneText.value = ""
                                            }else{
                                                page.value = -1

                                            }


                                        }
                                        "close" -> {
                                            if (callPhoneMode.value){
                                                callPhoneMode.value = false
                                                callPhoneText.value = ""
                                            }
                                        }
                                        // 其他的按键
                                        else -> {
                                            if (callPhoneText.value==""){
                                                callPhoneMode.value = true

                                            }
                                            if (callPhoneMode.value) {
                                                callPhoneText.value += it
                                                //  如果不是 *\# 开头
                                                if (callPhoneText.value.length > 1) {
                                                    if (callPhoneText.value[0] != '*' && callPhoneText.value[0] != '#' ) {
                                                        contracts = ContractUtils.getLogStartWithStr(
                                                                callPhoneText.value,
                                                                this@MainActivity
                                                            )


                                                    }
                                                }

                                            }
                                        }

                                    }
                                    println(it)
                                },
                                width = dpWidth,
                                height = dpWidth,
                                backgroundColor = getDarkModeBackgroundColor(
                                    this@MainActivity,
                                    0
                                ),
                                buttonColor = getDarkModeBackgroundColor(
                                    this@MainActivity,
                                    1
                                ),
                                fontColor = getDarkModeTextColor(
                                    this@MainActivity
                                )
                            )
                        }



                    }
            }
        }
    }
}



