package auto.ausiot.schedule;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by anu on 19/06/19.
 */

public class Schedule {
    public enum Days {
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;

        public static Days get(int index){
            return Days.values()[index];
        }
    }

    public enum ScheduleType {
        Daily, Weekly
    }


    Map<Days, ScheduleItem> mapSchedule = new HashMap<>();
    ScheduleType type = ScheduleType.Daily;

    public Schedule(){

    }

    public boolean isInitialized(){
        boolean ret = true;
        if (mapSchedule.size() == 0)
            ret = false;
        return ret;
    }
    public Schedule(Date time , int duration , boolean enabled){
        ScheduleItem si = new ScheduleItem(time,duration,enabled);
        createSchedule(si);

    }
    public void createSchedule(ScheduleItem si){
        for (Days day : Days.values()) {
            mapSchedule.put(day,si);
        }

    }

    public void setSheduleItem(int iday, Date time , int duration, boolean enabled ){
        Days day = Days.get(iday);
        ScheduleItem si = new ScheduleItem(time,duration, enabled);
        mapSchedule.put(day,si);
    }

    public void setSheduleItem(Days day, Date time , int duration, boolean enabled ){
        ScheduleItem si = new ScheduleItem(time,duration, enabled);
        mapSchedule.put(day,si);
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public ScheduleItem hasScheduleItem(int iday, Date time) throws ParseException {
        Days day = Days.get(iday);
        ScheduleItem si = mapSchedule.get(day);
        //long diffmints = Duration.between(si.time , time).toMinutes();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String stime = df.format(si.getTime());
        String comptime = df.format(time);
        Date snewitime = df.parse(stime);
        Date compnewtime = df.parse(comptime);


        long diffmints = (snewitime.getTime() - compnewtime.getTime()) / (1000 *60);
        if (( diffmints > 0) || (diffmints < 15)){
            return si;

        }
        return si;
        //return null;
    }

    public  Date getTimeForDay(Days day){
        return mapSchedule.get(day).time;
    }

    public  boolean getEnabledForDay(Days day){
        return mapSchedule.get(day).enabled;
    }

    public  int getDUrationForDay(Days day){
        return mapSchedule.get(day).duration;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    //@RequiresApi(api = Build.VERSION_CODES.O)
    public void createSheduleFromString(String schedule) throws ParseException {
        int itype = schedule.indexOf("::");
        if (itype > 0){
            String stype = schedule.substring(0,itype);
            if (stype.equalsIgnoreCase("WEEKLY") ==true){
                type = ScheduleType.Weekly;
            }else{
                type = ScheduleType.Daily;
            }
            schedule = schedule.substring(itype + 2, schedule.length());
            String delims = "[;]";
            String[] tokens = schedule.split(delims);
            for (int i = 0; i < tokens.length; i++){
                String delim_time = "[,]";
                String[] tokens_time = tokens[i].split(delim_time);
                String sday = tokens_time[0];
                String stime = tokens_time[1];
                String sduration = tokens_time[2];
                String senabled = tokens_time[3];

                //String sDate1="31/12/1998:" + stime;
                Date time = new SimpleDateFormat("HH:mm").parse(stime);

                //String sDate1="31/12/1998:" + stime;
                //Date time = new SimpleDateFormat("dd/MM/yyyy:HH:MM").parse(sDate1);
                //Date time = Date.parse(stime);
                int duration = Integer.parseInt(sduration);
                setSheduleItem(Integer.parseInt(sday),time,duration,Boolean.parseBoolean(senabled));
            }
        }
    }


    public String dumpSheduleString() {
        String dumpString = "";
        if (type == ScheduleType.Weekly){
            dumpString = "WEEKLY::";
        }else{
            dumpString = "DAILY::";
        }
        int count = 0;
        Iterator it = mapSchedule.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //String sTime = ((ScheduleItem)pair.getValue()).time.toString();
            String sTime = new SimpleDateFormat("HH:mm").format(((ScheduleItem)pair.getValue()).time);
            String sDuration = String.valueOf (   ((ScheduleItem)pair.getValue()).duration);
            String sEnabled =  String.valueOf (((ScheduleItem)pair.getValue()).enabled);
            String sDay =  Integer.toString(((Days)pair.getKey()).ordinal());
            if (count == 6) {
                dumpString = dumpString + sDay + "," + sTime + "," + sDuration + "," + sEnabled;
                count++;
            } else{
                dumpString = dumpString + sDay + "," + sTime + "," + sDuration + "," + sEnabled + ";";
                count++;
            }
        }
        return dumpString;
    }

}
