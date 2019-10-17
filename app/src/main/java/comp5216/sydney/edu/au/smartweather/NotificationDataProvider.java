package comp5216.sydney.edu.au.smartweather;

import android.content.Context;

//Providing data to notification through SharedPreferences
class NotificationDataProvider {

    void setTimeNoti(Long timenoti, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putLong("TimeNoti", timenoti)
                .apply();
    }

    void setDateNoti(String datenoti, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("DateNoti", datenoti)
                .apply();
    }

    void setLocationNoti(String locationnoti, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("LocationNoti", locationnoti)
                .apply();
    }

    void setWeatherNoti(String weathernoti, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("WeatherNoti", weathernoti)
                .apply();
    }

    void setUmbrellaNoti(String umbrellanoti, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("UmbrellaNoti", umbrellanoti)
                .apply();
    }

    void setClothRecommendationNoti(String clothrecommendationnoti, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("ClothRecommendationNoti", clothrecommendationnoti)
                .apply();
    }

}
