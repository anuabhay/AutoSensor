package auto.ausiot.autosensor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
//import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        Switch swSensor1 = (Switch) findViewById(R.id.sensor_1);

        swSensor1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {

                    Subscriber.connect();
                    //Topic
                    String topic = unitID;
                    if(isChecked) {
                        Subscriber.sendMsg(topic, Constants.ACTION_R1_OPEN);
                    }else{
                        Subscriber.sendMsg(topic, Constants.ACTION_R1_CLOSE);
                    }

                    logger.log("Open Meessage sent");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        });

        Switch swSensor2 = (Switch) findViewById(R.id.sensor_2);

        swSensor2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    Subscriber.connect();
                    //Topic
                    String topic = unitID;
                    if(isChecked) {
                        Subscriber.sendMsg(topic, Constants.ACTION_R2_OPEN);
                    }else{
                        Subscriber.sendMsg(topic, Constants.ACTION_R2_CLOSE);
                    }
                    Subscriber.disconnect();

                    logger.log("Open Meessage sent");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

//        Button btnOpen = (Button) findViewById(R.id.button_open);
//        btnOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                view.startAnimation(buttonClick);
//                try {
//                    Subscriber.connect();
//                    Subscriber.sendMsg(Constants.ACTION_TOPIC,"open");
//                    logger.log("Open Meessage sent");
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });
//
//
//        Button btnClose = (Button) findViewById(R.id.button_close);
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                view.startAnimation(buttonClick);
//                try {
//                    Subscriber.connect();
//                    Subscriber.sendMsg(Constants.ACTION_TOPIC,"close");
//                    logger.log("Close Meessage sent");
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//        });

        try {
            subscribeToStatus();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public void subscribeToStatus()  throws MqttException, URISyntaxException{
        Subscriber.connect();
        Subscriber.subscribe(Constants.STATUS_TOPIC,new MQTTCallBack(){

            @Override
            public void onCallBack(String msg) {
                textBanner.setText(sdf.format(new Date()));
            }
        });
    }
}





