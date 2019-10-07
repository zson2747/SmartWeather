package comp5216.sydney.edu.au.smartweather;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonHandler {

    /**
     * json处理
     *
     * @param location
     * @param result
     */
    protected void jsonHandler(LocationsWeather location, String result) {

        JSONObject jsonObject = JSON.parseObject(result);
        jsonHandlerCurrently(location, jsonObject);
        jsonHandlerHourly(location, jsonObject);
        jsonHandlerDaily(location, jsonObject);
    }

    /**
     * Currently
     *
     * @param location
     * @param jsonObject
     */
    private void jsonHandlerCurrently(LocationsWeather location, JSONObject jsonObject) {
        JSONObject currently = JSON.parseObject(jsonObject.getString("currently"));
        // current time
        int currentTime = currently.getInteger("time");
        // current summary
        String currentSummary = currently.getString("summary");
        // current temp
        double currentTemperature = currently.getDouble("temperature");
        // set current time, summary and temp
        location.setCurrent(currentTime, currentSummary, currentTemperature);
    }

    /**
     * Hourly
     *
     * @param location
     * @param jsonObject
     */
    private void jsonHandlerHourly(LocationsWeather location, JSONObject jsonObject) {
        JSONObject hourly = JSON.parseObject(jsonObject.getString("hourly"));
        // hour summary
        String hourSummary = hourly.getString("summary");
        // hour icon
        String hourIcon = hourly.getString("icon");
        // set hour summary and icon
        location.setHourSummary(hourSummary);
        location.setHourIcon(hourIcon);
        // hourly data
        JSONArray hourlyData = hourly.getJSONArray("data");
        for (int i = 0; i < hourlyData.size(); i++) {
            JSONObject jsonObjects = hourlyData.getJSONObject(i);
            // hour time
            int hourTime = jsonObjects.getInteger("time");
            // hour summary
            String hourSummaries = jsonObjects.getString("summary");
            // hour temp
            double hourTemperature = jsonObjects.getDouble("temperature");
            // set hour time, summary and temp
            location.setHour(hourTime, hourSummaries, hourTemperature);
        }
    }

    /**
     * Daily
     *
     * @param location
     * @param jsonObject
     */
    private void jsonHandlerDaily(LocationsWeather location, JSONObject jsonObject) {
        JSONObject daily = JSON.parseObject(jsonObject.getString("daily"));
        // daily summary
        String dailySummary = daily.getString("summary");
        // daily icon
        String dailyIcon = daily.getString("icon");
        // set daily summary and icon
        location.setDailySummary(dailySummary);
        location.setDailyIcon(dailyIcon);
        // daily data
        JSONArray dailyData = daily.getJSONArray("data");
        for (int i = 0; i < dailyData.size(); i++) {
            JSONObject jsonObjects = dailyData.getJSONObject(i);
            // daily time
            int dailyTime = jsonObjects.getInteger("time");
            // daily summary
            String dailySummaries = jsonObjects.getString("summary");
            // set daily time and summary
            location.setDaily(dailyTime, dailySummaries);
        }
    }
}
