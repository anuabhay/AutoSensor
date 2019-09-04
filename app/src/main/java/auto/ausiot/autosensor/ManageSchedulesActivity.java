package auto.ausiot.autosensor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.util.UserConfig;
import auto.ausiot.util.Constants;
import auto.ausiot.vo.Schedule;

/**
 * A login screen that offers login via email/password.
 */
public class ManageSchedulesActivity extends AppCompatActivity implements ScheduleLineFragment.OnFragmentInteractionListener
                            , AdapterView.OnItemSelectedListener {

    private String unitID = null;
    private String lineID = "1";
    private static int scheduleID = 1;
    Context context ;
    List<Fragment> activeCenterFragments = new ArrayList<Fragment>();

    List<ScheduleBO> schedulebo = new ArrayList<ScheduleBO>();
    private int scheduleCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_schedules);
        // Set up the login form.

        unitID = UserConfig.checkInitialized(this);
        //Add Icon to Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_1_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        ImageButton addButton = (ImageButton) findViewById(R.id.button_add_schedule);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scheduleCount < Constants.MAX_SCHEDULE_COUNT) {
                    Spinner type = (Spinner) findViewById(R.id.spinner_schedule_type);
                    if (type.getSelectedItemId() == 0){
                        loadRepeatEdit();
                    }else{
                        loadSingleEdit();
                    }
                } else{
                    new AlertDialog.Builder(ManageSchedulesActivity.this)
                            .setTitle(getResources().getString(R.string.warning_title_max_schedule))
                            .setMessage(getResources().getString(R.string.warning_detail_max_schedule))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();/*.setNegativeButton("No", null).show();*/

                }
            }
        });
        context = getApplicationContext();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        poulateSpinners();
        //loadUnits();
        //loadScheduleData();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(ManageSchedulesActivity.this,MonitorActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    i = new Intent(ManageSchedulesActivity.this,Disclaimer.class);
                    startActivity(i);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        poulateSpinners();
    }

    void loadRepeatEdit(){
        Intent i = new Intent(ManageSchedulesActivity.this,RepeatScheduleActivity.class);
        Bundle bundle = new Bundle();
        String uniqueID = UUID.randomUUID().toString();
        long seconds = System.currentTimeMillis() / 1000l;

        i.putExtra("schedule_index", scheduleCount);
        i.putExtra("scheduleid", Long.toString(seconds));
        i.putExtra("schedulenew", true);
        i.putExtra("lineID", lineID);
        i.putExtra("unitID", unitID);
        startActivity(i);
    }

    void loadSingleEdit(){
        Intent i = new Intent(ManageSchedulesActivity.this,SingleScheduleActivity.class);
        Bundle bundle = new Bundle();
        String uniqueID = UUID.randomUUID().toString();
        long seconds = System.currentTimeMillis() / 1000l;

        i.putExtra("schedule_index", scheduleCount);
        i.putExtra("scheduleid", Long.toString(seconds));
        i.putExtra("schedulenew", true);
        i.putExtra("lineID", lineID);
        i.putExtra("unitID", unitID);
        startActivity(i);
    }
    void addScheduleFragments(List<Schedule> sl){
        FragmentManager fm = getSupportFragmentManager();
        removeActiveCenterFragments(fm);
        for (int i = 0; i < sl.size() ; i++) {
            Fragment oldFragment = fm.findFragmentByTag("schedule_list_" + new Integer(i).toString());
            //if (oldFragment == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragment1 = ScheduleLineFragment.newInstance(sl.get(i).getId(), unitID,lineID);
                activeCenterFragments.add(fragment1);
                int resID = getResources().getIdentifier("schedule_line_" + new Integer(i).toString(), "id", getPackageName());
                ft.add(resID, fragment1, "schedule_list_" + new Integer(i).toString());
                ft.commit();
            //}
        }
    }

    private void removeActiveCenterFragments(FragmentManager fm) {
        if (activeCenterFragments.size() > 0) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment activeFragment : activeCenterFragments) {
                ft.remove(activeFragment);
            }
            activeCenterFragments.clear();
            ft.commit();
        }
    }

    void poulateSpinners(){
        String[] lineNames={"1","2"};
        Spinner spin_line = (Spinner) findViewById(R.id.spinner_lines);
        spin_line.setOnItemSelectedListener(this);

        ArrayAdapter aal = new ArrayAdapter(this,R.layout.spinner_item,lineNames);
        aal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_line.setAdapter(aal);

        String[] unitNames = new String[RestStore.units.size()];
        for (int i = 0; i < RestStore.units.size(); i++){
            unitNames[i] = RestStore.units.get(i).getId();
        }
        Spinner spin_unit = (Spinner) findViewById(R.id.spinner_units);
        spin_unit.setOnItemSelectedListener(this);

        ArrayAdapter aau = new ArrayAdapter(this,R.layout.spinner_item,unitNames);
        aau.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_unit.setAdapter(aau);

        String lineID_1 = getIntent().getStringExtra("lineID");
        String unitID_1 = getIntent().getStringExtra("unitID");
        if(lineID_1 != null){
            spin_line.setSelection(Arrays.asList(lineNames).indexOf(lineID_1));
            lineID = lineID_1;
        }
        if(unitID_1 != null){
            spin_unit.setSelection(Arrays.asList(unitNames).indexOf(unitID_1));
            unitID = unitID_1;
        }

        String[] scheduleTypes={"Recurring","Daily"};
        Spinner spin_types = (Spinner) findViewById(R.id.spinner_schedule_type);
        spin_types.setOnItemSelectedListener(this);

        ArrayAdapter aat = new ArrayAdapter(this,R.layout.spinner_item,scheduleTypes);
        aat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //aat.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spin_types.setAdapter(aat);
    }

