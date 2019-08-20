package auto.ausiot.autosensor;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.ui.TimePickerFragment;
import auto.ausiot.util.UserConfig;
import auto.ausiot.util.Constants;
import auto.ausiot.util.DateHelper;
import auto.ausiot.util.Logger;
import auto.ausiot.util.ScheduleValidation;
import auto.ausiot.vo.Days;
import auto.ausiot.vo.Schedule;
import auto.ausiot.vo.ScheduleItem;
import auto.ausiot.vo.ScheduleType;

public class RepeatScheduleActivity extends AppCompatActivity {
    private TextView mTextMessage;
    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    private Logger logger = null;
    ScheduleBO schedulebo = null;
    //AppConfig config ;
    private RadioGroup radioGroupDays ;
    //@TODO THis need to change for user ID to support multiple sensors
    public String unitID;
    public String lineID = "1";
    Button saveBtn;
    LinearLayout mainLayout;
    //RadioButton line_1;
    //RadioButton  line_2;
    private static int scheduleID = 1;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
             switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(RepeatScheduleActivity.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    i = new Intent(RepeatScheduleActivity.this,ManageSchedulesActivity.class);
                    i.putExtra("lineID", lineID);
                    i.putExtra("unitID", unitID);
                    startActivity(i);
                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(RepeatScheduleActivity.this,InitViewer.class);
                    startActivity(i);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_schedule);

        String sid = getIntent().getStringExtra("scheduleid");
        lineID = getIntent().getStringExtra("lineID");
        unitID = getIntent().getStringExtra("unitID");
        boolean isnew = getIntent().getBooleanExtra("schedulenew",false);

        if (isnew == false) {
            schedulebo = RestStore.getScheduleByID(sid);
        }else{
            int id = getIntent().getIntExtra("schedule_index",0);
            Map<Days, ScheduleItem> si = new HashMap<>();
            sid = RestStore.user.getId() + "_"+ unitID + "_"+ lineID +"_" + sid;
            Schedule defaultschedule = new Schedule( sid, "Schedule -" + new Integer(id).toString(),
                    RestStore.user.getId() , unitID, lineID,
                    new Date() , new Date() ,
                    si,true , ScheduleType.Weekly);
            //@TODO Move to constant file
            String schedule = "WEEKLY::1,00:00,0,TRUE;2,00:00,0,TRUE;3,00:00,0,TRUE;4,00:00,0,TRUE;5,00:00,0,TRUE;6,00:00,0,TRUE;0,00:00,0,TRUE";
            try {
                defaultschedule.createSheduleFromString(schedule);
                schedulebo =ScheduleBO.getScheduleBO(defaultschedule);
                int x = 1;
            } catch (ParseException  e) {
                e.printStackTrace();
            }
        }
        //Add Icon to Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_1_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Resources res = getResources();
        context = RepeatScheduleActivity.this.getApplicationContext();
        //config = new AppConfig(RepeatScheduleActivity.this.getApplicationContext());

        EditText txtName = (EditText) findViewById(R.id.text_input_name);
        txtName.setText(schedulebo.getName());
        //txtDes.setText("Schedule for Unit: " + unitID + " and Line: " + lineID);
        //@TODO Depricated
        //unitID = config.readFirstConfig();
        //checkInitialized();
        UserConfig.checkInitialized(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mainLayout = findViewById(R.id.lo_all_content);

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
        initListners();
        logger = new Logger(context);
        setDatePickers();
        setCheckBoxForDay();
    }

