package auto.ausiot.vo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import auto.ausiot.util.Constants;


public class Schedule {

    private String id;
    private String name;
    private String userID;
    private String unitID;
    private String lineID;

    private Map<Days, ScheduleItem> mapSchedule;
    private boolean enabled = true;
    ScheduleType type = ScheduleType.Daily;

    private Date startDate = new Date();
    private Date endDate = new Date(Constants.MAX_END_DATE);;

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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Days, ScheduleItem> getMapSchedule() {
        return mapSchedule;
    }

    public void setMapSchedule(Map<Days, ScheduleItem> mapSchedule) {
        this.mapSchedule = mapSchedule;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Schedule(String id, String name,
                    String userID , String unitID, String lineID,
                    Date startDate , Date endDate ,
                    Map<Days, ScheduleItem> mapSchedule, boolean enabled ,ScheduleType type ){
        this.id = id;
        this.name = name;
        this.mapSchedule = mapSchedule;
        this.enabled = enabled;
        this.userID = userID;
        this.unitID = unitID;
        this.lineID = lineID;

        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }
    public boolean isInitialized(){
        boolean ret = true;
        if (mapSchedule.size() == 0)
            ret = false;
        return ret;
    }

    public void createSchedule(ScheduleItem si){
        for (Days day : Days.values()) {
            mapSchedule.put(day,si);
        }

    }
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

                Date time = new SimpleDateFormat("HH:mm").parse(stime);
                int duration = Integer.parseInt(sduration);
                setSheduleItem(Integer.parseInt(sday),time,duration,Boolean.parseBoolean(senabled));
            }
        }


    }

    public void setSheduleItem(int iday, Date time , int duration, boolean enabled ){
        Days day = Days.get(iday);
        ScheduleItem si = new ScheduleItem("id",time,duration, enabled,null);
        mapSchedule.put(day,si);
    }
}