//    void loadUnits(){
//        final ScheduleHelper sh = new ScheduleHelper();
//        RestCallBack rcallback =  new RestCallBack() {
//            @Override
//            public void onResponse(Object obj) {
//                poulateSpinners();
//            }
//            @Override
//            public void onResponse(String token, String user) {
//
//            }
//            @Override
//            public void onFailure() {
//                ;
//            }
//
//        };
//
//        sh.getUnits(RestStore.user.getId(),null,rcallback);
//
//
//    }
//
//     void   loadScheduleData(){
//        //ScheduleBO sc = new ScheduleBO();
//        //String schedule = "WEEKLY::1,13:00,PT9M,FALSE;2,13:00,PT9M,TRUE;3,13:00,PT9M,TRUE;4,13:00,PT9M,FALSE;5,13:00,PT9M,TRUE;6,13:00,PT9M,TRUE;0,13:00,PT9M,TRUE";
//
//        ScheduleHelper sh = new ScheduleHelper();
//        //ScheduleBO sc  = sh.loadSchedule(context);
//
//
//        RestCallBack rcallback =  new RestCallBack() {
//            @Override
//            public void onResponse(Object obj) {
////                List<Schedule> sl = (List<Schedule>)obj;
////                addScheduleFragments(sl);
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//
//            @Override
//            public void onResponse(String s,String user) {
//                int x = 1;
//            }
//
//        };
//        sh.getUserSchedules(RestStore.user.getId(), context,rcallback);
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        if(RestStore.userSchedules != null) {
            scheduleCount = 1;
            if (arg0.getId() == R.id.spinner_units) {
                unitID = arg0.getItemAtPosition(position).toString();
//                List<Schedule> sl = new ArrayList();
//                for (int i = 0; i < RestStore.userSchedules.size(); i++) {
//                    if (RestStore.userSchedules.get(i).getUnitID().compareTo(unitID) == 0) {
//                        sl.add(RestStore.userSchedules.get(i));
//                    }
//                }
//                addScheduleFragments(sl);
            } else if (arg0.getId() == R.id.spinner_lines) {
                lineID = arg0.getItemAtPosition(position).toString();
//                List<Schedule> sl = new ArrayList();
//                for (int i = 0; i < RestStore.userSchedules.size(); i++) {
//                    if (RestStore.userSchedules.get(i).getLineID().compareTo(lineID) == 0) {
//                        sl.add(RestStore.userSchedules.get(i));
//                    }
//                }
//                addScheduleFragments(sl);
            }

            List<Schedule> sl = new ArrayList();
            for (int i = 0; i < RestStore.userSchedules.size(); i++) {
                if ((RestStore.userSchedules.get(i).getUnitID().compareTo(unitID) == 0) &&
                        (RestStore.userSchedules.get(i).getLineID().compareTo(lineID) == 0)){
                    sl.add(RestStore.userSchedules.get(i));
                    scheduleCount++;
                }
            }
            addScheduleFragments(sl);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }

}

