package auto.ausiot.schedule;

import java.time.Duration;
import java.util.Date;


/**
 * Created by anu on 19/06/19.
 */

public class ScheduleItemBO {
    Date time;
    //Duration duration;
    int duration ;

    boolean enabled = true;

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


    public ScheduleItemBO(Date time , int duration, boolean enabled){
        this.time = time;
        this.duration = duration;
        this.enabled = enabled;
    }
}