    private void setCheckBoxForDay(){
        RadioButton b = (RadioButton) findViewById(R.id.radio_2);
        b.setChecked(true);
        Date dt = schedulebo.getStartDate();
        Days day = Days.get(dt.getDay());
        onDayChange(day.ordinal());
    }
    private void setDatePickers(){
        final Calendar calendar_start = Calendar.getInstance();
        calendar_start.setTime(schedulebo.getStartDate());
        final TextView txtStart = (TextView) findViewById(R.id.text_startdate);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd");

        txtStart.setText(sdf.format(calendar_start.getTime()));
        txtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RepeatScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        Calendar cl = Calendar.getInstance();
                        cl.set(arg1,arg2,arg3);
                        schedulebo.setStartDate(cl.getTime());
                        txtStart.setText(sdf.format(cl.getTime()));
                    }

                }, calendar_start.get(Calendar.YEAR), calendar_start.get(Calendar.MONTH), calendar_start.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        final Calendar calendar_end = Calendar.getInstance();
        calendar_end.setTime(schedulebo.getEndDate());
        final TextView txtEnd = (TextView) findViewById(R.id.text_enddate);
        txtEnd.setText(sdf.format(calendar_end.getTime()));
        txtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RepeatScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        Calendar cl = Calendar.getInstance();
                        cl.set(arg1,arg2,arg3);
                        schedulebo.setEndDate(cl.getTime());
                        txtEnd.setText(sdf.format(cl.getTime()));
                    }

                }, calendar_end.get(Calendar.YEAR), calendar_end.get(Calendar.MONTH), calendar_end.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
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

        setSelectionBanner();
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

        EditText txtName = (EditText) findViewById(R.id.text_input_name);

        addTextWatchers(tvDuration, tvDate , txtName);

    }

    /**
     * This is to capture Duration and Time
     *
     * @param tvDuration
     * @param tvDate
     */
    private void addTextWatchers(TextView tvDuration , TextView tvDate , EditText tvName)
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

        TextWatcher twName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                schedulebo.setName(s.toString());
                saveBtn.setText("Save");
            }

        };
        tvName.addTextChangedListener(twName);

    }


    public void onButtonClick(Button view) throws MqttException, URISyntaxException {
        view.startAnimation(buttonClick);
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

    void saveScheduleData(){

        ScheduleHelper sh = new ScheduleHelper();
        RestCallBack rcallback =  new RestCallBack() {
            @Override
            public void onResponse(Object obj) {
                RestStore.addBO(schedulebo);
                saveBtn.setText("Saved");
            }

            @Override
            public void onResponse(String s,String user) {
                int x = 1;
            }

            @Override
            public void onFailure() {

            }

        };
        schedulebo.setType(ScheduleType.Weekly);
        if (validateSchedules(schedulebo)) {
            sh.saveScheduleFromService(context, rcallback, schedulebo.getScheduleVO());
        }
    }

    void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), Constants.ALARM_REQUEST_CODE, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);


    }

    void setAlarm(String unitID){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * Constants.ALARM_FREQUENCY, pendingIntent);

    }


    void setSelectionBanner(){
        String label = "";
        TextView tvselection = (TextView) findViewById(R.id.textBanner_Selection);
        radioGroupDays = (RadioGroup) findViewById(R.id.radioDays);
        Days day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());

        label = "You are modifying Schedule for " + day.name();
        tvselection.setText(label);
    }

    boolean validateSchedules(ScheduleBO sbonew){
//        boolean valid = true;
//        List<ScheduleBO> sbolist = RestStore.getScheduleByUnitLine(unitID,lineID);
//        for(int i=0; i < sbolist.size();i++){
//            if (sbolist.get(i).getId().compareTo(sbonew.getId())!=0){
//                ScheduleBO sboex = sbolist.get(i);
//                if(TimeIgnoringComparator.before(sboex.getEndDate() ,sbonew.getStartDate())||
//                        TimeIgnoringComparator.after(sboex.getStartDate() ,sbonew.getEndDate())){
//
//                }else{
//                    Date dt;
//                    if (TimeIgnoringComparator.before(sboex.getStartDate(), sbonew.getStartDate())){
//                        dt = sboex.getStartDate();
//
//
//                    }else{
//                        dt = sbonew.getStartDate();
//                    }
//                    int count = 1;
//                    boolean exit = false;
//                    String msg = "Check the schedule for ";
//                    while (TimeIgnoringComparator.beforeIncudingCurrentDay(dt,sbonew.getEndDate()) &&
//                            TimeIgnoringComparator.beforeIncudingCurrentDay(dt,sboex.getEndDate()) &&
//                            (count < 7) &&
//                            (exit == false)) {
//
//                        int x = dt.getDay();
//                        if (compareSchedules(dt,sbonew,sboex) == true){
//                            valid = false;
//                            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE");
//                            msg = msg + sboex.getName() + "  : " + sdf2.format(dt) + "\n";
//                        }
//                        count++;
//                        Calendar c = Calendar.getInstance();
//                        c.setTime(dt);
//                        c.add(Calendar.DATE, 1);
//                        dt = c.getTime();
//                    }
//
//                    if (valid == false){
//                         new AlertDialog.Builder(RepeatScheduleActivity.this)
//                            .setTitle("Overlaping Schedules")
//                            .setMessage(msg)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                     return;
//                                }
//                             }).show();/*.setNegativeButton("No", null).show();*/
//                    }
//                }
//               //they overlap
//            }
//        }
//        return valid;
        boolean valid  = true;
        //String msg = "";
        StringBuffer msg = new StringBuffer ("");
        valid = ScheduleValidation.validateSchedules(sbonew,unitID,lineID,msg);
        if (valid == false){
                new AlertDialog.Builder(RepeatScheduleActivity.this)
                   .setTitle("Overlaping Schedules")
                   .setMessage(msg.toString())
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                         return;
                   }
                }).show();/*.setNegativeButton("No", null).show();*/
        }
        return valid;
    }

    public boolean compareSchedules(Date dt, ScheduleBO sbonew, ScheduleBO sboex) {
        boolean ret = false;
         try {
            // If disable dont compare
             if (( sbonew.getcheduleItem( Days.get(dt.getDay() )).isEnabled() == false ) ||
                     ( sboex.getcheduleItem( Days.get(dt.getDay())).isEnabled() == false )){
                    return false;
             }
            Date newStart = sbonew.getcheduleItem( Days.get(dt.getDay())).getTime();
            Date exStart = sboex.getcheduleItem( Days.get(dt.getDay())).getTime();
            int newGap = sbonew.getcheduleItem( Days.get(dt.getDay() )).getDuration();
            int exGap = sboex.getcheduleItem( Days.get(dt.getDay() )).getDuration();
            Calendar newcal = GregorianCalendar.getInstance(); // creates a new calendar instance
            newcal.setTime(newStart);   // assigns calendar to given date

            Calendar excal = GregorianCalendar.getInstance(); // creates a new calendar instance
            excal.setTime(exStart);   // assigns calendar to given date

            int newStartM = newcal.get(Calendar.HOUR_OF_DAY) * 60 +  newcal.get(Calendar.MINUTE);
            int exStartM = excal.get(Calendar.HOUR_OF_DAY) * 60 +  excal.get(Calendar.MINUTE) ;

            int newEndM = newStartM + newGap;
            int exEndM = exStartM + exGap;

            if((exEndM < newStartM) || (exStartM > newEndM)){

            }else{
                ret = true;
            }
            return ret;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ret;

        }
}
