package comp5216.sydney.edu.au.smartweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


//Set alarm to update widget periodically
class WidgetAlarm
{

    //Update code
    private final Intent UPDATE_ALL  =
            new Intent("comp5216.sydney.edu.au.smartweather.UPDATE_ALL");

    //Settings
    private final int ALARM_ID = 12450;
    private final int SLEEP_PERIOD = 60000;

    private Context mContext;

    //Constructor
    WidgetAlarm(Context context)
    {
        mContext = context;
    }

    //Start alarm
    void startAlarm()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, SLEEP_PERIOD);

        //Construct PendingIntent
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(mContext, ALARM_ID, UPDATE_ALL,
                PendingIntent.FLAG_CANCEL_CURRENT);

        //Construct the AlarmManager object
        AlarmManager alarmManager
                = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);

        //Setting alarm to broadcasting (RTC does not wakes up the device)
        alarmManager
                .setRepeating(AlarmManager.RTC,
                        calendar.getTimeInMillis(), SLEEP_PERIOD, pendingIntent);
    }

    //Stop alarm
    void stopAlarm()
    {
        //Construct PendingIntent
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, ALARM_ID, UPDATE_ALL,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        //Stop alarm
        AlarmManager alarmManager = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}