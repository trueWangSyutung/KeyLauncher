package cn.tw.sar.easylauncher.beam.openWeatherMapApi

data class OpenWeatherCityBeanItem(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String
)