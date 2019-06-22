package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import auto.ausiot.schedule.Schedule;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.ui.TimePickerFragment;
import auto.ausiot.util.DateHelper;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    Context context ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
             switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(MainActivity.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
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
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        context = MainActivity.this.getApplicationContext();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        hideDaily();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioConfigType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
            if (checkedId == R.id.radioButton_daily) {
                    hideWeekly();
            } else if (checkedId == R.id.radioButton_weekly) {
                    hideDaily();
            }
            }
        });


        TextView tvduration = (TextView) findViewById(R.id.chronometer2);
        tvduration.setText("00:00");
        TextView tvtime = (TextView) findViewById(R.id.timePicker);
        tvtime.setText("00:00");


        loadScheduleData();

        setAlarm();

        initListners();
    }

    private void initListners(){
        Button saveBtn = (Button) findViewById(R.id.button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onButtonClick((Button) view);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        });

        TextView tvDuration = (TextView) findViewById(R.id.chronometer2);
        tvDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setID(R.id.chronometer2);
                TextView tpHourMin = (TextView) findViewById(R.id.chronometer2);
                String str = tpHourMin.getText().toString();

                newFragment.show(getFragmentManager(),"TimePicker");
                newFragment.setTime(str);


            }
        });

        TextView tvDate = (TextView) findViewById(R.id.timePicker);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setID(R.id.timePicker);
                TextView tpHourMin = (TextView) findViewById(R.id.timePicker);
                String str = tpHourMin.getText().toString();
                newFragment.show(getFragmentManager(),"TimePicker");
                newFragment.setTime(str);


            }
        });
    }

    private void hideWeekly(){
        RadioGroup rdays = (RadioGroup) findViewById(R.id.radioDays);
        rdays.setVisibility(View.VISIBLE);

        LinearLayout lout = (LinearLayout) findViewById(R.id.lo_weeks);
        lout.setVisibility(View.INVISIBLE);
    }

    private void hideDaily(){
        RadioGroup rdays = (RadioGroup) findViewById(R.id.radioDays);
        rdays.setVisibility(View.INVISIBLE);

        LinearLayout lout = (LinearLayout) findViewById(R.id.lo_weeks);
        lout.setVisibility(View.VISIBLE);
    }

    public void onButtonClick(Button view) throws MqttException, URISyntaxException {
        saveScheduleData();
    }

    public Date getSheduleTimeFromUI(){

        TextView tpTime = (TextView) findViewById(R.id.timePicker);
        String time = tpTime.getText().toString();
        Date dt = DateHelper.getDateFromString(time);
        return dt;
    }


    public int getSheduleDurationFromUI(){
        TextView tpDuration = (TextView) findViewById(R.id.chronometer2);
        String duration = tpDuration.getText().toString();
        int minutes = DateHelper.getMinutesFromDateString(duration);
        return minutes;
    }

    public void testcallback(String a){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
            }
        });

    }

     void loadScheduleData(){
        //Schedule sc = new Schedule();
        //String schedule = "WEEKLY::1,13:00,PT9M,FALSE;2,13:00,PT9M,TRUE;3,13:00,PT9M,TRUE;4,13:00,PT9M,FALSE;5,13:00,PT9M,TRUE;6,13:00,PT9M,TRUE;0,13:00,PT9M,TRUE";
        ScheduleHelper sh = new ScheduleHelper();
        Schedule sc  = sh.loadSchedule(context);
        //sc.createSheduleFromString(schedule);

        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        String str = DateHelper.convertHoursMinutes2HHmm(calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR_OF_DAY));
        TextView tvtimepicker = (TextView) findViewById(R.id.timePicker);
        tvtimepicker.setText(str);

        int duration = sc.getDUrationForDay(Schedule.Days.Friday);

        TextView tvduration = (TextView) findViewById(R.id.chronometer2);
        tvduration.setText(DateHelper.convertMinutes2HHmm(duration));


        Resources res = getResources();
        Context context = MainActivity.this.getApplicationContext();

        for (Schedule.Days day : Schedule.Days.values()) {
            String val = Integer.toString(day.ordinal() + 1);
            int id = res.getIdentifier("checkBox_" + val, "id", context.getPackageName());
            CheckBox ch = (CheckBox) findViewById(id);

            if (sc.getEnabledForDay(day)) {
                ch.setChecked(true);
            }
        }


    }

    void saveScheduleData(){
        Schedule sc = new Schedule();
        Resources res = getResources();
        Context context = MainActivity.this.getApplicationContext();

        for (Schedule.Days day : Schedule.Days.values()) {
            String val = Integer.toString(day.ordinal() + 1);
            int id = res.getIdentifier("checkBox_" + val, "id", context.getPackageName());
            CheckBox ch = (CheckBox) findViewById(id);

           if (ch.isChecked()){
                sc.setSheduleItem(day,getSheduleTimeFromUI(),getSheduleDurationFromUI(),true);
            }else{
                sc.setSheduleItem(day,getSheduleTimeFromUI(),getSheduleDurationFromUI(),false);
            }
        }

        ScheduleHelper sh = new ScheduleHelper();
        sh.saveSchedule(context,sc);

    }

    void cancelAlarm(){

    }

    void setAlarm(){

        int REQUEST_CODE=101;

        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 15, pendingIntent);

    }
}
