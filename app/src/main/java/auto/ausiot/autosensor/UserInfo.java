package auto.ausiot.autosensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import auto.ausiot.stroe.RestStore;

/**
 * Created by anu on 23/06/19.
 */

public class UserInfo extends AppCompatActivity {

    private WebView webview;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(UserInfo.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
//                case R.id.navigation_dashboard:
//                    i = new Intent(UserInfo.this,ManageSchedulesActivity.class);
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
        setContentView(R.layout.activity_user_info);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        webview =(WebView)findViewById(R.id.webview);

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);


        TextView tv = (TextView) findViewById(R.id.banner);
        tv.setText("Welcome " + RestStore.user.getFullname());


        String url = "http://www.bom.gov.au/places/";
        String address = RestStore.user.getAddress();
        if (address.split(",").length == 3) {
            String localty_com = address.split(",")[1].trim();
            String locality = localty_com.split(" ")[0].toLowerCase();
            String state = localty_com.split(" ")[1].toLowerCase();
            url = "http://www.bom.gov.au/places/" + state + "/" + locality + "/";
        }

        if (address.split(",").length == 2) {
            String localty_com = address.split(",")[0].trim();
            String locality = localty_com.split(" ")[0].toLowerCase();
            String state = localty_com.split(" ")[1].toLowerCase();
            url = "http://www.bom.gov.au/places/" + state + "/" + locality + "/";
        }

        webview.loadUrl(url);

    }

}



