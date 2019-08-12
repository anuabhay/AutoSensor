package mqtt;

import android.graphics.Color;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by anu on 30/07/19.
 */

public class HeartBeatCallBack implements MQTTCallBack {
    public static Date last_heart_beat;

    public static boolean STATUS_R1;
    public static boolean STATUS_R2;
    public static TextView textBanner = null;

    @Override
    public void onCallBack(String msg) {
//        if (msg.compareTo("NETWORKON") == 0) {
//            last_heart_beat = new Date();
//            if (textBanner!= null){
//                textBanner.setText("Network On");
//                textBanner.setTextColor(Color.GREEN);
//            }
//        }

    }


    public static Date getLast_heart_beat() {
        return last_heart_beat;
    }

//    public static void setLast_heart_beat(Date last_heart_beat) {
//        HeartBeatCallBack.last_heart_beat = last_heart_beat;
//    }
}
