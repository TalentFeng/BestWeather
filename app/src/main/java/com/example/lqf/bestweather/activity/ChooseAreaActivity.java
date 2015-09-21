package com.example.lqf.bestweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqf.bestweather.R;
import com.example.lqf.bestweather.db.BestWeatherDB;
import com.example.lqf.bestweather.model.City;
import com.example.lqf.bestweather.model.County;
import com.example.lqf.bestweather.model.Province;
import com.example.lqf.bestweather.util.HttpCallbackListener;
import com.example.lqf.bestweather.util.HttpUtil;
import com.example.lqf.bestweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LQF on 2015/9/20.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVICE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String > adapter;
    private BestWeatherDB bestWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("selected_city",false)){
            Intent intent=new Intent(ChooseAreaActivity.this,WeatherAcitivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,dataList);
        listView.setAdapter(adapter);
        bestWeatherDB=BestWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVICE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherAcitivity.class);
                    intent.putExtra("county_code",countyList.get(position).getCountyCode());
                    startActivity(intent);
                    finish();
                }

            }
        });
        queryProvinces();

    }
    private void queryProvinces(){
        provinceList=bestWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("china");
            currentLevel=LEVEL_PROVICE;
        }else{
            queryFromServer(null,"province");
        }
    }
    private void queryCities() {
        cityList = bestWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }
    private void queryCounties() {
        countyList = bestWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }
    private void queryFromServer(final String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code +
                    ".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(bestWeatherDB,response);
                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(bestWeatherDB,response,selectedProvince.getId());

                }else if("county".equals(type)){
                    result=Utility.handleCountiesResponse(bestWeatherDB,response,selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }


}
