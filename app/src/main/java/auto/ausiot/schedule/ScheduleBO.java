package auto.ausiot.schedule;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import auto.ausiot.util.Constants;
import auto.ausiot.vo.Days;
import auto.ausiot.vo.Schedule;
import auto.ausiot.vo.ScheduleItem;
import auto.ausiot.vo.ScheduleType;

/**
 * Created by anu on 19/06/19.
 */

public class ScheduleBO {

    String id;
    String name;

    Map<Days, ScheduleItemBO> mapSchedule = new HashMap<>();
    ScheduleType type = ScheduleType.Daily;

    private String userID;
    private String unitID;
    private String lineID;

    private Date startDate = new Date();
    private Date endDate = new Date(Constants.MAX_END_DATE);;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Days, ScheduleItemBO> getMapSchedule() {
        return mapSchedule;
    }

    public void setMapSchedule(Map<Days, ScheduleItemBO> mapSchedule) {
        this.mapSchedule = mapSchedule;
    }

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ScheduleBO(){

    }

    public boolean isInitialized(){
        boolean ret = true;
        if (mapSchedule.size() == 0)
            ret = false;
        return ret;
    }

//    public ScheduleBO(Date time , int duration , boolean enabled){
//        ScheduleItemBO si = new ScheduleItemBO(time,duration,enabled);
//        createSchedule(si);
//
//    }

    public ScheduleBO(String id, String name,
                      String userID , String unitID, String lineID,
                      Date startDate , Date endDate ,
                      Map<Days, ScheduleItemBO> mapSchedule , ScheduleType type){
        this.id = id;
        this.name = name;
        this.type = type;
        this.mapSchedule = mapSchedule;
        this.userID = userID;
        this.unitID = unitID;
        this.lineID = lineID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void createSchedule(ScheduleItemBO si){
        for (Days day : Days.values()) {
            mapSchedule.put(day,si);
        }

    }

    public void setSheduleItem(int iday, Date time , int duration, boolean enabled ){
        Days day = Days.get(iday);
        ScheduleItemBO si = new ScheduleItemBO(time,duration, enabled);
        mapSchedule.put(day,si);
    }

    public void setSheduleItem(Days day, Date time , int duration, boolean enabled ){
        ScheduleItemBO si = new ScheduleItemBO(time,duration, enabled);
        mapSchedule.put(day,si);
    }

    public Schedule getScheduleVO(){
        Iterator it = mapSchedule.entrySet().iterator();
        Map<Days, ScheduleItem> mapSchedulevo = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ScheduleItemBO bo = ((ScheduleItemBO)pair.getValue());
//            String id, Date time, int duration,
//            boolean enabled, Date lastTriggered
            //Time Zone

            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(bo.getTime());
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date newdate = new Date();
            newdate.setTime(0);
            //newdate.setYear(calendar.get(Calendar.YEAR));
            //newdate.setMonth(calendar.get(Calendar.MONTH));
            //newdate.setDate(calendar.get(Calendar.DATE));
            newdate.setHours(calendar.get(Calendar.HOUR));
            newdate.setMinutes(calendar.get(Calendar.MINUTE));

            ScheduleItem itemvo = new ScheduleItem("id",bo.getTime(),bo.getDuration(),bo.isEnabled(),null);
            mapSchedulevo.put((Days)pair.getKey(),itemvo);
        }
        Schedule svo = new Schedule(id, name, userID , unitID, lineID, startDate, endDate, mapSchedulevo,true, type);
        return svo;
    }

    public static ScheduleBO getScheduleBO(Schedule schedulevo){
        Map<Days, ScheduleItem> mapSchedule = schedulevo.getMapSchedule();
        Iterator it = mapSchedule.entrySet().iterator();
        Map<Days, ScheduleItemBO> mapSchedulebo = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ScheduleItem bo = ((ScheduleItem)pair.getValue());
//            String id, Date time, int duration,
//            boolean enabled, Date lastTriggered
            //Time Zone
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.setTime(bo.getTime());

            ScheduleItemBO itembo = new ScheduleItemBO(calendar.getTime(),bo.getDuration(),bo.isEnabled());
            mapSchedulebo.put((Days)pair.getKey(),itembo);
        }
        ScheduleBO svo = new ScheduleBO(schedulevo.getId(), schedulevo.getName(),
                schedulevo.getUserID(), schedulevo.getUnitID(), schedulevo.getLineID(),
                schedulevo.getStartDate(), schedulevo.getEndDate(),
                mapSchedulebo,schedulevo.getType());
        return svo;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public ScheduleItemBO hasScheduleItem(int iday, Date time) throws ParseException {
        Days day = Days.get(iday);
        ScheduleItemBO si = mapSchedule.get(day);
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

    public  int getDurationForDay(Days day){
        return mapSchedule.get(day).duration;
    }


    public  ScheduleItemBO getScheduleItem(Days day){
        return mapSchedule.get(day);
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
                //Date time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(stime);

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
            //String sTime = ((ScheduleItemBO)pair.getValue()).time.toString();
            String sTime = new SimpleDateFormat("HH:mm").format(((ScheduleItemBO)pair.getValue()).time);
            String sDuration = String.valueOf (   ((ScheduleItemBO)pair.getValue()).duration);
            String sEnabled =  String.valueOf (((ScheduleItemBO)pair.getValue()).enabled);
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

    public ScheduleItemBO getcheduleItem(Days day) throws ParseException {
        ScheduleItemBO si = mapSchedule.get(day);
        return si;
    }
}
