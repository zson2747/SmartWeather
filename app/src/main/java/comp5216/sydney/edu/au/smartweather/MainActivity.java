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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Main Activity for Smart Weather app.
 */
public class MainActivity extends AppCompatActivity {

    // Define variables
    LocationsWeather current;
    ArrayList<LocationsWeather> arrayOfLocations;
    LocationDB db;
    LocationDao locationDao;
    int apiRequestCount = 0;

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

        // Create an instance of ToDoItemDB and Dao
        db = LocationDB.getDatabase(this.getApplication().getApplicationContext());
        locationDao = db.locationDao();
        // read from database
        readItemsFromDatabase();
        updateUI();

        //TODO: current location request
        current = new LocationsWeather("-33.865143", "151.209900");
        //LocationsWeather current = new LocationsWeather("42.3601", "-71.0589");



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
            updateUI();
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
            // http request.网络请求相关操作
            try {
                HTTPClient hc = new HTTPClient();
                result = hc.HttpClient_GET(current.getLatitude(), current.getLongitude(), KEY);
            } catch (Exception e) {
                e.printStackTrace();
                data.putString("value", "NetworkError");
            }
            if (result != null) {
                apiRequestCount++;
                JsonHandler jh = new JsonHandler();
                jh.jsonHandler(current, result);
                data.putString("value", "200");
                if (arrayOfLocations.isEmpty()) {
                    arrayOfLocations.add(current);
                } else {
                    arrayOfLocations.set(0, current);
                    for (int i = 1; i < arrayOfLocations.size(); i++) {
                        try {
                            HTTPClient hc = new HTTPClient();
                            result = hc.HttpClient_GET(arrayOfLocations.get(i).getLatitude(),
                                    arrayOfLocations.get(i).getLongitude(), KEY);
                        } catch (Exception e) {
                            e.printStackTrace();
                            data.putString("value", "NetworkError");
                        }
                        if (result != null) {
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

            } else {
                data.putString("value", "Error");
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
                                    + " Name: " + item.getLocationInfo().getLatitude());
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
                    //TODO: location name
                    if (location.getDailySummary() != null)
                        Log.i("SQLite saved item", location.getDailySummary());
                    else
                        Log.i("SQLite saved item", location.getLatitude());
                }
                return null;
            }
        }.execute();
    }

    /**
     * UI界面的更新等相关操作
     */
    private void updateUI() {
        // TODO: UI界面的更新等相关操作
        TextView tv1 = (TextView) findViewById(R.id.test);
        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (!arrayOfLocations.isEmpty()) {
            tv1.setText(arrayOfLocations.get(0).toString());
        }


    }
}
