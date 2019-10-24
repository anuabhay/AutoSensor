package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import auto.ausiot.stroe.ConfigFileStore;
import auto.ausiot.util.UserConfig;

/**
 * Created by anu on 23/06/19.
 */

public class InitViewer extends AppCompatActivity {

    Button button;
    Button btnScan;
    Button btnReset;
    EditText editText;
    //String EditTextValue ;
    Thread thread ;
    public final static int QRcodeWidth = 350 ;
    Bitmap bitmap ;

    TextView tv_qr_readTxt;
    //AppConfig config ;

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
//                case R.id.navigation_dashboard:
//                    i = new Intent(InitViewer.this,ManageSchedulesActivity.class);
//                    startActivity(i);
//                    //mTextMessage.setText(R.string.title_dashboard);
//                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(InitViewer.this,Disclaimer.class);
                    startActivity(i);;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initialize);

//        if (RestStore.authToken == null){
//            Intent i = new Intent(InitViewer.this,LoginActivity.class);
//            startActivity(i);
//        }

        //Add Icon to Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        btnScan = (Button)findViewById(R.id.btnScan);
        btnReset = (Button)findViewById(R.id.btnReset);



        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);

        //config = new AppConfig(InitViewer.this.getApplicationContext());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (UserConfig.isInitialized()){
            button.setEnabled(false);
            btnScan.setEnabled(false);
            btnScan.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
            btnReset.setText("Remove Unit  :: " + UserConfig.getFirstUnit());
            btnReset.setEnabled(true);
            btnReset.setVisibility(View.VISIBLE);
        }else{
            btnReset.setEnabled(false);
            btnReset.setVisibility(View.INVISIBLE);

            button.setEnabled(true);
            btnScan.setEnabled(true);
            btnScan.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);

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
                new AlertDialog.Builder(InitViewer.this)
                        .setTitle(getResources().getString(R.string.warning_title_unit_delete))
                        .setMessage(R.string.warning_detail_unit_delete)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSensor(UserConfig.getFirstUnit());
                            }
                        }).setNegativeButton("Cancel", null).show();
               // deleteSchedule(UserConfig.getFirstUnit());
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
    protected void onStart() {
        super.onStart();
        if (UserConfig.isInitialized()){
            button.setEnabled(false);
            btnScan.setEnabled(false);
            btnScan.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
            btnReset.setText("Remove Unit  :: " + UserConfig.getFirstUnit());
            btnReset.setEnabled(true);
            btnReset.setVisibility(View.VISIBLE);
        }else{
            btnReset.setEnabled(false);
            btnReset.setVisibility(View.INVISIBLE);

            button.setEnabled(true);
            btnScan.setEnabled(true);
            btnScan.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
        }

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
                //addUnit(result.getContents());
                editText.setText(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            //editText.setText(result.getContents());
            //Toast.makeText(this, "Scanned: " + data, Toast.LENGTH_LONG).show();
        }
    }

    void addSensor(String unitstr){
       ConfigFileStore config = new ConfigFileStore(getApplicationContext(),"config.txt");
       config.save(unitstr);
       restartApp();
    }

    void restartApp(){
        new AlertDialog.Builder(InitViewer.this)
                .setTitle(getResources().getString(R.string.warning_title_unit_add_1))
                .setMessage(R.string.warning_detail_unit_add_1)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);;
                    }
                }).setNegativeButton("Cancel", null).show();


    }

    void deleteSensor(final String unitID){
        ConfigFileStore config = new ConfigFileStore(getApplicationContext(),"config.txt");
        config.delete();
        Intent i = new Intent(InitViewer.this, InitViewer.class);
        startActivity(i);
//        ScheduleHelper sh = new ScheduleHelper();
//        RestCallBack rcallback =  new RestCallBack() {
//            int count = 0;
//            @Override
//            public void onResponse(Object obj) {
//                //@TODO Count Actual responses
//                //if(count++ == 2) {
//                    //config.reset();
//                    RestStore.deleteUnit(unitID);
//                    Intent i = new Intent(InitViewer.this, InitViewer.class);
//                    startActivity(i);
//                //}
//            }
//            @Override
//            public void onResponse(String token, String user) {
//
//            }
//            @Override
//            public void onFailure() {
//                //@TODO
//                int x = 1;
//            }
//
//        };
//        sh.deleteUnit(unitID, null,rcallback);


    }
    public boolean checkInitialized(){
        boolean ret = true;
        if ( UserConfig.isInitialized() == false){
            ret = false;
            Toast.makeText(this, "Not Init",Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    void createuser(String qrcode){
        //
    }
}



