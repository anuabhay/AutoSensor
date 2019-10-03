package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;


import auto.ausiot.util.UserConfig;
import auto.ausiot.util.Constants;
import mqtt.HeartBeatCallBack;
import mqtt.Subscriber;

public class MonitorActivity extends AppCompatActivity implements GarageLineFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    public static TextView textBanner;
    public static TextView textBanner_2;


    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    //AppConfig config ;
    private String unitID;


    MediaPlayer mp;
    public static AlarmManager alarmManager = null;
    static HeartBeatCallBack hbcallback = null;
    static boolean initCalled = false;


    public boolean isNetwork_on() {
        return network_on;
    }

    public void setNetwork_on(boolean network_on) {
        this.network_on = network_on;
    }

    private boolean network_on = false;
    //@TODO read from config
    private int ZONE_COUNT = 3;
    private int LINE_COUNT = 3;

    private static boolean isActivityInitialized = false;
    static Bundle m_savedInstanceState = new Bundle();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    //myBtn.setVisibility(View.VISIBLE);
                    return true;
//                case R.id.navigation_dashboard:
//                    i = new Intent(MonitorActivity.this,ManageSchedulesActivity.class);
//                    startActivity(i);
//                    //mTextMessage.setText(R.string.title_dashboard);
//                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(MonitorActivity.this,Disclaimer.class);
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

