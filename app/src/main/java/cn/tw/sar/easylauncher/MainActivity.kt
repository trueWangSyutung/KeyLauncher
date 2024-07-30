package cn.tw.sar.easylauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.telephony.CellInfo
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.easylauncher.activities.LocalSettingActivity
import cn.tw.sar.easylauncher.beam.DesktopIcon
import cn.tw.sar.easylauncher.database.QuickContractsDatabase
import cn.tw.sar.easylauncher.entity.CallHistory
import cn.tw.sar.easylauncher.entity.QuickContracts
import cn.tw.sar.easylauncher.utils.LocalCalenderUtils
import cn.tw.sar.easylauncher.utils.LunarCalender
import cn.tw.sar.easylauncher.utils.dataFormat
import cn.tw.sar.easylauncher.utils.getAllInstallApps
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import cn.tw.sar.easylauncher.utils.getInstalledApps
import cn.tw.sar.easylauncher.utils.requestPhonePermission
import cn.tw.sar.easylauncher.weight.KeyBoard
import cn.tw.sar.easylauncher.weight.LineBar
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.qweather.sdk.bean.base.Unit
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.HeConfig
import com.qweather.sdk.view.QWeather
import net.time4j.android.ApplicationStarter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.math.round


class MainActivity : ComponentActivity() {
    var appList = mutableStateListOf<DesktopIcon> ()
    var allList = mutableStateListOf<DesktopIcon> ()
    var quickContract  = mutableStateListOf<QuickContracts>()
    lateinit var locationManager: LocationManager
    var haveNetWorkPermission = mutableStateOf(false)






    var showMoreApps = mutableStateOf(false)

    var page = mutableIntStateOf(0)
    var currAppNumber = mutableIntStateOf(0)
    var callPhoneMode = mutableStateOf(false)
    var callPhoneText = mutableStateOf("")
    var contracts = mutableStateListOf<CallHistory>()
    var isDown = mutableStateOf(false)
    var maxPage = mutableIntStateOf(0)
    var weatherStr = mutableStateOf("")


