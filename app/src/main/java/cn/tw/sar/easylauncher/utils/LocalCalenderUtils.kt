package cn.tw.sar.easylauncher.utils

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.IslamicCalendar
import android.util.Log
import com.qweather.sdk.bean.base.Lang
import net.time4j.calendar.HebrewCalendar
import net.time4j.calendar.HijriCalendar
import net.time4j.calendar.PersianCalendar
import net.time4j.engine.StartOfDay
import java.time.LocalDate
import java.time.temporal.IsoFields


object LocalCalenderUtils {
    // 阿拉伯历法国家
    private val ARABIC_COUNTRIES = arrayOf(
        "DZ", "BH", "TD", "KM", "DJ", "EG", "ER", "IQ", "JO", "KW", "LB", "LY", "MR", "MA", "OM", "PS", "QA", "SA", "SO", "SD", "SY", "TN", "AE", "YE"
    )
    // 伊斯兰历法国家
    private val ISLAMIC_COUNTRIES = arrayOf(
        "AL", "AZ", "BD", "BF", "BI", "BJ", "CI", "CM", "DJ", "ER", "ET", "GA", "GM", "GN", "ID",  "JO", "KE", "KM", "KW", "LB", "ML", "MR", "MY", "NE", "NG", "OM", "PK", "PS", "QA", "SA", "SN", "SO", "SD", "SY", "TG", "TH", "TN", "TR", "TM", "UZ", "AE", "YE"
    )
    // 中国农历
    private val CHINESE_COUNTRIES = arrayOf(
        "CN", "HK", "MO"
    )
    // 波斯历法国家
    private val PERSIAN_COUNTRIES = arrayOf(
        "AF", "IR"
    )
    // 主体纪年法，朝鲜
    private val DANGUN_COUNTRIES = arrayOf(
        "KP"
    )
    // 民国纪年法，台湾地区
    private val MINGUO_COUNTRIES = arrayOf(
        "TW"
    )
    // 年号纪年法，日本
    private val JAPANESE_COUNTRIES = arrayOf(
        "JP"
    )
    // 希伯来历法
    private val HEBREW_COUNTRIES = arrayOf(
        "IL"
    )

    fun isChineseNation(context: Context):Boolean{
        val locale = context.resources.configuration.locale
        val country = locale.country
        if (CHINESE_COUNTRIES.contains(country) or MINGUO_COUNTRIES.contains(country)){
            Log.d("LocalCalenderUtils", "isChineseNation: true")
            return true
        }else{
            Log.d("LocalCalenderUtils", "isChineseNation: false")
            return false
        }
    }

    /**
     * 简体中文	zh-hans、zh	LANGUAGE_TYPE_ZH	ZH_HANS
     * 繁体中文	zh-hant	LANGUAGE_TYPE_ZHHANT	ZH_HANT
     * 英文	en	LANGUAGE_TYPE_EN	ENGLISH
     * 德语	de	LANGUAGE_TYPE_DE	GERMAN
     * 西班牙语	es	LANGUAGE_TYPE_ES	SPANISH
     * 法语	fr	LANGUAGE_TYPE_FR	FRENCH
     * 意大利语	it	LANGUAGE_TYPE_IT	ITALIAN
     * 日语	ja	LANGUAGE_TYPE_JP	JAPANESE
     * 韩语	ko	LANGUAGE_TYPE_KR	KOREAN
     * 俄语	ru	LANGUAGE_TYPE_RU	RUSSIAN
     * 印地语	hi	LANGUAGE_TYPE_IN	HINDI
     * 泰语	th	LANGUAGE_TYPE_TH	THAI
     * 阿拉伯语	ar	LANGUAGE_TYPE_AR	AR
     * 葡萄牙语	pt	LANGUAGE_TYPE_PT	PT
     * 孟加拉语	bn	LANGUAGE_TYPE_BN	BN
     * 马来语	ms	LANGUAGE_TYPE_MS	MS
     * 荷兰语	nl	LANGUAGE_TYPE_NL	NL
     * 希腊语	el	LANGUAGE_TYPE_EL	EL
     * 拉丁语	la	LANGUAGE_TYPE_LA	LA
     * 瑞典语	sv	LANGUAGE_TYPE_SV	SV
     * 印尼语	id	LANGUAGE_TYPE_ID	ID
     * 波兰语	pl	LANGUAGE_TYPE_PL	PL
     * 土耳其语	tr	LANGUAGE_TYPE_TR	TR
     * 捷克语	cs	LANGUAGE_TYPE_CS	CS
     * 爱沙尼亚语	et	LANGUAGE_TYPE_ET	ET
     * 越南语	vi	LANGUAGE_TYPE_VI	VI
     * 菲律宾语	fil	LANGUAGE_TYPE_FIL	FIL
     * 芬兰语	fi	LANGUAGE_TYPE_FI	FI
     * 希伯来语	he	LANGUAGE_TYPE_HE	HE
     * 冰岛语	is	LANGUAGE_TYPE_IS	IS
     * 挪威语	nb	LANGUAGE_TYPE_NB	NB
     */
    fun getWeatherLang(
        context: Context,
    ) : Lang {
        val locale = context.resources.configuration.locale
        Log.d("LocalCalenderUtils", "getWeatherLang: ${locale.language}")
        Log.d("LocalCalenderUtils", "getWeatherLang: ${locale.country}")
        Log.d("LocalCalenderUtils", "getWeatherLang: ${locale.script}")
        val country = locale.country
        when(country) {
            "CN" -> {
                return Lang.ZH_HANS
            }

            "TW", "HK", "MO" -> return Lang.ZH_HANT
            "JP" -> return Lang.JA
            "KR" -> return Lang.KO
            "RU" -> return Lang.RU
            "IN" -> return Lang.HI
            "TH" -> return Lang.TH
            "AR" -> return Lang.AR
            "PT" -> return Lang.PT
            "BN" -> return Lang.BN
            "MS" -> return Lang.MS
            "NL" -> return Lang.NL
            "EL" -> return Lang.EL
            "LA" -> return Lang.LA
            "SV" -> return Lang.SV
            "ID" -> return Lang.ID
            "PL" -> return Lang.PL
            "TR" -> return Lang.TR
            "CS" -> return Lang.CS
            "ET" -> return Lang.ET
            "VI" -> return Lang.VI
            "FIL" -> return Lang.FIL
            "FI" -> return Lang.FI
            "HE" -> return Lang.HE
            "IS" -> return Lang.IS
            "NB" -> return Lang.NB
            "EN" -> return Lang.EN
            "DE" -> return Lang.DE
            "ES" -> return Lang.ES
            "FR" -> return Lang.FR
            "IT" -> return Lang.IT

            else -> {
                return Lang.EN
            }
        }
    }

