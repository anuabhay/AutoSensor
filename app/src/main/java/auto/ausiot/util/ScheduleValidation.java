package auto.ausiot.util;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import auto.ausiot.autosensor.RepeatScheduleActivity;
import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.vo.Days;

public class ScheduleValidation {
    public static boolean validateSchedules(ScheduleBO sbonew , String unitID, String lineID, StringBuffer msg){
        boolean valid = true;
        // End date is before the current day
        if (TimeIgnoringComparator.before(sbonew.getEndDate(),new Date())){
            msg.append("Schedule End date is already passed ..");
            return false;
        }else if (TimeIgnoringComparator.before(sbonew.getEndDate(),sbonew.getStartDate())){
            msg.append("Schedule End date comes before the start day ..");
            return false;
        }

        List<ScheduleBO> sbolist = RestStore.getScheduleByUnitLine(unitID,lineID);
        msg.append("The following days have overlapping schedules \n");
        for(int i=0; i < sbolist.size();i++){
            if (sbolist.get(i).getId().compareTo(sbonew.getId())!=0){
                ScheduleBO sboex = sbolist.get(i);
                if(TimeIgnoringComparator.before(sboex.getEndDate() ,sbonew.getStartDate())||
                        TimeIgnoringComparator.after(sboex.getStartDate() ,sbonew.getEndDate())){

                }else{
                    Date dt;
                    if (TimeIgnoringComparator.before(sboex.getStartDate(), sbonew.getStartDate())){
                        dt = sboex.getStartDate();


                    }else{
                        dt = sbonew.getStartDate();
                    }
                    int count = 1;
                    boolean exit = false;
                    while (TimeIgnoringComparator.beforeIncudingCurrentDay(dt,sbonew.getEndDate()) &&
                            TimeIgnoringComparator.beforeIncudingCurrentDay(dt,sboex.getEndDate()) &&
                            (count < 7) &&
                            (exit == false)) {

                        int x = dt.getDay();
                        if (compareSchedules(dt,sbonew,sboex) == true){
                            valid = false;
                            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE,dd MMM YYYY", Locale.US);
                            msg.append(sboex.getName() + "   " + sdf2.format(dt) + "\n");
                        }
                        count++;
                        Calendar c = Calendar.getInstance();
                        c.setTime(dt);
                        c.add(Calendar.DATE, 1);
                        dt = c.getTime();
                    }
                }
                //they overlap
            }
        }
        return valid;
    }

    static boolean compareSchedules(Date dt, ScheduleBO sbonew, ScheduleBO sboex) {
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