//    void checkInitialized(){
//        if (config.checkInitialized() == false){
//            Intent i = new Intent(MonitorActivity.this,InitViewer.class);
//            startActivity(i);
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.help) {
//            //startActivity(new Intent(this, CoursesActivity.class));
//            startActivity(new Intent(this, UserInfo.class));
//        }
//        if (item.getItemId() == R.id.app_settings) {
//            startActivity(new Intent(this, AppSettings.class));
//        }
//        if (item.getItemId() == R.id.disclaimer) {
//            startActivity(new Intent(this, Disclaimer.class));
//        }

        if (item.getItemId() == R.id.init_view) {
            startActivity(new Intent(this, InitViewer.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        unitID = UserConfig.checkInitialized(this);

        //Add Icon to Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        textBanner = (TextView) findViewById(R.id.banner);
        textBanner_2 = (TextView) findViewById(R.id.banner_2);



        if (isActivityInitialized == false) {
            isActivityInitialized = true;
            try {
                Subscriber.connect();
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        context = MonitorActivity.this.getApplicationContext();
        //config = new AppConfig(MonitorActivity.this.getApplicationContext());
        //this.unitID = config.readFirstConfig();
        //checkInitialized();
        FragmentManager fm = getSupportFragmentManager();
        Fragment oldFragment = fm.findFragmentByTag("fragment_one");

        if (oldFragment == null) {
            mp = MediaPlayer.create(this, R.raw.click);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment1 = GarageLineFragment.newInstance("1", "1", "2", unitID, mp);
            ft.add(R.id.water_line, fragment1, "fragment_one");

            //Fragment fragment2 = GarageLineFragment.newInstance("2", "1", "2", unitID, mp);
            //ft.add(R.id.water_line, fragment2, "fragment_two");

            ft.commit();
        }
         //Init Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //m_savedInstanceState = savedInstanceState;
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(unitID != null) {
            subscribeToStatus(unitID);
            sendMQTTMsg(unitID, Constants.ACTION_GET_STATUS);
            setAlarm(unitID);
            setNetworkStatusBanner(false);
            //Restore Fragment values
            restore_fragment_check_boxes(m_savedInstanceState);
        }
    }

    private void restore_fragment_check_boxes(Bundle savedInstanceState){
        if(savedInstanceState != null) {
            FragmentManager fm = getSupportFragmentManager();
            GarageLineFragment fragment = (GarageLineFragment) fm.findFragmentByTag("fragment_one");
            if (fragment != null)
                fragment.restore_state(savedInstanceState);
                //fragment.set_Indicators();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //setNetworkStatusBanner();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        GarageLineFragment fragment = (GarageLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.save_state(m_savedInstanceState);
//        m_savedInstanceState.putInt("aaa",1);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        GarageLineFragment fragment = (GarageLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.restore_state(savedInstanceState);
    }

    public void sendMQTTMsg(String topic, String action) {
        try {
            Subscriber.sendMsg(topic, action);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //Topic

    }

    public void subscribeToStatus(String topic)  {
        if(initCalled == false) {
            initCalled = true;
            network_on = false;
            try {
                hbcallback = new HeartBeatCallBack() {
                    /*
                    Format

                    {
                        'UNIT' : 'ON' ,
                        'R1'   :  'OFF' ,
                        'R2'   : 'ON'
                    }


                     */
                    @Override
                    public void onCallBack(String msg) {
                        Gson gson = new GsonBuilder().create();
                        Object obj =  gson.fromJson(msg,Object.class);
                        //@TODO Need to take off when all messages are JSON
                        if ((obj instanceof String) == false) {
                            Map<String, String> map = (Map<String, String>) obj;
                            if (map.get("UNIT").compareTo("ON") == 0){
                                HeartBeatCallBack.last_heart_beat = new Date();
                            }
                            if (map.get("R1").compareTo("ON") == 0){
                                HeartBeatCallBack.STATUS_R1 = true;
                            }
                            if (map.get("R2").compareTo("ON") == 0){
                                HeartBeatCallBack.STATUS_R2 = true;
                            }
                            if (map.get("R1").compareTo("OFF") == 0){
                                HeartBeatCallBack.STATUS_R1 = false;
                            }
                            if (map.get("R2").compareTo("OFF") == 0){
                                HeartBeatCallBack.STATUS_R2 = false;
                            }
                        }

//                        if (msg.compareTo(Constants.SENSOR_STATUS_ON_MSG) == 0) {
//                            HeartBeatCallBack.last_heart_beat = new Date();
//                            //Interrupt the UI thread using an alarm
//                            Intent intent1 = new Intent(context, AlarmReceiver.class);
//                            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent1, 0);
//                            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1, pendingIntent);
//                            //setNetworkStatusBanner();
//                        }else if((msg.compareTo(Constants.STATUS_R1_CLOSE) == 0) || (msg.compareTo(Constants.STATUS_R1_OPEN)== 0)){
//
//                        }else if((msg.compareTo(Constants.STATUS_R2_CLOSE) == 0) || (msg.compareTo(Constants.STATUS_R2_OPEN)== 0)){
//
//                        }
                    }
                };
                HeartBeatCallBack.textBanner = textBanner;
                Subscriber.connect();
                Subscriber.subscribe_to_heartbeat(topic);
                Subscriber.setHbcallback(hbcallback);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNetworkStatusBanner(boolean iscallback){
        if (HeartBeatCallBack.getLast_heart_beat()!= null) {
            if (compareDates(HeartBeatCallBack.getLast_heart_beat(), new Date(), Constants.MAX_HEARTBEAT_MISSES * Constants.STATUS_CHECK_FREQUENCY)) {
                setNetWorkDown();
                HeartBeatCallBack.setNetwork_up(false);
            } else {
                setNetWorkUp(iscallback);
                HeartBeatCallBack.setNetwork_up(true);
            }
            if(iscallback){
                setIndicators();
            }
        }else{
            setNetWorkDown();
        }
    }
    public boolean compareDates(Date startTime , Date nowTime, int gapInMiliSeconds) {
        boolean ret = false;
        long diff = nowTime.getTime() - startTime.getTime();
        //long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        if ( diff >= gapInMiliSeconds){
            ret = true;
        }
        return ret;
    }

    void cancelAlarm(){
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), Constants.ALARM_REQUEST_CODE, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);


    }

    void setAlarm(String unitID){
//        if (alarmManager == null) {
//            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//            AlarmReceiver.monitorActivity =this;
//            Intent intent = new Intent(this, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 120, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.STATUS_CHECK_FREQUENCY, pendingIntent);
//
//        }
        if (alarmManager == null) {
            AlarmReceiver.monitorActivity =this;
            Intent intent = new Intent(MonitorActivity.this, AlarmReceiver.class);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, 0);

            final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Constants.STATUS_CHECK_FREQUENCY, pendingIntent);
        }
    }

    public void processAlarmCallBack(){
        //Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        setNetworkStatusBanner(true);
        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void setNetWorkDown(){
        FragmentManager fm = getSupportFragmentManager();
        GarageLineFragment fragment = (GarageLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.disable_all_Controls();

        textBanner.setText("Network Down");
        textBanner.setTextColor(Color.RED);
        textBanner_2.setText(getResources().getString(R.string.try_again_text));
        network_on = false;
    }

    public void setNetWorkUp(boolean iscallback){
        FragmentManager fm = getSupportFragmentManager();
        GarageLineFragment fragment = (GarageLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.enable_all_Controls(iscallback);
        network_on = true;
    }

    public void setIndicators(){
        FragmentManager fm = getSupportFragmentManager();
        GarageLineFragment fragment = (GarageLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.set_Indicators();

    }

}





