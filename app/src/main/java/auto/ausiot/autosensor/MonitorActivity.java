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

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.util.Date;

import auto.ausiot.util.AppConfig;
import auto.ausiot.util.Constants;
import mqtt.HeartBeatCallBack;
import mqtt.Subscriber;

public class MonitorActivity extends AppCompatActivity implements WaterLineFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    public static TextView textBanner;
    public static TextView textBanner_2;


    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    AppConfig config ;
    private String unitID;


    MediaPlayer mp;
    public static AlarmManager alarmManager = null;
    static HeartBeatCallBack hbcallback = null;
    static boolean initCalled = false;


    //@TODO read from config
    private int ZONE_COUNT = 3;
    private int LINE_COUNT = 3;

    private static boolean isActivityInitialized = false;

    public boolean isNetwork_on() {
        return network_on;
    }

    public void setNetwork_on(boolean network_on) {
        this.network_on = network_on;
    }

    private boolean network_on = false;
    //private boolean network_status = true;
    //private Date last_heart_beat = new Date();

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
                case R.id.navigation_dashboard:
                    i = new Intent(MonitorActivity.this,MainActivity.class);
                    startActivity(i);
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(MonitorActivity.this,InitViewer.class);
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

    void checkInitialized(){
        if (config.checkInitialized() == false){
            Intent i = new Intent(MonitorActivity.this,InitViewer.class);
            startActivity(i);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help) {
            //startActivity(new Intent(this, CoursesActivity.class));
            startActivity(new Intent(this, AddUser.class));
        }
        if (item.getItemId() == R.id.app_settings) {
            startActivity(new Intent(this, AppSettings.class));
        }
        if (item.getItemId() == R.id.disclaimer) {
            startActivity(new Intent(this, Disclaimer.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        //Add Icon to Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_1_round);
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
        config = new AppConfig(MonitorActivity.this.getApplicationContext());
        this.unitID = config.readFirstConfig();
        checkInitialized();

        FragmentManager fm = getSupportFragmentManager();
        Fragment oldFragment = fm.findFragmentByTag("fragment_one");

        if (oldFragment == null) {
            mp = MediaPlayer.create(this, R.raw.click);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment1 = WaterLineFragment.newInstance("1", "1", "2", unitID, mp);
            ft.add(R.id.water_line, fragment1, "fragment_one");

            //Fragment fragment2 = WaterLineFragment.newInstance("2", "1", "2", unitID, mp);
            //ft.add(R.id.water_line, fragment2, "fragment_two");

            ft.commit();
        }
         //Init Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //subscribeToStatus(unitID);
        //sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
        //setAlarm(unitID);
        // This needed when oncreate is called multiple times
       // setNetworkStatusBanner();

        //handler = new Handler(Looper.getMainLooper());
        //handler.postDelayed(runnable, Constants.STATUS_CHECK_FREQUENCY);
    }

    @Override
    protected void onStart(){
        super.onStart();
        subscribeToStatus(unitID);
        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
        setAlarm(unitID);
        setNetworkStatusBanner();

    }

    @Override
    public void onResume() {
        super.onResume();
        //setNetworkStatusBanner();

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
            //setNetWorkDown();
            //textBanner.setText("Network Down");
            //textBanner.setTextColor(Color.RED);

            try {
                hbcallback = new HeartBeatCallBack() {
                    @Override
                    public void onCallBack(String msg) {
                        if (msg.compareTo(Constants.SENSOR_STATUS_ON_MSG) == 0) {
                            HeartBeatCallBack.last_heart_beat = new Date();
                            //setNetworkStatusBanner();
                        }
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

    public void setNetworkStatusBanner(){
        if (HeartBeatCallBack.getLast_heart_beat()!= null) {
            if (compareDates(HeartBeatCallBack.getLast_heart_beat(), new Date(), Constants.MAX_HEARTBEAT_MISSES * Constants.STATUS_CHECK_FREQUENCY)) {
                setNetWorkDown();

            } else {
                setNetWorkUp();

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
        setNetworkStatusBanner();
        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setNetWorkDown(){
        FragmentManager fm = getSupportFragmentManager();
        WaterLineFragment fragment = (WaterLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.disable_all_Controls();

        textBanner.setText("Network Down");
        textBanner.setTextColor(Color.RED);
        textBanner_2.setText(getResources().getString(R.string.try_again_text));
        network_on = false;
    }

    public void setNetWorkUp(){
        FragmentManager fm = getSupportFragmentManager();
        WaterLineFragment fragment = (WaterLineFragment)fm.findFragmentByTag("fragment_one");
        if (fragment!= null)
            fragment.enable_all_Controls();
        network_on = true;
    }


}





