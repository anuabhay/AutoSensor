package auto.ausiot.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.widget.TimePicker;

import auto.ausiot.autosensor.R;
import auto.ausiot.util.DateHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    int viewID = 0;
    int minute;
    int hour;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
    //public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, this.hour, this.minute,
                true);
    }

    public void setTime(int minute,int hour){
        //tp.updateTime(minute ,hour);
        this.minute = minute;
        this.hour = hour;
    };

    public void setTime(String str){
        int minute_t = DateHelper.getMinutesFromDateString(str);
        this.minute = minute_t % 60;
        this.hour = minute_t / 60;
        //this.tp.updateTime(1, 10);

        //tp.updateTime(minute / 60, minute % 60);
    }

    public void setID(int viewID)
    {
        this.viewID = viewID;
    }
    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(this.viewID);
        //Set a message for user
        tv.setText("Your chosen time is...\n\n");
        //Display the user changed time on TextView

        String comptime = DateHelper.convertHoursMinutes2HHmm(minute,hourOfDay);

        /*SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        Date date = new Date();
        date.setHours(hourOfDay);
        date.setMinutes(minute);

        String comptime = df.format(date);*/


        tv.setText(comptime);
    }
}
