package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.ui.TimePickerFragment;
import auto.ausiot.util.AppConfig;
import auto.ausiot.util.Constants;
import auto.ausiot.util.DateHelper;
import auto.ausiot.util.Logger;
import auto.ausiot.vo.Days;
import auto.ausiot.vo.Schedule;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    private Logger logger = null;
    ScheduleBO schedulebo = null;
    AppConfig config ;
    private RadioGroup radioGroupDays ;
    //@TODO THis need to change for user ID to support multiple sensors
    private String unitID;
    private String sensorID = "1";
    Button saveBtn;
    LinearLayout mainLayout;
    RadioButton line_1;
    RadioButton  line_2;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
             switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(MainActivity.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(MainActivity.this,InitViewer.class);
                    startActivity(i);

                    return true;
            }
            return false;
        }
    };

    void checkInitialized(){
        if (config.checkInitialized() == false){
            Intent i = new Intent(MainActivity.this,InitViewer.class);
            startActivity(i);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        context = MainActivity.this.getApplicationContext();
        config = new AppConfig(MainActivity.this.getApplicationContext());
        unitID = config.readFirstConfig();

        checkInitialized();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //hideWeekly();
        //@TODO If Weekly schedules are needed make this visible
//        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioConfigType);

        //@TODO If Weekly schedules are needed make this visible
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // find which radio button is selected
//            if (checkedId == R.id.radioButton_daily) {
//                    hideWeekly();
//            } else if (checkedId == R.id.radioButton_weekly) {
//                    hideDaily();
//            }
//            }
//        });

        mainLayout = findViewById(R.id.lo_all_content);
        line_1 = findViewById(R.id.radioButton_sensor1);
        line_2 = findViewById(R.id.radioButton_sensor2);

        RadioGroup radioGroupSensor = (RadioGroup) findViewById(R.id.radioSensorID);
        radioGroupSensor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radioButton_sensor1) {
                    sensorID = "1";
                    //RelativeLayout l =  (RelativeLayout) findViewById(R.id.XXXXX);
                    //l.setBackgroundColor(Color.CYAN);
                    line_2.setTextColor(Color.GRAY);
                    line_1.setTextColor(getResources().getColor(R.color.colorPrimary));

                    //mainLayout.setBackgroundColor(getResources().getColor(R.color.schedule_1_background));
                } else {
                        sensorID = "2";
                    //RelativeLayout l =  (RelativeLayout) findViewById(R.id.XXXXX);
                    line_1.setTextColor(Color.GRAY);
                    line_2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    //mainLayout.setBackgroundColor(getResources().getColor(R.color.schedule_2_background));
                    //l.setBackgroundColor(Color.GRAY              );
                }
                loadScheduleData();
            }
        });


        //RadioGroup radioGroupconfig = (RadioGroup) findViewById(R.id.radioConfigType);
        //radioGroupconfig.setVisibility(View.INVISIBLE);

        radioGroupDays = (RadioGroup) findViewById(R.id.radioDays);
        radioGroupDays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //hideDaily();
               onDayChange(checkedId);
            }
        });


        TextView tvduration = (TextView) findViewById(R.id.chronometer2);
        tvduration.setText("00:00");
        TextView tvtime = (TextView) findViewById(R.id.timePicker);
        tvtime.setText("00:00");
        //loadScheduleData();
        RadioButton radiobutton_sensor_1 = (RadioButton) findViewById(R.id.radioButton_sensor1);
        radiobutton_sensor_1.setChecked(true);

        //No Alarms From the app any more
        //setAlarm();

        initListners();
        logger = new Logger(context);
    }

    private Days getDayFromCheckID(int checkedId) {
        Days day = Days.Friday;
        switch (checkedId) {
            case R.id.radio_1:
                // code block
                day = Days.Monday;
                break;
            case R.id.radio_2:
                day = Days.Tuesday;
                break;
            case R.id.radio_3:
                day = Days.Wednesday;
                break;
            case R.id.radio_4:
                day = Days.Thursday;
                break;
            case R.id.radio_5:
                day = Days.Friday;
                break;
            case R.id.radio_6:
                day = Days.Saturday;
                break;
            case R.id.radio_7:
                day = Days.Sunday;
                break;
            default:
                day = Days.Friday;
        }

        return day;
    }

    private void onDayChange(int checkedId){
        Days day = getDayFromCheckID(checkedId);

        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(schedulebo.getTimeForDay(day));

        calendar.setTimeZone(TimeZone.getDefault());
        String str = DateHelper.convertHoursMinutes2HHmm(calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR_OF_DAY));

        TextView tvtimepicker = (TextView) findViewById(R.id.timePicker);
        tvtimepicker.setText(str);

        int duration = schedulebo.getDurationForDay(day);
        TextView tvduration = (TextView) findViewById(R.id.chronometer2);
        tvduration.setText(DateHelper.convertMinutes2HHmm(duration));

        boolean enabled = schedulebo.getEnabledForDay(day);
        CheckBox chkEnabled = (CheckBox) findViewById(R.id.checkBox_enable);
        chkEnabled.setChecked(enabled);


    }
    private void initListners(){
        // Save button
        saveBtn = (Button) findViewById(R.id.button);
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

        CheckBox chkEnabled = (CheckBox) findViewById(R.id.checkBox_enable);
        chkEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    Days day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());
                    schedulebo.getScheduleItem(day).setEnabled(isChecked);
                }
            }
        );

        // Capture the Duration  ( sensor will be open for this duration )
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

        // Capture the time ( sensor will be open at this time )
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

        addTextWatchers(tvDuration, tvDate);

    }

    /**
     * This is to capture Duration and Time
     *
     * @param tvDuration
     * @param tvDate
     */
    private void addTextWatchers(TextView tvDuration , TextView tvDate)
    {
        TextWatcher twdate = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //RadioGroup radioGroupDays = (RadioGroup) findViewById(R.id.radioDays);
                Days day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());
                Date dt = DateHelper.getDateFromString(s.toString());
                schedulebo.getScheduleItem(day).setTime(dt);
                saveBtn.setText("Save");

            }

        };
        tvDate.addTextChangedListener(twdate);

        TextWatcher twDuration = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //RadioGroup radioGroupDays = (RadioGroup) findViewById(R.id.radioDays);
                Days day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());
                int minutes = DateHelper.getMinutesFromDateString(s.toString());
                schedulebo.getScheduleItem(day).setDuration(minutes);
                saveBtn.setText("Save");
            }

        };
        tvDuration.addTextChangedListener(twDuration);
    }

    /**
     * Hide weekly Schedule Controls
     */
