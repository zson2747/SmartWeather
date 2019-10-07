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

import java.text.DateFormat;
import java.util.ArrayList;

public class LocationsWeather {
    //TODO: location name
    private String name;
    private String latitude;
    private String longitude;
    private String json;
    private ArrayList<WeatherInfo> arrayOfCurrent;
    private String hourSummary;
    private String hourIcon;
    private ArrayList<WeatherInfo> arrayOfHour;
    private String dailySummary;
    private String dailyIcon;
    private ArrayList<WeatherInfo> arrayOfDaily;

    public LocationsWeather(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        json = null;
        arrayOfCurrent = new ArrayList<WeatherInfo>();
        arrayOfHour = new ArrayList<WeatherInfo>();
        arrayOfDaily = new ArrayList<WeatherInfo>();
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getJson() {
        return json;
    }

    public String getHourSummary() {
        return hourSummary;
    }

    public String getHourIcon() {
        return hourIcon;
    }

    public ArrayList<WeatherInfo> getArrayOfCurrent() {
        return arrayOfCurrent;
    }

    public ArrayList<WeatherInfo> getArrayOfHour() {
        return arrayOfHour;
    }

    public String getDailySummary() {
        return dailySummary;
    }

    public String getDailyIcon() {
        return dailyIcon;
    }

    public ArrayList<WeatherInfo> getArrayOfDaily() {
        return arrayOfDaily;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setCurrent(int time, String summary, double temperature) {
        WeatherInfo current = new WeatherInfo(time, summary);
        current.setTemperature(temperature);
        arrayOfCurrent.add(current);
    }

    public void setHourSummary(String hourSummary) {
        this.hourSummary = hourSummary;
    }

    public void setHourIcon(String hourIcon) {
        this.hourIcon = hourIcon;
    }

    public void setHour(int time, String summary, double temperature) {
        WeatherInfo hour = new WeatherInfo(time, summary);
        hour.setTemperature(temperature);
        arrayOfHour.add(hour);
    }

    public void setDailySummary(String dailySummary) {
        this.dailySummary = dailySummary;
    }

    public void setDailyIcon(String dailyIcon) {
        this.dailyIcon = dailyIcon;
    }

    public void setDaily(int time, String summary) {
        WeatherInfo daily = new WeatherInfo(time, summary);
        arrayOfDaily.add(daily);
    }

    public String toString() {
        DateFormat dfTime = DateFormat.getTimeInstance();
        DateFormat dfDate = DateFormat.getDateInstance();
        String print;
        print = "latitude: " + latitude + "\n" +
                "longitude: " + longitude + "\n" +
                "";
        print += "\nCurrent Weather:\n";
        for (WeatherInfo current : arrayOfCurrent) {
            print += "time: " + current.getTime() + ", summary: " + current.getSummary() + ", " +
                    "temperature:" + current.getTemp() + "\n";
        }
        print += "\nHourly Summary: " + hourSummary + "\n";
        for (WeatherInfo hour : arrayOfHour) {
            print += "time: " + dfTime.format(hour.getTime()) + ", summary: " + hour.getSummary() +
                    ", temperature:" + hour.getTemp() + "\n";
        }
        print += "\nDaily Summary: " + hourSummary + "\n";
        for (WeatherInfo daily : arrayOfDaily) {
            print += "time: " + dfDate.format(daily.getTime()) + ", summary: " + daily.getSummary() +
                    "\n";
        }
        return print;
    }
}
