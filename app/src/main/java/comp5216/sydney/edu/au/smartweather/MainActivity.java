/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package comp5216.sydney.edu.au.smartweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Main Activity for Smart Weather app.
 */
public class MainActivity extends AppCompatActivity {

    // Define variables
    ArrayList<LocationsWeather> arrayOfLocations;

    // Define Constants
    private final String KEY = "6a700b02d5c29e9cb8e7b41308c994d1";

    /**
     * Initialize the activity
     *
     * @param savedInstanceState Bundle: If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). Note:
     *                           Otherwise it is null. This value may be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);

        // Construct the data source
        arrayOfLocations = new ArrayList<LocationsWeather>();

        //TODO: read from database

        //TODO: current location request
        LocationsWeather current = new LocationsWeather("-33.865143", "151.209900");
        arrayOfLocations.add(0, current);

        //TODO: save to database

        // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
        new Thread(networkTask).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            TextView tv1 = (TextView) findViewById(R.id.test);
            tv1.setText(arrayOfLocations.get(0).getJson());
        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            String result = null;
            // http request.网络请求相关操作
            for (LocationsWeather location : arrayOfLocations) {
                try {
                    result = HttpClient_GET(location.getLatitude(), location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    location.setJson(result);
                    //TODO: json处理
                    /*
            JSONArray jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++){
                HeadNews headnews=new HeadNews();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                headnews.setTitle(jsonObject.getString("title"));
                headnews.setContent(jsonObject.getString("content"));
                headnews.setConut(jsonObject.getString("conut"));
                headnews.setImage(jsonObject.getString("image"));
                list.add(headnews);
            }*/
                    //TODO: save to database
                }
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    /**
     * 天气API连接
     *
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    public String HttpClient_GET(String latitude, String longitude) throws Exception {
        String result = null;
        List<LocationsWeather> list = new LinkedList<LocationsWeather>();
        String path = "https://api.darksky.net/forecast/";
        StringBuilder sb = new StringBuilder(path);
        //key
        sb.append(KEY);
        sb.append("/");
        //latitude
        sb.append(latitude);
        sb.append(",");
        //longitude
        sb.append(longitude);
        //units
        sb.append("?units=si");
        System.out.println(sb);

        //1.得到浏览器
        HttpClient httpClient = new DefaultHttpClient();//浏览器
        //2指定请求方式
        HttpGet httpGet = new HttpGet(sb.toString());
        //3.执行请求
        HttpResponse httpResponse = httpClient.execute(httpGet);
        //4 判断请求是否成功
        int status = httpResponse.getStatusLine().getStatusCode();

        if (status == 200) {
            //读取响应内容
            result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } else Log.d("json", "Error");
        return result;
    }
}