    fun getWeather(city: String) {
        QWeather.getWeatherNow(
            this@MainActivity,
            city,
            LocalCalenderUtils.getWeatherLang(this@MainActivity),
            Unit.METRIC,
            object : QWeather.OnResultWeatherNowListener {
                override fun onError(p0: Throwable?) {
                    Log.d("weather", "error"+p0.toString())

                }

                override fun onSuccess(p0: WeatherNowBean?) {
                    if (p0 != null) {
                        runOnUiThread {
                            Log.d("weather", p0.now.text + "  " + floor(p0.now.temp.toDouble()).toInt().toString() + "℃")
                            weatherStr.value = p0.now.text + "  " + floor(p0.now.temp.toDouble()).toInt().toString() + "℃"
                        }
                    }
                }

            });







    }
    fun checkPermission() : Boolean {
        var permissions = arrayOf(
            android.Manifest.permission.QUERY_ALL_PACKAGES,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.INTERNET,

        )
        var hasPermission = true
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {

                hasPermission = false
                return hasPermission
            }
        }
        return hasPermission

    }
    fun initWithStartApp(){
        // Init With Start App
        isChinese.value = LocalCalenderUtils.isChineseNation(this@MainActivity)

        updateTime()
        updateDate()
        allList.clear()
        allList.addAll(getAllInstallApps(this@MainActivity))
        // 重新获取应用
        appList.clear()
        appList.addAll(getInstalledApps(this@MainActivity))
        // quickContract = ContractUtils.readQuickContract(this@MainActivity)

        var settings =  getSharedPreferences("settings", MODE_PRIVATE)
        var showMore = settings.getBoolean("1", false)
        if (showMore) {
            appList.add(
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
        Log.d("weather", "city: $city")
        if (city != "") {
            haveLocation.value = true
            thread {
                getWeather(city!!)
                // 20分钟更新一次
                Thread.sleep(30*60*1000)
            }

        }else{
            haveLocation.value = false
        }


        Log.d("showDesktop", "appList.size: ${appList.size}")

        keySound.value = settings.getBoolean("10", false)
        if (keySound.value) {
            tts = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.CHINA)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(
                            this@MainActivity,
                            "Language not supported",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // 设置进度监听器（可选）
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String) {}
                            override fun onDone(utteranceId: String) {
                                // 文本转换语音完成
                            }

                            override fun onError(utteranceId: String) {}
                        })
                    }
                }
            }
        }

        // 获取屏幕宽度 dp
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val keyHight = dpWidth*5/4

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

        val database: QuickContractsDatabase =
            QuickContractsDatabase.getDatabase(this@MainActivity)

        val quickContractsDao = database.quickContractsDao()
        thread {
            quickContract.clear()
            quickContract.addAll(
                quickContractsDao.getLimit(pageSize.toInt())
            )
        }
        // 如果是其他页面返回到该页面的
    }
    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("init", MODE_PRIVATE)
        val isFirst = sharedPreferences.getBoolean("isFirst", true)
        if (isFirst) {
            val intent = Intent(this@MainActivity, InitActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            initWithStartApp()
        }





    }
    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }
    fun speakText(text: String?) {
        // 如果未初始化
        if (tts == null) {
            Toast.makeText(this, "TextToSpeech is not initialized", Toast.LENGTH_SHORT).show()
            return
        }
        if (tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
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

    var isChinese = mutableStateOf(false)



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
        var weekStrs = arrayOf(
            resources.getString(R.string.sunday),
            resources.getString(R.string.monday),
            resources.getString(R.string.tuesday),
            resources.getString(R.string.wednesday),
            resources.getString(R.string.thursday),
            resources.getString(R.string.friday),
            resources.getString(R.string.saturday)
        )

        rlStr.value = "${year}/${month}/" +
                "${day}, ${weekStrs[week-1]}"

        // 获取农历
        // val lunarUtils = cn.tw.sar.easylauncher.utils.LunarUtils(year, month, day)
        val lunarCalender = LunarCalender()
        var lunarCyclical = lunarCalender.cyclical(year, month, day)
        var launrAnimal = lunarCalender.animalsYear(year)

        var lunarString = lunarCalender.getLunarString(year, month, day);
        var fartival = lunarCalender.getFestival(year, month, day)

        var yijiStr = lunarCalender.getyiji(year, month, day).split("-")
        // 如該沒有分割成功
        if (yijiStr.size != 2) {
            yijiStr = lunarCalender.getyiji(year, month, day).split(" ")
        }

        val lunar = "${lunarString} ${fartival}"

        yi.value = yijiStr[0].replace("宜:", "")
        ji.value = yijiStr[1].replace("忌:", "")
        nlStr.value = lunar
        nlYear.value = LocalCalenderUtils.getLocalCalender(
            this,year, month, day
        )
    }
    fun getPageAndPageSize(){

        if (showMoreApps.value) {
            maxPage.value = 1
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
                        }
                    }
                }else{
                    var newminute = Calendar.getInstance().get(Calendar.MINUTE)
                    if (newminute != minute) {
                        minute = newminute
                        runOnUiThread {
                            updateTime()
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
    var keySound = mutableStateOf(false)
    private  var tts: TextToSpeech? = null

    val isTonghua = mutableStateOf(false)
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: TelephonyCallback
    @RequiresApi(api = Build.VERSION_CODES.S)
    private class MyCallStateListener : TelephonyCallback(),
        TelephonyCallback.CallStateListener,TelephonyCallback.CellInfoListener
    {
        override fun onCallStateChanged(state: Int) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    //
                    Log.d("MainActivity", "Call:No")
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    // 获取 当前电话
                    Log.d("MainActivity", "Call:Coming")
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d("MainActivity", "Call:Jiedianhua")
                }
            }
        }





        override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>) {
            Log.d("MainActivity", "onCellInfoChanged: ${cellInfo.toString()}")
        }


    }

    private class MyPhoneStateListener : PhoneStateListener() {
        @Deprecated("Deprecated in Java")
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d("MainActivity", "Call:No" + phoneNumber)
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d("MainActivity", "Call:Coming " + phoneNumber)
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d("MainActivity", "Call：Jiedianhua" + phoneNumber)
                }
            }
            super.onCallStateChanged(state, phoneNumber)
        }
    }


    fun initTele(){
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = @RequiresApi(Build.VERSION_CODES.S)
        object : TelephonyCallback() {

        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //android12以上

            val listener =  MyCallStateListener();
            telephonyManager.registerTelephonyCallback(this@MainActivity.mainExecutor, listener);
            Log.d("MainActivity", "MyPhoneStateListener")

            //val listener =  MyPhoneStateListener();
            //telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }else{
            val listener =  MyPhoneStateListener();
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }



    }
    var subList =  mutableStateListOf<DesktopIcon>()
    var biliList  = mutableStateListOf<Float>()
    var alphaTable = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z"
    )
    var currPackageName = mutableStateOf("")
    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalFoundationApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "basedPermission: ${checkPermission()}")
        HeConfig.init("HE2407101759391793", "a90fdf38ff7c4b199a5ca021285b7529");
        HeConfig.switchToDevService();
        // 和风天气 API 初始化

        ApplicationStarter.initialize(this, true);
        // with prefetch on background thread



        isChinese.value = LocalCalenderUtils.isChineseNation(this@MainActivity)



        // 获取屏幕宽度 dp
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val keyHight = dpWidth*5/4

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
        // initTele()


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
                                                    .fillMaxHeight(0.9f)
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
                                                        resources.getString(R.string.no_address)
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
                                                    text = if (isChinese.value){
                                                        nlYear.value+nlStr.value
                                                    }else{
                                                        nlYear.value
                                                    },
                                                    fontSize = 14.sp,
                                                    textAlign = TextAlign.Center,
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
                                                AnimatedVisibility(visible = isChinese.value) {
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
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight()
                                                    .padding(0.dp, 10.dp, 0.dp, 0.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = resources.getString(R.string.quick_contacts),
                                                    fontSize = 20.sp,
                                                    modifier = Modifier.clickable {
                                                        page.value = -1
                                                    },
                                                    color = getDarkModeTextColor(
                                                        this@MainActivity
                                                    )
                                                )
                                                Text(
                                                    text =resources.getString(R.string.app_hidden),
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
                                }
                                else if (page.value == -1) {
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
                                }
                                else if (page.value == -2) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(
                                                subHight.dp
                                            )/// 垂直滚动


                                        ) {
                                        // 显示垂直滑动的联系人

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    (subHight - 60).dp
                                                )/// 垂直滚动
                                                .verticalScroll(scrollerLocal),
                                        ) {
                                            for (contract in contracts) {
                                                var checked = remember { mutableStateOf(false) }
                                                var right_checked = remember { mutableStateOf(false) }
                                                var drop = remember { mutableStateOf(false) }
                                                var dragOffset =  remember { mutableStateOf(0f) }
                                                Row(
                                                    modifier = Modifier
                                                        .pointerInput(Unit) {
                                                            detectTapGestures(
                                                                onTap = {
                                                                    val intent =
                                                                        Intent(Intent.ACTION_CALL)
                                                                    intent.data =
                                                                        android.net.Uri.parse("tel:${contract.phone}")
                                                                    thread {
                                                                        val database: QuickContractsDatabase =
                                                                            QuickContractsDatabase.getDatabase(
                                                                                this@MainActivity
                                                                            )
                                                                        val callHistoryDao =
                                                                            database.callHistoryDao()
                                                                        val quickContractsDao =
                                                                            database.quickContractsDao()

                                                                        val quickContracts =
                                                                            quickContractsDao.getByPhone(
                                                                                contract.phone
                                                                            )
                                                                        if (quickContracts != null) {
                                                                            callHistoryDao.insert(
                                                                                CallHistory(
                                                                                    id = 0,
                                                                                    name = quickContracts.name,
                                                                                    phone = contract.phone,
                                                                                    type = 2,
                                                                                    time = Date(
                                                                                        System.currentTimeMillis()
                                                                                    ),
                                                                                    isDelete = false
                                                                                )
                                                                            )
                                                                        } else {
                                                                            callHistoryDao.insert(
                                                                                CallHistory(
                                                                                    id = 0,
                                                                                    name = "",
                                                                                    phone = contract.phone,
                                                                                    type = 2,
                                                                                    time = Date(
                                                                                        System.currentTimeMillis()
                                                                                    ),
                                                                                    isDelete = false
                                                                                )
                                                                            )
                                                                        }


                                                                    }
                                                                    startActivity(intent)
                                                                },
                                                                onLongPress = {
                                                                    checked.value = !checked.value

                                                                }
                                                            )

                                                        }
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
                                                    ,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {

                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        if (contract.name != "") {
                                                            Text(text = contract.name, fontSize = 20.sp, color = getDarkModeTextColor(this@MainActivity))
                                                        }
                                                        Text(text = contract.phone, fontSize = 12.sp, color = getDarkModeTextColor(this@MainActivity))
                                                    }
                                                    AnimatedVisibility(visible = checked.value) {
                                                        TextButton(onClick = {
                                                            val database: QuickContractsDatabase =
                                                                QuickContractsDatabase.getDatabase(this@MainActivity)
                                                            val quickContractsDao = database.quickContractsDao()
                                                            thread {
                                                                val num = quickContractsDao.getCountByPhone(contract.phone)
                                                                if (num == 0) {
                                                                    if (contract.name != "") {
                                                                        quickContractsDao.insert(QuickContracts(phone = contract.phone,
                                                                            name = contract.name!!
                                                                        ))
                                                                    }else{
                                                                        quickContractsDao.insert(QuickContracts(phone = contract.phone,
                                                                            name = "未知电话"))
                                                                    }
                                                                } else {
                                                                    runOnUiThread {
                                                                        Toast.makeText(this@MainActivity, "已存在", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }

                                                            }
                                                        }) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Center,

                                                                ) {
                                                                Icon(
                                                                    imageVector = Icons.Filled.Star,
                                                                    contentDescription = "delete",
                                                                    modifier = Modifier.size(30.dp),
                                                                    tint = getDarkModeTextColor(this@MainActivity)
                                                                )
                                                                Text(text = resources.getString(R.string.select), fontSize = 12.sp, color = getDarkModeTextColor(this@MainActivity))
                                                            }
                                                        }
                                                    }
                                                    AnimatedVisibility(visible = !checked.value) {
                                                        Column(
                                                            verticalArrangement = Arrangement.Center,
                                                            horizontalAlignment = Alignment.End
                                                        ){
                                                            Text(text = dataFormat(contract.time!!.time.toLong()), fontSize = 15.sp, color = getDarkModeTextColor(this@MainActivity))
                                                            Row {
                                                                if (contract.type == 1) {
                                                                    // 获取drawable资源
                                                                    var drawable = resources.getDrawable(R.drawable.callin)
                                                                    Icon(

                                                                        painter = rememberDrawablePainter(drawable = drawable),
                                                                        modifier = Modifier.size(20.dp),
                                                                        contentDescription = ""
                                                                    )
                                                                    //  ${timeFormat(contract.lastCallDuration)}
                                                                    Text(text = "最近呼入", fontSize = 15.sp, color = getDarkModeTextColor(this@MainActivity))
                                                                }else if (contract.type == 2) {
                                                                    // 获取drawable资源
                                                                    var drawable = resources.getDrawable(R.drawable.callout)
                                                                    Icon(

                                                                        painter = rememberDrawablePainter(drawable = drawable),
                                                                        modifier = Modifier.size(20.dp),
                                                                        contentDescription = ""
                                                                    )
                                                                    Text(text = "最近呼出", fontSize = 15.sp, color = getDarkModeTextColor(this@MainActivity))
                                                                } else if (contract.type == 3) {
                                                                    // 获取drawable资源
                                                                    var drawable = resources.getDrawable(R.drawable.unjie)
                                                                    Icon(

                                                                        painter = rememberDrawablePainter(drawable = drawable),
                                                                        modifier = Modifier.size(20.dp),
                                                                        contentDescription = ""
                                                                    )
                                                                    Text(
                                                                        // ${
                                                                        //                                                                        timeFormat(
                                                                        //                                                                            contract.lastCallDuration
                                                                        //                                                                        )
                                                                        //                                                                    }
                                                                        text = "未接响铃",
                                                                        fontSize = 15.sp,
                                                                        color = getDarkModeTextColor(this@MainActivity)
                                                                    )
                                                                }
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
                                                    (60).dp
                                                ),/// 垂直滚动
                                        ) {
                                            Text(
                                                text = callPhoneText.value,
                                                fontSize = 25.sp,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        top = 5.dp,
                                                        bottom = 5.dp
                                                    ),
                                                textAlign = TextAlign.Center,
                                                color = getDarkModeTextColor(this@MainActivity),
                                            )
                                        }
                                    }


                                }
                                else {
                                    var dragDistance = remember {
                                        mutableStateOf(0F)
                                    }
                                    val draggableState = rememberDraggableState(onDelta = {
                                        dragDistance.value += it
                                    })


                                        Column(
                                            modifier = if (showMoreApps.value) {
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(
                                                        subHight.dp
                                                    )



                                            } else {
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(
                                                        subHight.dp
                                                    )
                                            }
                                        ) {
                                            // 显示所有应用图标，一行显示 2 个
                                            Log.d("MainActivity", "page.value: ${pageCount *(page.value-1)}~${pageCount *(page.value)}")

                                            if (showMoreApps.value) {
                                                // 从 list 获取 前五个 元素
                                                // 下标 为 currAppNumber.value - 2 ~ currAppNumber.value + 3
                                                // currAppNumber.value - 2 < 0 , 取 currAppNumber.value - 2 + list.size
                                                // currAppNumber.value + 2 > list.size, 取 (currAppNumber.value + 2) % list.size
                                                // 获取前两个
                                                // 获取 currAppNumber.value - 2 ~ currAppNumber.value + 3 的字数组
                                                Row(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth(1f)
                                                            .fillMaxHeight(),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.Start,
                                                    ){
                                                        for (i in 0 until  subList.size) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth(),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                Column(
                                                                    modifier = Modifier.fillMaxWidth(
                                                                        biliList[i]
                                                                    ),
                                                                ) {

                                                                }
                                                                DesktopIcon(
                                                                    app =   subList.elementAt(i),
                                                                    width = (dpWidth / 2 ).dp,
                                                                    isXuanzhong = currPackageName.value == subList.elementAt(i).packageName,
                                                                    small =  currPackageName.value != subList.elementAt(i).packageName,

                                                                    )
                                                            }

                                                        }
                                                    }

                                                }



                                            }else{
                                                for (i in pageCount *(page.value-1) until pageCount *(page.value) step 2) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                    ) {

                                                        if (i < appList.size) {
                                                            // 获取第i个应用
                                                            DesktopIcon(
                                                                app =  appList.elementAt(i),
                                                                width = (dpWidth / 2 ).dp,
                                                                isXuanzhong = currAppNumber.value == i
                                                            )
                                                            if (i + 1 < appList.size) {
                                                                DesktopIcon(
                                                                    app = appList.elementAt(i+1),
                                                                    width = (dpWidth / 2).dp,
                                                                    isXuanzhong = currAppNumber.value == i + 1
                                                                )
                                                            }
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
                                    AnimatedVisibility(visible = !callPhoneMode.value) {
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
                                                }
                                                else if (page.value == -2) {
                                                    // 跳转到最后一页
                                                    page.value = maxPage.value
                                                    currAppNumber.value = maxIds - 1
                                                }
                                                else {
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




                            KeyBoard(
                                page= page.value,
                                mode= callPhoneMode.value,
                                func = {
                                    var maxSize = if (showMoreApps.value) {
                                        allList.size
                                    }else{
                                        appList.size
                                    }

                                    Log.d("MainActivity", maxSize.toString())
                                    when (it) {
                                        "up" -> {

                                            if (!callPhoneMode.value){
                                                if (showMoreApps.value){
                                                    if (currAppNumber.value - 1 >= 0) {
                                                        currAppNumber.value -= 1
                                                        if (currAppNumber.value < (page.value - 1) * pageCount ) {
                                                            page.value -= 1
                                                        }
                                                    }else{
                                                        currAppNumber.value = maxSize - 1
                                                    }
                                                    currPackageName.value = allList[currAppNumber.value].packageName
                                                    loadDesktop()

                                                }else{
                                                    if (currAppNumber.value - 2 >= 0) {
                                                        currAppNumber.value -= 2
                                                        if (currAppNumber.value < (page.value - 1) * pageCount ) {
                                                            page.value -= 1
                                                        }
                                                    }else{
                                                        currAppNumber.value = maxSize - 1
                                                        page.value = maxPage.value
                                                    }
                                                }

                                                if (keySound.value){
                                                    // 获取appName
                                                    var list = if (showMoreApps.value) {
                                                        allList
                                                    }else{
                                                        appList
                                                    }
                                                    var appManager = packageManager
                                                    var appName = appManager.getApplicationLabel(
                                                        appManager.getApplicationInfo(
                                                            list.elementAt(currAppNumber.value).packageName,
                                                            PackageManager.GET_META_DATA
                                                        )
                                                    )
                                                    speakText(appName.toString())
                                                }

                                            }else {
                                                Log.d("MainActivity", "scrollerLocal.value: ${scrollerLocal.value}")
                                                isDown.value = false
                                            }

                                        }
                                        "down" -> {

                                            if (!callPhoneMode.value){
                                                if (showMoreApps.value){
                                                    if (currAppNumber.value + 1 < maxSize) {
                                                        currAppNumber.value += 1
                                                    }else{
                                                        currAppNumber.value = 0
                                                    }
                                                    currPackageName.value = allList[currAppNumber.value].packageName
                                                    loadDesktop()
                                                }else{
                                                    if (currAppNumber.value + 2 < maxSize) {
                                                        currAppNumber.value += 2

                                                        if (currAppNumber.value >= page.value * pageCount ) {
                                                            page.value += 1
                                                        }
                                                    }else{
                                                        currAppNumber.value = 0
                                                        page.value = 1
                                                    }
                                                }


                                                if (keySound.value){
                                                    // 获取appName
                                                    var list = if (showMoreApps.value) {
                                                        allList
                                                    }else{
                                                        appList
                                                    }

                                                    var appManager = packageManager
                                                    var appName = appManager.getApplicationLabel(
                                                        appManager.getApplicationInfo(
                                                            list.elementAt(currAppNumber.value).packageName,
                                                            PackageManager.GET_META_DATA
                                                        )
                                                    )
                                                    speakText(appName.toString())
                                                }


                                            }else{
                                                isDown.value = true
                                            }

                                        }
                                        "left" -> {

                                            if (!callPhoneMode.value){
                                                if (showMoreApps.value){
                                                    if (currAppNumber.value - 1 >= 0) {
                                                        currAppNumber.value -= 1
                                                        if (currAppNumber.value < (page.value - 1) * pageCount ) {
                                                            page.value -= 1
                                                        }
                                                    }else{
                                                        currAppNumber.value = maxSize - 1
                                                    }
                                                    currPackageName.value = allList[currAppNumber.value].packageName
                                                    loadDesktop()
                                                } else{
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


                                                if (keySound.value){
                                                    // 获取appName
                                                    var list = if (showMoreApps.value) {
                                                        allList
                                                    }else{
                                                        appList
                                                    }
                                                    var appManager = packageManager
                                                    var appName = appManager.getApplicationLabel(
                                                        appManager.getApplicationInfo(
                                                            list.elementAt(currAppNumber.value).packageName,
                                                            PackageManager.GET_META_DATA
                                                        )
                                                    )
                                                    speakText(appName.toString())
                                                }
                                            }

                                        }
                                        "right" -> {

                                            if (!callPhoneMode.value){
                                                if (showMoreApps.value){
                                                    if (currAppNumber.value + 1 < maxSize) {
                                                        currAppNumber.value += 1
                                                        if (currAppNumber.value >= page.value * pageCount ) {
                                                            page.value += 1
                                                        }
                                                    }else{
                                                        currAppNumber.value = 0
                                                        page.value = 1
                                                    }
                                                    currPackageName.value = allList[currAppNumber.value].packageName
                                                    loadDesktop()
                                                }else{
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

                                                if (keySound.value){
                                                    // 获取appName
                                                    var list = if (showMoreApps.value) {
                                                        allList
                                                    }else{
                                                        appList
                                                    }
                                                    var appManager = packageManager
                                                    var appName = appManager.getApplicationLabel(
                                                        appManager.getApplicationInfo(
                                                            list.elementAt(currAppNumber.value).packageName,
                                                            PackageManager.GET_META_DATA
                                                        )
                                                    )
                                                    speakText(appName.toString())
                                                }

                                            }

                                        }
                                        "open" -> {

                                            if (!callPhoneMode.value) {
                                                if (page.value==0){
                                                    page.value = -1
                                                }else{
                                                    var list = if (showMoreApps.value) {
                                                        allList
                                                    }else{
                                                        appList
                                                    }
                                                    if (keySound.value){
                                                        speakText("打开应用")
                                                    }
                                                    val launchIntent =
                                                        packageManager.getLaunchIntentForPackage(
                                                            list.elementAt(currAppNumber.value).packageName
                                                        )
                                                    startActivity(launchIntent)
                                                }

                                            }
                                        }
                                        "home" -> {
                                            page.value = 0
                                            if (keySound.value){
                                                speakText("返回主页")
                                            }
                                        }
                                        "back" -> {
                                            if (callPhoneMode.value){
                                                if (keySound.value){
                                                    speakText("删除")
                                                }
                                                callPhoneText.value =
                                                    callPhoneText.value.substring(
                                                        0,
                                                        callPhoneText.value.length - 1
                                                    )

                                                if (callPhoneText.value == "") {
                                                    callPhoneMode.value = false
                                                    page.value = 0
                                                }else{
                                                    val database: QuickContractsDatabase =
                                                        QuickContractsDatabase.getDatabase(this@MainActivity)

                                                    val callHistoryDao = database.callHistoryDao()
                                                    thread {
                                                        contracts.clear()
                                                        var zz = callPhoneText.value + "%"
                                                        val list = callHistoryDao.getLimitByPhone(zz,15)
                                                        Log.d("MainActivity", "list: $list")
                                                        contracts.addAll(list)
                                                        Log.d("MainActivity", "contracts: $contracts")
                                                    }
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
                                                val callPermission = requestPhonePermission(this@MainActivity)

                                                val intent = Intent(Intent.ACTION_CALL)
                                                intent.data =
                                                    android.net.Uri.parse("tel:${callPhoneText.value}")
                                                startActivity(intent)
                                                val bac = callPhoneText.value
                                                thread {
                                                    val database: QuickContractsDatabase =
                                                        QuickContractsDatabase.getDatabase(this@MainActivity)
                                                    val callHistoryDao = database.callHistoryDao()
                                                    val quickContractsDao = database.quickContractsDao()
                                                    val quickContracts = quickContractsDao.getByPhone(bac)
                                                    if(quickContracts!=null){
                                                        callHistoryDao.insert(
                                                            CallHistory(
                                                                id=0,
                                                                name=quickContracts.name,
                                                                phone = bac,
                                                                type=2,
                                                                time= Date(System.currentTimeMillis()),
                                                                isDelete = false
                                                            )
                                                        )
                                                    }else{
                                                        callHistoryDao.insert(
                                                            CallHistory(
                                                                id=0,
                                                                name="",
                                                                phone = bac,
                                                                type=2,
                                                                time= Date(System.currentTimeMillis()),
                                                                isDelete = false
                                                            )
                                                        )
                                                    }



                                                }

                                                callPhoneMode.value = false
                                                callPhoneText.value = ""
                                                page.value = 0
                                                if (keySound.value){
                                                    speakText("拨打${callPhoneText.value}")
                                                }

                                            }else{
                                                page.value = -1

                                            }


                                        }
                                        "close" -> {
                                            if (callPhoneMode.value){
                                                callPhoneMode.value = false
                                                callPhoneText.value = ""
                                                page.value = 0
                                                if (keySound.value){
                                                    speakText("退出拨号")
                                                }
                                            }
                                        }
                                        // 其他的按键
                                        else -> {
                                            if (keySound.value){
                                                speakText(it)
                                            }
                                            if (callPhoneText.value==""){
                                                callPhoneMode.value = true
                                                page.value = -2
                                            }
                                            if (callPhoneMode.value) {
                                                callPhoneText.value += it
                                                //  如果不是 *\# 开头
                                                if (callPhoneText.value.length > 1) {
                                                    if (callPhoneText.value[0] != '*' && callPhoneText.value[0] != '#' )
                                                    {
                                                        val database: QuickContractsDatabase =
                                                            QuickContractsDatabase.getDatabase(this@MainActivity)

                                                        val callHistoryDao = database.callHistoryDao()
                                                        thread {
                                                            contracts.clear()
                                                            var zz = callPhoneText.value + "%"
                                                            val list = callHistoryDao.getLimitByPhone(zz,15)
                                                            Log.d("MainActivity", "list: $list")
                                                            contracts.addAll(list)
                                                            Log.d("MainActivity", "contracts: $contracts")
                                                        }

                                                    }
                                                    else{
                                                        // 如果 是 *#01#*
                                                        if (callPhoneText.value == "*#01#*") {
                                                            callPhoneText.value = ""
                                                            callPhoneMode.value = false
                                                            page.value = 0
                                                            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                                                            startActivity(intent)
                                                        }
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
                                ),
                                context = this@MainActivity
                            )
                        }



                    }
            }
        }
    }


    fun loadDesktop() {
        var list = if (showMoreApps.value) {
            allList
        } else {
            appList
        }
        var before = round(((pageCount / 2 - 1) / 2).toDouble()).toInt()
        biliList.clear()
        for (i in 0 until  before) {
            biliList.add(0f+0.1f*i)
        }

        var newList = biliList.reversed()
        biliList.add(0f+0.1f*before)

        biliList.addAll(newList)



        subList.clear()
        subList.addAll(if (currAppNumber.value - before < 0) {
            var qian = list.subList(
                currAppNumber.value - before + list.size,
                list.size
            )
            var hou = list.subList(
                0,
                if (currAppNumber.value + before >= list.size){
                    list.size
                }else{
                    currAppNumber.value + before + 1
                }
            )
            qian.addAll(hou)
            qian
        }
        else {
            var before_list = list.subList(
                currAppNumber.value - before,
                if (currAppNumber.value + before + 1 > list.size){
                    list.size
                }else{
                    currAppNumber.value + before + 1
                }
            )
            if (currAppNumber.value + before + 1 > list.size){
                var after_list = list.subList(
                    0,
                    currAppNumber.value + before + 1 - list.size
                )
                before_list.addAll(after_list)
                before_list
            }else{

                before_list
            }

        })
    }

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
        small :  Boolean = false,
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
                            if (showMoreApps.value) {
                                currPackageName.value = allList[0].packageName
                                loadDesktop()
                            }
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
                // 获取语言, string.xml

                var text = if (app.type == 99) {
                    resources.getString(R.string.more)
                }else if (app.type == 98) {
                    resources.getString(R.string.desktop_settings)
                } else {
                    resources.getString(R.string.mores)
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
        else if (app.type == 1024){
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
                   ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // 获取应用图标
                // 匹配屏幕最佳字体大小
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(50.dp)
                        .height(50.dp)
                ){

                }
                Column {
                    Text(
                        text = "",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start,
                        color = getDarkModeTextColor(
                            this@MainActivity
                        )
                    )

                }
            }
        }
        else{
            if (small){
                Row(
                    modifier = Modifier
                        .width(width)
                        .height(60.dp)
                        .padding(8.dp, 8.dp, 8.dp, 0.dp)
                       ,
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
                            .width(25.dp)
                            .height(25.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.extraLarge
                            )

                    )
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        color = getDarkModeTextColor(
                            this@MainActivity
                        )
                    )



                }
            }else{
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

    }
}