    @SuppressLint("NewApi")
    fun getLocalCalender(
        context: Context,
        year: Int,
        month: Int,
        day: Int
    ): String {
        // 获取系统语言地区
        val locale = context.resources.configuration.locale
        // 获取系统语言地区
        val language = locale.language
        // 获取系统语言地区
        val country = locale.country
        Log.d("LocalCalenderUtils", "language: $language, country: $country")
        // 中国农历
        if (CHINESE_COUNTRIES.contains(country)) {
            // 将公历转换为农历
            val lunarCalender = LunarCalender()
            var lunarCyclical = lunarCalender.cyclical(year, month, day)
            var launrAnimal = lunarCalender.animalsYear(year)

            var lunarString = lunarCalender.getLunarString(year, month, day);
            var fartival = lunarCalender.getFestival(year, month, day)

            return "农历${lunarCyclical}${launrAnimal}"
        }
        // 年号纪年法
        if (JAPANESE_COUNTRIES.contains(country)) {
            // 将公历转换为年号纪年法
            // 年份转换为日本年号，现在是令和 ，2019年5月1日以后是令和元年，2019年4月30日以前是平成31年，2020年1月1日以后是令和2年
            var era = ""
            if (year >= 2019) {
                era = "令和"
            } else if (year >= 1989) {
                era = "平成"
            } else if (year >= 1926) {
                era = "昭和"
            } else if (year >= 1912) {
                era = "大正"
            } else if (year >= 1868) {
                era = "明治"
            }
            var cha = year - 2019
            val strNum = arrayOf("元",  "二", "三", "四", "五", "六", "七", "八", "九")
            if (cha > 0 && cha < 10) {
                era += strNum[cha]
                return "令和${era}年${month}月${day}日"
            }

            // 将数字转为汉字
            val str = arrayOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九")
            while (cha > 0) {
                val n = cha % 10
                era = if (n != 0) {
                    str[n] + era
                } else {
                    str[n] + era
                }
                cha /= 10
            }

            return "令和${era}年${month}月${day}日"



        }

        // 主体纪年法
        if (DANGUN_COUNTRIES.contains(country)) {
            // 将公历转换为主体纪年法
            // 主体纪年法从1912年开始，1912年为主体1年，2012年为主体101年
            val cha = year - 1912
            // 获取当地的年、月、日翻译

            return "바디${cha}년${month}월${day}일"
        }

        // 民国纪年法
        if (MINGUO_COUNTRIES.contains(country)) {
            // 将公历转换为民国纪年法
            // 民国纪年法从1912年开始，1912年为民国1年，2012年为民国101年
            val cha = year - 1949
            if (cha == 1) {
                return "中華人民共和國元年${month}月${day}日"
            }
            val lunarCalender = LunarCalender()
            var lunarCyclical = lunarCalender.cyclical(year, month, day)
            var launrAnimal = lunarCalender.animalsYear(year)

            var lunarString = lunarCalender.getLunarString(year, month, day);
            var fartival = lunarCalender.getFestival(year, month, day)

            return "中華人民共和國${cha}年${month}月${day}日"+"\n"+ "農歷${lunarCyclical}${launrAnimal}"
        }

        // 波斯历法
        if (PERSIAN_COUNTRIES.contains(country)) {
            // 将公历转换为波斯历法
            // 波斯历法从622年开始，622年为波斯1年，2022年为波斯1401年
            val cha = year - 622

            val date = PersianCalendar.nowInSystemTime()
            val persianYear = date.year
            val persianMonth = date.month
            val persianDay = date.dayOfMonth

            return "${persianYear}, ${persianMonth}, ${persianDay}"
        }

        // 希伯来历法
        if (HEBREW_COUNTRIES.contains(country)) {

            // 利用 Time4J 库转换
            // שנה
            //חודש
            //יום

            val hebcal = HebrewCalendar.nowInSystemTime()
            val hebYear = hebcal.year
            val hebMonth = hebcal.month
            val hebDay = hebcal.dayOfMonth
            return "${hebYear}, ${hebMonth}, ${hebDay}"

        }

        // 伊斯兰历法
        if (ISLAMIC_COUNTRIES.contains(country) or  ARABIC_COUNTRIES.contains(country)) {
            // 将公历转换为伊斯兰历法
            val islandar = HijriCalendar.nowInSystemTime(
                HijriCalendar.VARIANT_UMALQURA,
                StartOfDay.MIDNIGHT
            )
            val islamicYear = islandar.year
            val islamicMonth = islandar.month
            val islamicDay = islandar.dayOfMonth
            // سنة
            //شهر .
            //اليوم .
            return "${islamicYear}, ${islamicMonth}, ${islamicDay}"
        }




        // 默认返回公历
        return ""

    }
}