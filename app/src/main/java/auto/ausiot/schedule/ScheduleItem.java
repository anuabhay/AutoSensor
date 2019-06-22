package auto.ausiot.schedule;

import java.time.Duration;
import java.util.Date;


/**
 * Created by anu on 19/06/19.
 */

public class ScheduleItem {
    Date time;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    //Duration duration;
    int duration ;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    boolean enabled = true;

    public ScheduleItem(Date time , int duration, boolean enabled){
        this.time = time;
        this.duration = duration;
        this.enabled = enabled;
    }
}
