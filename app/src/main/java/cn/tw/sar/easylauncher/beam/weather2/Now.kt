package cn.tw.sar.easylauncher.beam.weather2

data class Now(
    val clouds: String,
    val code: String,
    val dew_point: String,
    val feels_like: String,
    val humidity: String,
    val pressure: String,
    val temperature: String,
    val text: String,
    val visibility: String,
    val wind_direction: String,
    val wind_direction_degree: String,
    val wind_scale: String,
    val wind_speed: String
)