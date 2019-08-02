package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AlphaAnimation;
//import android.widget.Button;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import auto.ausiot.util.AppConfig;
import auto.ausiot.util.Constants;
import auto.ausiot.util.Logger;
import mqtt.HeartBeatCallBack;
import mqtt.MQTTCallBack;
import mqtt.Subscriber;

public class MonitorActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static TextView textBanner;

    Context context ;
    Logger logger = null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    AppConfig config ;
    private static final DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private String unitID;

    Button btnSensor1;
    Button btnSensor2;
    private boolean isSensorOpen_1 = false;
    private boolean isSensorOpen_2 = false;
    MediaPlayer mp;
    private Handler handler;
    private static AlarmManager alarmManager = null;
    static HeartBeatCallBack hbcallback = null;
    static boolean initCalled = false;
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

        final Resources res = getResources();
        context = MonitorActivity.this.getApplicationContext();
        config = new AppConfig(MonitorActivity.this.getApplicationContext());
        this.unitID = config.readFirstConfig();

        checkInitialized();

        mTextMessage = (TextView) findViewById(R.id.message);
        textBanner = (TextView) findViewById(R.id.banner);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logger = new Logger(context);
        mp = MediaPlayer.create(this, R.raw.click);

        btnSensor1 = (Button) findViewById(R.id.button_sensor1);
        btnSensor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GradientDrawable drawable = (GradientDrawable) btnSensor1.getBackground();
                if (isSensorOpen_1) {
                    btnSensor1.setText("Open");
                    drawable.setColor(getResources().getColor(R.color.buttonOn));
                    mp.start();
                    sendMQTTMsg(unitID,Constants.ACTION_R1_CLOSE);
                }else{
                    btnSensor1.setText("Close");
                    drawable.setColor(Color.RED);
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                    mp.start();
                    sendMQTTMsg(unitID,Constants.ACTION_R1_OPEN);
                }
                isSensorOpen_1 = !isSensorOpen_1;

       }});

        btnSensor2 = (Button) findViewById(R.id.button_sensor2);
        btnSensor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GradientDrawable drawable = (GradientDrawable) btnSensor2.getBackground();
                if (isSensorOpen_2) {
                    btnSensor2.setText("Open");
                    drawable.setColor(getResources().getColor(R.color.buttonOn));
                    mp.start();
                    sendMQTTMsg(unitID,Constants.ACTION_R2_CLOSE);
                }else{
                    btnSensor2.setText("Close");
                    drawable.setColor(Color.RED);
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                    mp.start();
                    sendMQTTMsg(unitID,Constants.ACTION_R2_OPEN);
                }
                isSensorOpen_2 = !isSensorOpen_2;

            }});

        // Get Status
        // Subscribe to the same topic
        // Send Message STATUS
        // And look for Message format NETWORKON
        //HeartBeatCallBack.textBanner = textBanner;


        //setNetworkStatusBanner();
        subscribeToStatus(unitID);
        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
        setAlarm(unitID);
        // This needed when oncreate is called multiple times
        setNetworkStatusBanner();
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
        if(initCalled == false) {
            initCalled = true;
            textBanner.setText("Network Down");
            textBanner.setTextColor(Color.RED);
            // Set calender to One hour behind from the current time
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.HOUR, -1);
            Date oneHourBack = cal.getTime();
            HeartBeatCallBack.last_heart_beat = oneHourBack;
            try {
                hbcallback = new HeartBeatCallBack() {
                    @Override
                    public void onCallBack(String msg) {
                        if (msg.compareTo("NetworkON") == 0) {
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
            if (compareDates(HeartBeatCallBack.getLast_heart_beat(), new Date(), Constants.MAX_HEART_BEAT_MISS_DURATION)) {
                textBanner.setText("Network Down");
                textBanner.setTextColor(Color.RED);
            } else {
                textBanner.setText("Network On");
                textBanner.setTextColor(getResources().getColor(R.color.LineLableColor));
            }
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

}





