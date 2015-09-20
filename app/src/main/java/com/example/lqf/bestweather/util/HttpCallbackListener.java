package com.example.lqf.bestweather.util;

/**
 * Created by LQF on 2015/9/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