//    private void hideWeekly(){
//        RadioGroup rdays = (RadioGroup) findViewById(R.id.radioDays);
//        rdays.setVisibility(View.VISIBLE);
//
//        LinearLayout lout = (LinearLayout) findViewById(R.id.lo_weeks);
//        lout.setVisibility(View.INVISIBLE);
//    }
//
//    private void hideDaily(){
//        RadioGroup rdays = (RadioGroup) findViewById(R.id.radioDays);
//        rdays.setVisibility(View.INVISIBLE);
//
//        LinearLayout lout = (LinearLayout) findViewById(R.id.lo_weeks);
//        lout.setVisibility(View.VISIBLE);
//    }

    public void onButtonClick(Button view) throws MqttException, URISyntaxException {
        view.startAnimation(buttonClick);

//        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//        Date x = schedulebo.getTimeForDay(Days.Monday);
//        calendar.setTime(x);
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date newdate = new Date();
//
//        //newdate.setYear(calendar.get(Calendar.YEAR));
//        //newdate.setMonth(calendar.get(Calendar.MONTH));
//        newdate.setDate(calendar.get(Calendar.DATE));
//        newdate.setHours(calendar.get(Calendar.HOUR));
//        newdate.setMinutes(calendar.get(Calendar.MINUTE));
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
        //ScheduleBO sc = new ScheduleBO();
        //String schedule = "WEEKLY::1,13:00,PT9M,FALSE;2,13:00,PT9M,TRUE;3,13:00,PT9M,TRUE;4,13:00,PT9M,FALSE;5,13:00,PT9M,TRUE;6,13:00,PT9M,TRUE;0,13:00,PT9M,TRUE";

        ScheduleHelper sh = new ScheduleHelper();
        //ScheduleBO sc  = sh.loadSchedule(context);


        RestCallBack rcallback =  new RestCallBack() {
             @Override
             public void onResponse(Schedule scvo) {
                 schedulebo = ScheduleBO.getScheduleBO(scvo);
                 Days selected_day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());

                 Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                 calendar.setTime(schedulebo.getTimeForDay(selected_day));
                 calendar.setTimeZone(TimeZone.getDefault());

                 /*int m = calendar.get(Calendar.MINUTE);
                 int h = calendar.get(Calendar.HOUR_OF_DAY);
                 long timeCPH = calendar.getTimeInMillis();

                 calendar.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));

                 m = calendar.get(Calendar.MINUTE);
                 h = calendar.get(Calendar.HOUR_OF_DAY);
                 timeCPH = calendar.getTimeInMillis();*/


                 String str = DateHelper.convertHoursMinutes2HHmm(calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR_OF_DAY));


                 TextView tvtimepicker = (TextView) findViewById(R.id.timePicker);
                 tvtimepicker.setText(str);

                 int duration = schedulebo.getDurationForDay(selected_day);

                 TextView tvduration = (TextView) findViewById(R.id.chronometer2);
                 tvduration.setText(DateHelper.convertMinutes2HHmm(duration));


                 boolean enabled = schedulebo.getEnabledForDay(selected_day);
                 CheckBox chkEnabled = (CheckBox) findViewById(R.id.checkBox_enable);
                 chkEnabled.setChecked(enabled);

                 Resources res = getResources();
                 Context context = MainActivity.this.getApplicationContext();

                 for (Days day : Days.values()) {
                     String val = Integer.toString(day.ordinal() + 1);
                     int id = res.getIdentifier("checkBox_" + val, "id", context.getPackageName());
                     CheckBox ch = (CheckBox) findViewById(id);

                     if (schedulebo.getEnabledForDay(day)) {
//                         ch.setChecked(true);
                     }
                 }
             }

             @Override
             public void onFailure() {

             }

         };
        sh.loadScheduleFromService(unitID + "_" + sensorID ,context,rcallback);
    }

    void saveScheduleData(){

        ScheduleHelper sh = new ScheduleHelper();
        RestCallBack rcallback =  new RestCallBack() {
            @Override
            public void onResponse(Schedule scvo) {
                saveBtn.setText("Saved");
            }

            @Override
            public void onFailure() {

            }

        };
        sh.saveScheduleFromService(context,rcallback,schedulebo.getScheduleVO());
    }

    void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), Constants.ALARM_REQUEST_CODE, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);


    }

    void setAlarm(){


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * Constants.ALARM_FREQUENCY, pendingIntent);

    }


}
