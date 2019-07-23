package auto.ausiot.autosensor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.GregorianCalendar;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.util.AppConfig;
import auto.ausiot.util.DateHelper;
import auto.ausiot.util.Logger;
import auto.ausiot.vo.Days;
import auto.ausiot.vo.Schedule;

/**
 * Created by anu on 23/06/19.
 */

public class InitViewer extends AppCompatActivity {

    ImageView imageView;
    Button button;
    Button btnScan;
    Button btnReset;
    EditText editText;
    //String EditTextValue ;
    Thread thread ;
    public final static int QRcodeWidth = 350 ;
    Bitmap bitmap ;

    TextView tv_qr_readTxt;
    AppConfig config ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(InitViewer.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    i = new Intent(InitViewer.this,MainActivity.class);
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
        setContentView(R.layout.activity_initialize);

        imageView = (ImageView)findViewById(R.id.imageView);
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        btnScan = (Button)findViewById(R.id.btnScan);
        btnReset = (Button)findViewById(R.id.btnReset);



        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);

        config = new AppConfig(InitViewer.this.getApplicationContext());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (config.checkInitialized()){
            button.setEnabled(false);
            btnScan.setEnabled(false);
            btnScan.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            btnReset.setText("Reset Sensor :- " + config.readConfig()[0].toString());
        }else{
            btnReset.setEnabled(false);
            btnReset.setVisibility(View.INVISIBLE);

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSensor(editText.getText().toString());
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSensor(config.readConfig()[0].toString());
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(InitViewer.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");
            } else {
                Log.e("Scan", "Scanned");
                tv_qr_readTxt.setText(result.getContents());
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //addSensor(result.getContents());
                editText.setText(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            //editText.setText(result.getContents());
            //Toast.makeText(this, "Scanned: " + data, Toast.LENGTH_LONG).show();
        }
    }

    void addSensor(String unitID){
        if (checkInitialized() == false) {
            //@TODO CREATE SENSOR
            // Create Sensor Using REST Service
            ScheduleHelper sh = new ScheduleHelper();
            final String sID = unitID;
            RestCallBack rcallback =  new RestCallBack() {
                int count ;
                @Override
                public void onResponse(Schedule scvo) {
                    //@TODO Count Actual responses
                    //if(count++ == 2) {
                        config.writeConfig(sID);
                        Intent i = new Intent(InitViewer.this, InitViewer.class);
                        startActivity(i);
                    //}
                }

                @Override
                public void onFailure() {
                    //@TODO
                    int x = 1;
                }

            };
            sh.addSensorfromservice(null,rcallback,unitID + "_1"  );
            sh.addSensorfromservice(null,rcallback,unitID + "_2"  );

        }


    }

    void deleteSensor(String unitID){
        ScheduleHelper sh = new ScheduleHelper();
        RestCallBack rcallback =  new RestCallBack() {
            int count = 0;
            @Override
            public void onResponse(Schedule scvo) {
                //@TODO Count Actual responses
                //if(count++ == 2) {
                    config.reset();
                    Intent i = new Intent(InitViewer.this, InitViewer.class);
                    startActivity(i);
                //}
            }

            @Override
            public void onFailure() {
                //@TODO
                int x = 1;
            }

        };
        sh.deleteScheduleFromService(null,rcallback,unitID + "_1");
        sh.deleteScheduleFromService(null,rcallback,unitID + "_2");



    }
    public boolean checkInitialized(){
        boolean ret = true;
        if ( config.checkInitialized() == false){
            ret = false;
            Toast.makeText(this, "Not Init",Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    void createuser(String qrcode){
        //
    }
}



