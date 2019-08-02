package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
//import android.widget.Button;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import auto.ausiot.util.AppConfig;
import auto.ausiot.util.Constants;
import auto.ausiot.util.Logger;
import mqtt.HeartBeatCallBack;
import mqtt.Subscriber;

public class MonitorActivity extends AppCompatActivity implements WaterLineFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private static TextView textBanner;

    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    AppConfig config ;
    private String unitID;


    MediaPlayer mp;
    private static AlarmManager alarmManager = null;
    static HeartBeatCallBack hbcallback = null;

    //@TODO read from config
    private int ZONE_COUNT = 3;
    private int LINE_COUNT = 3;

    private static boolean isActivityInitialized = false;

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
        if (item.getItemId() == R.id.add_user) {
            //startActivity(new Intent(this, CoursesActivity.class));
            startActivity(new Intent(this, AddUser.class));
        }
        if (item.getItemId() == R.id.manage_sensor) {
            //startActivity(new Intent(this, AddUser.class));
        }
        if (item.getItemId() == R.id.manage_something) {
            //startActivity(new Intent(this, HandicapActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        if (isActivityInitialized == false) {
            isActivityInitialized = true;
            try {
                Subscriber.connect();
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            mp = MediaPlayer.create(this, R.raw.click);
        }

        context = MonitorActivity.this.getApplicationContext();
        config = new AppConfig(MonitorActivity.this.getApplicationContext());
        this.unitID = config.readFirstConfig();
        checkInitialized();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment1 = WaterLineFragment.newInstance("1", "1", "2", unitID, mp);
        ft.add(R.id.water_line, fragment1, "fragment_one");

        Fragment fragment2 = WaterLineFragment.newInstance("2", "1", "2", unitID, mp);
        ft.add(R.id.water_line, fragment2, "fragment_two");

        ft.commit();

        //Init Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        for ( int zcount=0 ; zcount < ZONE_COUNT ; zcount++) {
//            for (int lcount = 0; lcount < LINE_COUNT; lcount++) {
//            }
//        }
//
//        checkInitialized();
//
//        mTextMessage = (TextView) findViewById(R.id.message);
//        textBanner = (TextView) findViewById(R.id.banner);
//
//
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//        logger = new Logger(context);
//        mp = MediaPlayer.create(this, R.raw.click);
//
//        btnSensor1 = (Button) findViewById(R.id.button_sensor2);
//        btnSensor1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GradientDrawable drawable = (GradientDrawable) btnSensor1.getBackground();
//                if (isSensorOpen_1) {
//                    btnSensor1.setText("Open");
//                    drawable.setColor(getResources().getColor(R.color.buttonOn));
//                    mp.start();
//                    sendMQTTMsg(unitID,Constants.ACTION_R1_CLOSE);
//                }else{
//                    btnSensor1.setText("Close");
//                    drawable.setColor(Color.RED);
//                    view.playSoundEffect(SoundEffectConstants.CLICK);
//                    mp.start();
//                    sendMQTTMsg(unitID,Constants.ACTION_R1_OPEN);
//                }
//                isSensorOpen_1 = !isSensorOpen_1;
//
//       }});
//
//        btnSensor2 = (Button) findViewById(R.id.button_sensor2);
//        btnSensor2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GradientDrawable drawable = (GradientDrawable) btnSensor2.getBackground();
//                if (isSensorOpen_2) {
//                    btnSensor2.setText("Open");
//                    drawable.setColor(getResources().getColor(R.color.buttonOn));
//                    mp.start();
//                    sendMQTTMsg(unitID,Constants.ACTION_R2_CLOSE);
//                }else{
//                    btnSensor2.setText("Close");
//                    drawable.setColor(Color.RED);
//                    view.playSoundEffect(SoundEffectConstants.CLICK);
//                    mp.start();
//                    sendMQTTMsg(unitID,Constants.ACTION_R2_OPEN);
//                }
//                isSensorOpen_2 = !isSensorOpen_2;
//
//            }});
//


        //ft.add(R.id.water_line, WaterLineFragment.newInstance("1","1","2",unitID,mp));
        //ft.add(R.id.water_line, WaterLineFragment.newInstance("2","1","2",unitID,mp));
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        //ft.commit();
        // Now later we can lookup the fragment by tag


        // Get Status
        // Subscribe to the same topic
        // Send Message STATUS
        // And look for Message format NETWORKON
        //HeartBeatCallBack.textBanner = textBanner;


        //setNetworkStatusBanner();
//        subscribeToStatus(unitID);
//        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
//
//        setAlarm(unitID);
        //handler = new Handler(Looper.getMainLooper());
        //handler.postDelayed(runnable, Constants.STATUS_CHECK_FREQUENCY);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setNetworkStatusBanner();

    }
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//      /* do what you need to do */
//            setNetworkStatusBanner();
//            sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);      /* and here comes the "trick" */
//            handler.postDelayed(this, Constants.STATUS_CHECK_FREQUENCY);
//        }
//    };
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

        // Set calender to One hour behind from the current time
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.HOUR, -1);
//        Date oneHourBack = cal.getTime();
//        HeartBeatCallBack.setLast_heart_beat(oneHourBack);
        textBanner.setText("Network Down");
        textBanner.setTextColor(Color.RED);

        try {
            hbcallback = new HeartBeatCallBack()
            {
                @Override
                public void onCallBack(String msg) {
                    if (msg.compareTo("NETWORKON") == 0) {
                        HeartBeatCallBack.last_heart_beat = new Date();
                        //setNetworkStatusBanner();
                    }
                }
            };
            HeartBeatCallBack.textBanner = textBanner;
            Subscriber.connect();
            Subscriber.subscribe_to_heartbeat(topic);
            Subscriber.setHbcallback(hbcallback);

//            Subscriber.setHbcallback(new HeartBeatCallBack(){
//                @Override
//                public void onCallBack(String msg) {
//                    if (msg.compareTo("NETWORKON") == 0) {
//                        HeartBeatCallBack.last_heart_beat = new Date();
//                        setNetworkStatusBanner();
//                    }else if(msg.compareTo("STATUS") == 0){
//                        setNetworkStatusBanner();
//                    }
//
//                }
//            });

        }  catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setNetworkStatusBanner(){

        if ( compareDates(HeartBeatCallBack.getLast_heart_beat(),new Date(),Constants.MAX_HEART_BEAT_MISS_DURATION)){
            textBanner.setText("Network Down");
            textBanner.setTextColor(Color.RED);
        }else{
            textBanner.setText("Network On");
            textBanner.setTextColor(getResources().getColor(R.color.LineLableColor));
        }
    }
    public boolean compareDates(Date startTime , Date nowTime, int gapInMinutes) {
        boolean ret = false;
        long diff = nowTime.getTime() - startTime.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        if ( minutes >= gapInMinutes){
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
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            AlarmReceiver.monitorActivity =this;
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 120, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.STATUS_CHECK_FREQUENCY, pendingIntent);
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
}





