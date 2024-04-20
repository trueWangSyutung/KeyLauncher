package cn.tw.sar.easylauncher.beam.openWeatherMapApiW

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)