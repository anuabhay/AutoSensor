package auto.ausiot.autosensor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.util.ArrayList;

import auto.ausiot.util.Constants;
import auto.ausiot.util.Logger;
import mqtt.Subscriber;

/**
 * Created by anu on 23/06/19.
 */

public class EventViewer extends AppCompatActivity {

    private TextView mTextMessage;
    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    private Logger logger = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                     i = new Intent(EventViewer.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
//                case R.id.navigation_dashboard:
//                    i = new Intent(EventViewer.this,ManageSchedulesActivity.class);
//                    startActivity(i);
//                    //mTextMessage.setText(R.string.title_dashboard);
//                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Resources res = getResources();
        context = EventViewer.this.getApplicationContext();


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logger = new Logger(context);
        loadData();
        Button btnOpen = (Button) findViewById(R.id.button2);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                loadData();
            }

        });
    }

    void loadData(){
        String[] sa = logger.readLog();
        ListAdapter listAdapter = null;
        listAdapter=new ArrayAdapter<String>(context , android.R.layout.simple_spinner_item,sa);
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(listAdapter);
    }
}




