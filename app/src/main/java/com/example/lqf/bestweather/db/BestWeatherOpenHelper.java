package com.example.lqf.bestweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LQF on 2015/9/20.
 */
public class BestWeatherOpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_PROVINCE="create table Province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text" +
            ")";
    public static final String CREATE_CITY="create table City(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer" +
            ")";
    public static final String  CREATE_COUNTY="create table County(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer" +
            ")";
    public BestWeatherOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){

        super(context,name,factory,version);
    }
    public void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_PROVINCE);
        database.execSQL(CREATE_CITY);
        database.execSQL(CREATE_COUNTY);


    }
    public void onUpgrade(SQLiteDatabase database,int i,int version){

    }
}
