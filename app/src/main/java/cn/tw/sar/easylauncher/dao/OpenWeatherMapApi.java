package cn.tw.sar.easylauncher.dao;

import cn.tw.sar.easylauncher.beam.CityBean;
import cn.tw.sar.easylauncher.beam.openWeatherMapApi.OpenWeatherCityBean;
import cn.tw.sar.easylauncher.beam.openWeatherMapApiW.OpenWeatherBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {
    @GET("geo/1.0/direct")
    Call<OpenWeatherCityBean> getCity(
            @Query("q") String q,
            @Query("limit") int limit,
            @Query("appid") String appid
    );

    // OpenWeatherBean
    @GET("data/2.5/weather")
    Call<OpenWeatherBean> getWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String appid,
            @Query("lang") String lang,
            @Query("units") String units
    );
}
