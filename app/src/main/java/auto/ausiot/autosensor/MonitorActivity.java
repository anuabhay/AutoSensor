package auto.ausiot.autosensor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalTime;

import auto.ausiot.schedule.Schedule;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.util.Constants;
import auto.ausiot.util.Logger;
import mqtt.Subscriber;

public class MonitorActivity extends AppCompatActivity {

    private TextView mTextMessage;

    Context context ;
    Logger logger = null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);

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
                    i = new Intent(MonitorActivity.this,EventViewer.class);
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        Resources res = getResources();
        context = MonitorActivity.this.getApplicationContext();


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logger = new Logger(context);
        Button btnOpen = (Button) findViewById(R.id.button_open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                try {
                    Subscriber.connect();
                    Subscriber.sendMsg(Constants.ACTION_TOPIC,"open");
                    logger.log("Open Meessage sent");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        });


        Button btnClose = (Button) findViewById(R.id.button_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                try {
                    Subscriber.connect();
                    Subscriber.sendMsg(Constants.ACTION_TOPIC,"close");
                    logger.log("Close Meessage sent");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        });

        Button btnStatus = (Button) findViewById(R.id.button_status);
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onButtonStatus((Button) view);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        });


    }


    public void onButtonStatus(Button view) throws MqttException, URISyntaxException {
        view.startAnimation(buttonClick);


    }


}





