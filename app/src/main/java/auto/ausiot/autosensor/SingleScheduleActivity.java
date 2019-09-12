package auto.ausiot.autosensor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
//import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;



import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.ui.TimePickerFragment;
import auto.ausiot.util.UserConfig;
import auto.ausiot.util.DateHelper;
import auto.ausiot.util.Logger;
import auto.ausiot.util.ScheduleValidation;
import auto.ausiot.vo.Days;
import auto.ausiot.vo.Schedule;
import auto.ausiot.vo.ScheduleItem;
import auto.ausiot.vo.ScheduleType;

public class SingleScheduleActivity extends AppCompatActivity {
    private TextView mTextMessage;
    Context context ;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    private Logger logger = null;
    ScheduleBO schedulebo = null;
    //AppConfig config ;
    //private RadioGroup radioGroupDays ;
    //@TODO THis need to change for user ID to support multiple sensors
    public String unitID;
    public String lineID = "1";
    Button saveBtn;
    LinearLayout mainLayout;
    //RadioButton line_1;
    //RadioButton  line_2;
    private static int scheduleID = 1;
    TextView txtStart;
    //final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
             switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(SingleScheduleActivity.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
//                case R.id.navigation_dashboard:
//                    i = new Intent(SingleScheduleActivity.this,ManageSchedulesActivity.class);
//                    i.putExtra("lineID", lineID);
//                    i.putExtra("unitID", unitID);
//                    startActivity(i);
//                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(SingleScheduleActivity.this,Disclaimer.class);
                    startActivity(i);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_schedule);

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
                    si,true , ScheduleType.Daily);
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
        context = SingleScheduleActivity.this.getApplicationContext();


        EditText txtName = (EditText) findViewById(R.id.text_input_name);
        txtName.setText(schedulebo.getName());

        TextView txtNameExp = (TextView) findViewById(R.id.text_input_name_explanation);
        txtNameExp.setText("Unit " + unitID + " and Line " + lineID);

        //txtDes.setText("Schedule for Unit: " + unitID + " and Line: " + lineID);
        //@TODO Depricated
        //unitID = config.readFirstConfig();
        UserConfig.checkInitialized(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mainLayout = findViewById(R.id.lo_all_content);

        TextView tvduration = (TextView) findViewById(R.id.chronometer2);
        tvduration.setText("00:00");
        TextView tvtime = (TextView) findViewById(R.id.timePicker);
        tvtime.setText("00:00");
        initListners();
        logger = new Logger(context);
        setDatePickers();
        onDayChange();
    }

    private void setDatePickers(){
        final Calendar calendar_start = Calendar.getInstance();
        calendar_start.setTime(schedulebo.getStartDate());
        txtStart = (TextView) findViewById(R.id.text_startdate);

        txtStart.setText(DateHelper.getPrintableDate(calendar_start.getTime()));
        txtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SingleScheduleActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        Calendar cl = Calendar.getInstance();
                        cl.set(arg1,arg2,arg3);
                        schedulebo.setStartDate(cl.getTime());
                        schedulebo.setEndDate(cl.getTime());
                        txtStart.setText(DateHelper.getPrintableDate(cl.getTime()));
                    }

                }, calendar_start.get(Calendar.YEAR), calendar_start.get(Calendar.MONTH), calendar_start.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                //showCalendar();

            }
        });

        final Calendar calendar_end = Calendar.getInstance();
        calendar_end.setTime(schedulebo.getEndDate());
        final TextView txtEnd = (TextView) findViewById(R.id.text_enddate);
        txtEnd.setText(DateHelper.getPrintableDate(calendar_end.getTime()));
        txtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SingleScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        Calendar cl = Calendar.getInstance();
                        cl.set(arg1,arg2,arg3);
                        schedulebo.setEndDate(cl.getTime());
                        txtEnd.setText(DateHelper.getPrintableDate(cl.getTime()));
                        //txtEnd.setText(new Integer(arg1).toString() + new Integer(arg2).toString() +new Integer(arg3).toString());
                        // arg1 = year
                        // arg2 = month
                        // arg3 = dayarg
                    }

                }, calendar_end.get(Calendar.YEAR), calendar_end.get(Calendar.MONTH), calendar_end.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

   private void showCalendar(){
        OnDaysSelectionListener odsl = new OnDaysSelectionListener() {
            @Override
            public void onDaysSelected(List<Day> selectedDays) {
                Calendar cl = Calendar.getInstance();
                cl = selectedDays.get(0).getCalendar();
//                        cl.set(arg1,arg2,arg3);
                schedulebo.setStartDate(cl.getTime());
                schedulebo.setEndDate(cl.getTime());
                txtStart.setText(DateHelper.getPrintableDate(cl.getTime()));
            }
        };

        CalendarDialog cld = new CalendarDialog(this, odsl);
        cld.show();
        cld.setSelectionType(SelectionType.MULTIPLE);
        cld.setCalendarOrientation(LinearLayoutManager.HORIZONTAL);
        cld.setWeekendDayTextColor(cld.getDayTextColor());
        int textColor = Color.parseColor("#519674");
        cld.setMonthTextColor(textColor);
        cld.setCurrentDayTextColor(textColor);

        Date dt = new Date();
        Day dy = new Day(dt);
        List<Day> lst = new ArrayList<Day>();
        lst.add(dy);
        odsl.onDaysSelected(lst);
    }
    private Days getDayFromChveckID(int checkedId) {
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

    private void onDayChange(){
        //Days day = getDayFromCheckID(checkedId);
        Date dt = schedulebo.getStartDate();

        Days day = Days.get(dt.getDay());

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

        //setSelectionBanner();
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
                    Date date = null;
                    try {
                        date = DateHelper.getDateFromPrintableDate(txtStart.getText().toString());
                        Days day = Days.get(date.getDay());
                        int xx = date.getHours();
                        schedulebo.getScheduleItem(day).setEnabled(isChecked);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
                //Days day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());
                //Date dt = DateHelper.getDateFromString(s.toString());
                Date dttoday = null;
                try {
                    dttoday = DateHelper.getDateFromPrintableDate(txtStart.getText().toString());
                    Days day = Days.get(dttoday.getDay() );
                    Date dt = DateHelper.getDateFromString(s.toString());
                    schedulebo.getScheduleItem(day).setTime(dt);
                    saveBtn.setText("Save");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                //Days day = getDayFromCheckID(radioGroupDays.getCheckedRadioButtonId());

                Date dt = null;
                try {
                    dt = DateHelper.getDateFromPrintableDate(txtStart.getText().toString());
                    Days day = Days.get(dt.getDay() );
                    int minutes = DateHelper.getMinutesFromDateString(s.toString());
                    schedulebo.getScheduleItem(day).setDuration(minutes);
                    saveBtn.setText("Save");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
        new AlertDialog.Builder(SingleScheduleActivity.this)
                .setTitle(getResources().getString(R.string.warning_title_schedule_modify))
                .setMessage(getResources().getString(R.string.warning_detail_schedule_modify))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveScheduleData();;
                    }
                }).setNegativeButton("Cancel", null).show();;
        //saveScheduleData();
    }

    public Date getSheduleTimeFromUI(){

        TextView tpTime = (TextView) findViewById(R.id.timePicker);
        String time = tpTime.getText().toString();
        Date dt = DateHelper.getDateFromString(time);
        return dt;
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
        schedulebo.setType(ScheduleType.Daily);
        disableOtherDays();
        if (validateSchedules(schedulebo)) {
            sh.saveScheduleFromService(context, rcallback, schedulebo.getScheduleVO());
        }
    }

    void disableOtherDays(){
        try {
            Date dt = DateHelper.getDateFromPrintableDate(txtStart.getText().toString());
            Days sday = Days.get(dt.getDay());
            for (Days day : Days.values()) {
                if (day != sday)
                    schedulebo.getcheduleItem(day).setEnabled(false);
                System.out.println(day);
            }
        }catch(ParseException e){

        }
    }

    boolean validateSchedules(ScheduleBO sbonew){
        boolean valid  = true;
        StringBuffer msg = new StringBuffer ("");
        valid = ScheduleValidation.validateSchedules(sbonew,unitID,lineID,msg);
        if (valid == false){
            new AlertDialog.Builder(SingleScheduleActivity.this)
                    .setTitle("Overlaping Schedules")
                    .setMessage(msg.toString())
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
