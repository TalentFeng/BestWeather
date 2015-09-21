package com.example.lqf.bestweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.lqf.bestweather.receiver.AutoUpdateReceiver;
import com.example.lqf.bestweather.util.HttpCallbackListener;
import com.example.lqf.bestweather.util.HttpUtil;
import com.example.lqf.bestweather.util.Utility;

/**
 * Created by LQF on 2015/9/21.
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    public int onStartCommand(Intent intent,int flags,int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int hour=1*60*60*1000;
//        int hour=1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+hour;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("weatherCode", "");
        String address="http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
