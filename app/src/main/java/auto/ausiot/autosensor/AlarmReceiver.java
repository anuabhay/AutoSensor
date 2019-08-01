package auto.ausiot.autosensor;

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
//        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
//        setNetworkStatusBanner();
//        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
        if (monitorActivity != null){
            monitorActivity.processAlarmCallBack();
        }
    }

//    public void xx(){
//        int x = 10;
//    }
//
//    public void sendMQTTMsg(String topic, String action) {
//        try {
//            Subscriber.sendMsg(topic, action);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setNetworkStatusBanner(){
//        if ( compareDates(HeartBeatCallBack.getLast_heart_beat(),new Date(),Constants.MAX_HEART_BEAT_MISS_DURATION)){
//            textBanner.setText("Network Down");
//            textBanner.setTextColor(Color.RED);
//        }else{
//            textBanner.setText("Network On");
//            textBanner.setTextColor(Color.GREEN);
//        }
//    }
//    public boolean compareDates(Date startTime , Date nowTime, int gapInMinutes) {
//        boolean ret = false;
//        long diff = nowTime.getTime() - startTime.getTime();
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
//
//        if ( minutes >= gapInMinutes){
//            ret = true;
//        }
//        return ret;
//    }


}