package auto.ausiot.autosensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;

import auto.ausiot.util.Constants;

/**
 * Created by anu on 23/06/19.
 */

public class AppSettings extends AppCompatActivity {

    private WebView webview;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(AppSettings.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    i = new Intent(AppSettings.this,MainActivity.class);
                    startActivity(i);
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final EditText config_hb = (EditText) findViewById(R.id.config_hb);
        final EditText config_network_freq = (EditText) findViewById(R.id.config_check_freq);

        config_hb.setText(Integer.toString(Constants.MAX_HEARTBEAT_MISSES));
        config_network_freq.setText(Integer.toString(Constants.STATUS_CHECK_FREQUENCY / 1000));
        final Button saveBtn = (Button) findViewById(R.id.button_save);

        config_hb.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                saveBtn.setText("Save");
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });


        config_network_freq.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                saveBtn.setText("Save");
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.MAX_HEARTBEAT_MISSES = Integer.parseInt(config_hb.getText().toString());
                Constants.STATUS_CHECK_FREQUENCY = (Integer.parseInt(config_network_freq.getText().toString())) * 1000;
                saveBtn.setText("Saved");
            }

        });
    }

}



