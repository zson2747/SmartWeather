package comp5216.sydney.edu.au.smartweather;

import android.content.Context;

//Providing data to widget through SharedPreferences
class WidgetDataProvider {

    void setTime(String time, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
            .edit()
            .putString("Time", time)
            .apply();
    }

    void setDate(String date, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("Date", date)
                .apply();
    }

    void setLocation(String location, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("Location", location)
                .apply();
    }

    void setWeather(String weather, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("Weather", weather)
                .apply();
    }

    void setUmbrella(String umbrella, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("Umbrella", umbrella)
                .apply();
    }

    void setClothRecommendation(String clothrecommendation, Context context) {

        context.getSharedPreferences("wd", Context.MODE_PRIVATE)
                .edit()
                .putString("ClothRecommendation", clothrecommendation)
                .apply();
    }

}
