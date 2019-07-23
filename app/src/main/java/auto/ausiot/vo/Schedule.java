package auto.ausiot.vo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class Schedule {

    private String id;

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

    private Map<Days, ScheduleItem> mapSchedule;
    private boolean enabled = true;
    ScheduleType type = ScheduleType.Daily;

    public Schedule(String id, Map<Days, ScheduleItem> mapSchedule,boolean enabled  ){
        this.id = id;
        this.mapSchedule = mapSchedule;
        this.enabled = enabled;
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

}