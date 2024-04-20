package cn.tw.sar.easylauncher.beam.weather2

data class Result(
    val last_update: String,
    val location: Location,
    val now: Now
)