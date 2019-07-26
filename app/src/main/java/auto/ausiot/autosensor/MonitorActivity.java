package auto.ausiot.autosensor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import auto.ausiot.util.AppConfig;
import auto.ausiot.util.Constants;
import auto.ausiot.util.Logger;
import mqtt.MQTTCallBack;
import mqtt.Subscriber;

public class MonitorActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView textBanner;

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
    private Timer myTimer;

    private boolean network_status = false;

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
        subscribeToStatus(unitID);
        sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);


        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                TimerMethod();
            }

        }, 0, Constants.STATUC_CHECK_FREQUENCY);

    }

    public void sendMQTTMsg(String topic, String action) {
        try {
            //Subscriber.connect();
            Subscriber.sendMsg(topic, action);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //Topic

    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            if ( network_status == false){
                textBanner.setText("Network Down");
                //textBanner.setTextColor(Color.RED);
            }else{
                textBanner.setText(sdf.format(new Date()));
            }
            network_status = false;
            sendMQTTMsg(unitID,Constants.ACTION_GET_STATUS);
            //subscribeToStatus(unitID);
        }


    };

    public void subscribeToStatus(String topic)  {
        try {
            Subscriber.connect();
            Subscriber.subscribe(topic, new MQTTCallBack() {

                @Override
                public void onCallBack(String msg) {
                    if (msg.compareTo("NETWORKON") == 0) {

                       //textBanner.setTextColor(getResources().getColor(R.color.LineLableColor));
                       network_status = true;
                    }
                }
            });
        }  catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}





