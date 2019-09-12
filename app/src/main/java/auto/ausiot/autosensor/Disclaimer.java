package auto.ausiot.autosensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import auto.ausiot.util.Constants;

/**
 * Created by anu on 23/06/19.
 */

public class Disclaimer extends AppCompatActivity {

    private WebView webview;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(Disclaimer.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
//                case R.id.navigation_dashboard:
//                    i = new Intent(Disclaimer.this,ManageSchedulesActivity.class);
//                    startActivity(i);
//                    //mTextMessage.setText(R.string.title_dashboard);
//                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        //Add Icon to Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_1_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        TextView textview= (TextView) findViewById(R.id.text_disclaimer);
        textview.setMovementMethod(ScrollingMovementMethod.getInstance());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


//        String x = " This software application is shipped with Oz Bach Multi-line Controller system (OBMCS). OBMCS consists of one or more controller units and water valves that allow you to manage multiple water lines connected to each zone.\n" +
//                "\n" +
//                "\\n\\nOz Bach controller and water valves have been tested and are guaranteed to perform with highest quality and accuracy under normal conditions. However no technology is warrantied to perform without error under all conditions.\n" +
//                "\n" +
//                "\\n\\nThese rare occurances can be due to varience in water pressure, in-correct workmanship in fitting and configuring these equipmentc etc. Please contact Oz Bach on XXXXX if you need any assistance in fixing or configuring OBMCS.\n" +
//                "\n" +
//                "\\n\\nThis software application provides you with the functionality to manage multiple water lines using your Android /IOS smart phone. This  application has been tested and will work without issues under normal conditions. However there could be network connectivity issues, hardware failure issues, etc which are beyond the control of Oz Bach Technologies.\n" +
//                "\n" +
//                "\\n\\nOz Bach Technologies will NOT assume responsibility for any damages caused by the use of OBMCS and this smart phone software application." +
//                "\n\n dsaddasdasd \n\n fdsfsdfdsfs \n\n fdsfsdfdsfs" +
//                "\n anu" +
//                "\n\n stop \n\ngggg" +
//                "\n\n phone"+
//                "\n\n\n\n\n\n";
//        textview.setText(x);
        //textview.setMovementMethod(ScrollingMovementMethod.getInstance());

    }

}



