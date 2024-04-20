package cn.tw.sar.easylauncher.dao;

import cn.tw.sar.easylauncher.beam.CityBean;
import cn.tw.sar.easylauncher.beam.WeatherBean;
import cn.tw.sar.easylauncher.beam.weather2.WeatherAPIBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    //get请求
    // key=&location=beijing&language=zh-Hans&unit=c
    @GET("v3/weather/now.json")
    Call<WeatherBean> getData(
            @Query("key") String key,
            @Query("location") String location,
            @Query("language") String language,
            @Query("unit") String unit
    );

    @GET("v3/weather/now.json")
    Call<WeatherAPIBean> getWeatherByMoney(
            @Query("key") String key,
            @Query("location") String location,
            @Query("language") String language,
            @Query("unit") String unit
    );

    // ?key=S8pbZ7dUk0RT9Rnwt&q=shanghai
    @GET("v3/location/search.json")
    Call<CityBean> getCity(
            @Query("key") String key,
            @Query("q") String q
    );
}
