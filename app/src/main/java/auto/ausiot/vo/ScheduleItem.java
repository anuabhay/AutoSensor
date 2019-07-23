package auto.ausiot.vo;

import java.util.Date;


public class ScheduleItem {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getLastTriggered() {
        return lastTriggered;
    }

    public void setLastTriggered(Date lastTriggered) {
        this.lastTriggered = lastTriggered;
    }

    public ScheduleItem(String id, Date time, int duration,
                        boolean enabled, Date lastTriggered )
    {
        this.id = id;
        this.time = time;
        this.duration = duration;

        this.enabled = enabled;
        this.lastTriggered = lastTriggered;
    }
    private String id;

    private Date time;

    private int duration ;
    private boolean enabled = true;
    private Date lastTriggered;
}