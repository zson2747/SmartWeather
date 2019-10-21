package comp5216.sydney.edu.au.smartweather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


//Implement App Widget provider
public class WeatherWidget extends AppWidgetProvider {

    //Update code
    private final String UPDATE_ALL = "comp5216.sydney.edu.au.smartweather.UPDATE_ALL";

    //Storing widget ids
    private static Set WidgetIdSet = new HashSet();

    //Update content for all exist widget
    static void updateAllAppWidget(Context context, AppWidgetManager appwidgetmanager,
                                Set widgetidset) {

        //Temp setting, need to be comment out later
        WidgetDataProvider wdp = new WidgetDataProvider();
        wdp.setTime ("23:59:60",context);
        wdp.setDate ("20/20/20",context);
        wdp.setLocation ("Syd",context);
        wdp.setWeather ("Rainy",context);
        wdp.setUmbrella ("Yes",context);
        wdp.setClothRecommendation ("Thick",context);

        //Get data from SharedPreferences
        String time
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                .getString("Time", "time");
        String date
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                .getString("Date", "date");
        String location
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                .getString("Location", "location");
        String weather
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                .getString("Weather", "weather");
        String umbrella
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                .getString("Umbrella", "umbrella");
        String clothrecommendation
                = context.getSharedPreferences("wd", MODE_PRIVATE)
                .getString("ClothRecommendation", "clothrecommendation");

        //Log.d("WidgetProvider",
        //        time+"lll"+date+"lll"+location+"lll"
        //                +weather+"lll"+umbrella+"lll"+clothrecommendation);

        int widgetid;
        Iterator it = widgetidset.iterator();

        //Update the content in each widget
        while (it.hasNext()) {
            widgetid = ((Integer)it.next());

            //Construct the remoteViews object
            RemoteViews remoteviews
                    = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            //Update each text
            remoteviews.setTextViewText(R.id.Time, time);
            remoteviews.setTextViewText(R.id.Date, date);
            remoteviews.setTextViewText(R.id.Location, location);
            remoteviews.setTextViewText(R.id.Weather, weather);
            remoteviews.setTextViewText(R.id.Umbrella, umbrella);
            remoteviews.setTextViewText(R.id.ClothRecommendation, clothrecommendation);

            //Instruct the widget manager to update the widget
            appwidgetmanager.updateAppWidget(widgetid, remoteviews);
            //Log.d("WidgetProvider", "text updated");
        }
    }

    //Receive broadcast
    @Override
    public void onReceive(Context context, Intent intent) {

        //Check if the broadcast is an update broadcast
        if (intent.getAction().equals(UPDATE_ALL)) {
            //Call update function
            updateAllAppWidget(context, AppWidgetManager.getInstance(context), WidgetIdSet);
        }

        super.onReceive(context, intent);
        //Log.d("WidgetProvider", "onReceive");
    }

    //Called on update
    @Override
    public void onUpdate(Context context, AppWidgetManager appwidgetmanager, int[] widgetids) {

        //Record widget ids
        for (int widgetid : widgetids) {
            WidgetIdSet.add(widgetid);
        }

        updateAllAppWidget(context, appwidgetmanager, WidgetIdSet);
        //Log.d("WidgetProvider", "onUpdate");
    }

    //Called when first widget is created
    @Override
    public void onEnabled(Context context) {

        //Start alarm
        WidgetAlarm widgetalarm = new WidgetAlarm(context.getApplicationContext());
        widgetalarm.startAlarm();

        super.onEnabled(context);
        //Log.d("WidgetProvider", "onEnabled");
    }

    //Called when last widget is removed
    @Override
    public void onDisabled(Context context) {

        // stop alarm
        AppWidgetManager appwidgetmanager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName
                = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appwidgetids
                = appwidgetmanager.getAppWidgetIds(thisAppWidgetComponentName);
        if (appwidgetids.length == 0) {
            WidgetAlarm widgetalarm
                    = new WidgetAlarm(context.getApplicationContext());
            widgetalarm.stopAlarm();
        }

        super.onDisabled(context);
        //Log.d("WidgetProvider", "onDisabled");
    }

    //Called when widget is removed
    @Override
    public void onDeleted(Context context, int[] appwidgetids) {

        //Delete stored widget id when widget is removed
        for (int appwidgetid : appwidgetids) {
            WidgetIdSet.remove(appwidgetid);
        }

        super.onDeleted(context, appwidgetids);
        //Log.d("WidgetProvider", "onDeleted");
    }
}
