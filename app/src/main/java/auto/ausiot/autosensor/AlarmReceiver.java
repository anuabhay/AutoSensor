package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.schedule.ScheduleItemBO;
import auto.ausiot.util.Constants;
import mqtt.HeartBeatCallBack;
import mqtt.Subscriber;

/**
 * Created by anu on 21/06/19.
 */

public class AlarmReceiver extends BroadcastReceiver {
    //public static String unitID;
    //public static TextView textBanner;

    public static MonitorActivity monitorActivity = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (monitorActivity != null){
            monitorActivity.processAlarmCallBack();
        }

        Intent intent1 = new Intent(context, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent1, 0);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Constants.STATUS_CHECK_FREQUENCY, pendingIntent);

    }

}