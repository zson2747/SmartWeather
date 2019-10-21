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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


/**
 * Main Activity for Smart Weather app.
 */
public class MainActivity extends AppCompatActivity {

    // Define variables
    Double currentLatitude;
    Double currentLongtitude;
    LocationsWeather current;
    ArrayList<LocationsWeather> arrayOfLocations;
    LocationDB db;
    LocationDao locationDao;
    int apiRequestCount = 0;

    // Define Constants
    private static final String KEY = "6a700b02d5c29e9cb8e7b41308c994d1";
    private static final int MY_LOCATION_REQUEST_CODE = 1000;

    private AppBarConfiguration mAppBarConfiguration;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Construct the data source
        arrayOfLocations = new ArrayList<LocationsWeather>();

        // Create an instance of ToDoItemDB and Dao
        db = LocationDB.getDatabase(this.getApplication().getApplicationContext());
        locationDao = db.locationDao();
        // read from database
        readItemsFromDatabase();
        // TODO：计时器，半小时内不重复获取location和天气信息
        updateUI();

        //检测网络，如果连通则获取location信息，更新界面
        if (isConnected()) {
            // current location request
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            } else {
                getCurrentLocation();
            }
        } else {
            updateUI("NetworkError");
        }


        //LocationsWeather current = new LocationsWeather("42.3601", "-71.0589");


        // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
        //new Thread(networkTask).start();


        //Construct notification channel when SDK higher tha 26
        if (android.os.Build.VERSION.SDK_INT>26) {

            String channelid = "SmartWeather";
            String channelname = "WeatherNotification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationchannel =
                    new NotificationChannel(channelid, channelname, importance);
            NotificationManager notificationmanager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationmanager.createNotificationChannel(notificationchannel);
        }

        FloatingActionButton fab = findViewById(R.id.add_location_but);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_donot_forget_function, R.id.nav_manage_location,
                R.id.nav_wind_unit, R.id.nav_umbrella_bluetooth, R.id.nav_temperature_unit)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            updateUI(val);
        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            String result = null;
            JsonHandler jh = new JsonHandler();

            if (checkLocationNull(currentLatitude, currentLongtitude)) {
                current = new LocationsWeather(currentLatitude.toString(),
                        currentLongtitude.toString());
                // 尝试更新现在位置的天气
                try {
                    // http request.网络请求相关操作
                    HTTPClient hc = new HTTPClient();
                    result = hc.HttpClient_GET(current.getLatitude(), current.getLongitude(), KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                    data.putString("value", "NetworkError");
                }
                // 返回成功
                if (result != null) {
                    // api计数
                    apiRequestCount++;
                    data.putString("value", "200");
                    current.setJson(result);
                    //json处理
                    jh.jsonHandler(current, result);
                } else {
                    data.putString("value", "Error");
                }
            } else {
                current = new LocationsWeather(null, null);
            }
            // 首次打开app，只添加现在位置的天气
            if (arrayOfLocations.isEmpty()) {
                arrayOfLocations.add(current);
            } else {
                if (current.getJson() != null) {
                    arrayOfLocations.set(0, current);
                }
            }
            if (!arrayOfLocations.isEmpty()) {
                // 除了current, 其他的location获得天气
                for (int i = 1; i < arrayOfLocations.size(); i++) {
                    try {
                        // http request.网络请求相关操作
                        HTTPClient hc = new HTTPClient();
                        result = hc.HttpClient_GET(arrayOfLocations.get(i).getLatitude(),
                                arrayOfLocations.get(i).getLongitude(), KEY);
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.putString("value", "NetworkError");
                    }
                    // 返回成功
                    if (result != null) {
                        // api计数
                        apiRequestCount++;
                        arrayOfLocations.get(i).setJson(result);
                        //json处理
                        jh.jsonHandler(arrayOfLocations.get(i), result);
                        data.putString("value", "200");
                    } else {
                        data.putString("value", "Error");
                    }
                }
            }

            Log.i("Api Request Count", String.valueOf(apiRequestCount));
            // save to database
            saveItemsToDatabase();
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    /**
     * Read locations from local database and store into ArrayList
     */
    private void readItemsFromDatabase() {
        //Use asynchronous task to run query on the background and wait for result
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    //read locations from database
                    List<Location> locationsFromDB = locationDao.listAll();
                    arrayOfLocations = new ArrayList<LocationsWeather>();
                    if (locationsFromDB != null & locationsFromDB.size() > 0) {
                        for (Location item : locationsFromDB) {
                            arrayOfLocations.add(item.getLocationInfo());
                            //TODO: location name
                            Log.i("SQLite read item", "ID: " + item.getLocationID()
                                    + ", Latitude: " + item.getLocationInfo().getLatitude() + ", " +
                                    "Longtitude: " + item.getLocationInfo().getLongitude());
                        }
                    }
                    return null;
                }
            }.execute().get();
        } catch (Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }

    /**
     * Save locations to local database from ArrayList
     */
    private void saveItemsToDatabase() {
        //Use asynchronous task to run query on the background to avoid locking UI
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //delete all locations and re-insert
                locationDao.deleteAll();
                for (LocationsWeather location : arrayOfLocations) {
                    Location item = new Location(location);
                    locationDao.insert(item);
                    System.out.println("SQLite saved item: Latitude: " + location.getLatitude() +
                            ", Longtitude: " + location.getLongitude());
                }
                return null;
            }
        }.execute();
    }


    /**
     * UI界面的更新等相关操作，默认连接成功
     */
    private void updateUI() {
        updateUI("200");
    }

    /**
     * UI界面的更新等相关操作
     */
    private void updateUI(String val) {
        System.out.println("Latitude: " + currentLatitude +
                ", Longtitude: " + currentLongtitude);
        if (val == "Error" || val == "NetworkError") {
            Toast.makeText(this, "Network Connect Error", Toast.LENGTH_LONG).show();
        } else if (val == null) {
            Toast.makeText(this, "location permission denied", Toast.LENGTH_LONG).show();
        }
        // TODO: UI界面的更新等相关操作
//        if (checkLocationNull(currentLatitude, currentLongtitude)) {
//            TextView tv3 = (TextView) findViewById(R.id.test3);
//            tv3.setText(currentLatitude.toString());
//        }
//        TextView tv1 = (TextView) findViewById(R.id.test);
//        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
//        if (!arrayOfLocations.isEmpty()) {
//            tv1.setText(arrayOfLocations.get(0).toString());
//        }


    }

    /**
     * 检测用户allow或deny location permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "location permission granted", Toast.LENGTH_LONG).show();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "location permission denied", Toast.LENGTH_LONG).show();
                currentLatitude = null;
                currentLongtitude = null;
                getCurrentLocation(null);
            }
        }
    }

    /**
     * current location request
     */
    private void getCurrentLocation() {
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        //TODO: 计时器
        //为了尽少使用gps，只使用getLastLocation，在半个小时内不获取新的位置
        //天气app不像map，不需要连续的location获取，也不用setPriority
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongtitude = location.getLongitude();
                        } else {
                            currentLatitude = null;
                            currentLongtitude = null;
                        }
                        new Thread(networkTask).start();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                        currentLatitude = null;
                        currentLongtitude = null;
                        new Thread(networkTask).start();
                    }
                });
    }

    /**
     * 没有location permission，直接更新arraylist里剩下的天气
     *
     * @param status
     */
    private void getCurrentLocation(String status) {
        //TODO: 计时器
        new Thread(networkTask).start();
    }

    /**
     * 检测current location是否为空
     *
     * @param currentLatitude
     * @param currentLongtitude
     * @return
     */
    private boolean checkLocationNull(Double currentLatitude, Double currentLongtitude) {
        if (currentLatitude != null && currentLongtitude != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查网络连接
     *
     * @return 连接成功或失败
     */
    public boolean isConnected() {
        final String command = "ping -c 1 8.8.8.8";
        boolean isConnected = false;
        try {
            isConnected = Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    //Sending notification
    public void Notification(View view) {

        //Get this context
        Context context = this.getApplication().getApplicationContext();

        //Temp setting, need to be comment out later
        NotificationDataProvider ndp = new NotificationDataProvider();
        ndp.setTimeNoti (System.currentTimeMillis(),context);
        ndp.setDateNoti ("2020/20/20",context);
        ndp.setLocationNoti ("Sydney australia",context);
        ndp.setWeatherNoti ("Rainy",context);
        ndp.setUmbrellaNoti ("Bring Your Umbrella",context);
        ndp.setClothRecommendationNoti ("Wear Thick Cloth",context);

        //Get data from SharedPreferences
        Long time
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                        .getLong("TimeNoti", 0);
        String date
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                        .getString("DateNoti", "date");
        String location
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                        .getString("LocationNoti", "location");
        String weather
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                        .getString("WeatherNoti", "weather");
        String umbrella
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                        .getString("UmbrellaNoti", "umbrella");
        String clothrecommendation
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                        .getString("ClothRecommendationNoti", "clothrecommendation");

        //Construct BigTextStyle
        NotificationCompat.BigTextStyle bigtextstyle = new NotificationCompat.BigTextStyle()
                .bigText("Weather detail: " + weather
                                + "\nUmbrella alert: " + umbrella
                                + "\nCloth recommendation: " + clothrecommendation);

        //Construct notification compat builder
        NotificationCompat.Builder notificationbuilder =
                new NotificationCompat.Builder(this, "SmartWeather")

                        //attributes

                        //Notification method
                        //(DEFAULT_VIBRATE/DEFAULT_SOUND/DEFAULT_LIGHTS/DEFAULT_ALL)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        //Text on ticker
                        .setTicker("It's " + weather)
                        //Icon
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //Abstract, used as date
                        .setSubText(date)
                        //Notification time
                        .setWhen(time)
                        //Topic, used as location
                        .setContentTitle(location)
                        //Weather detail, umbrella alert and cloth recommendation
                        .setStyle(bigtextstyle)
                        //Clean when click on the notification
                        .setAutoCancel(true);

        //Set priority when SDK version under 27
        if (android.os.Build.VERSION.SDK_INT<27){
            notificationbuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        }

        //Construct intent
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingintent =
                PendingIntent.getActivity(this, 0, intent, 0);
        notificationbuilder.setContentIntent(pendingintent);

        //Send notification
        NotificationManager notificationmanager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Using same notification id will make new notification replace old one
        //set a different notification id to keep several notifications shown together
        notificationmanager.notify(0, notificationbuilder.build());
    }

}
