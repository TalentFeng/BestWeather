package com.example.lqf.bestweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lqf.bestweather.service.AutoUpdateService;

/**
 * Created by LQF on 2015/9/21.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    public void onReceive(Context context,Intent intent){
        Intent i=new Intent(context,AutoUpdateService.class);
        context.startService(i);
    }
}
