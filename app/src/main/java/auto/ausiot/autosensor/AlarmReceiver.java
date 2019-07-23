package auto.ausiot.autosensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.schedule.ScheduleItemBO;
import auto.ausiot.util.Constants;
import mqtt.Subscriber;

/**
 * Created by anu on 21/06/19.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        ScheduleHelper sh = new ScheduleHelper();
        ScheduleBO sc  = sh.loadSchedule(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));

        ScheduleItemBO si = null;
        try {
            si = sc.hasScheduleItem(calendar.get(Calendar.DAY_OF_WEEK) - 1, new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ( si != null) {
            if (si.isEnabled()) {
                send(si);
            }
        }
    }

    void send(ScheduleItemBO si){
        try {
            String msg = si.getTime().toString() + si.getDuration();
            Subscriber.connect();
            Subscriber.sendMsg(Constants.ACTION_TOPIC,msg);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}