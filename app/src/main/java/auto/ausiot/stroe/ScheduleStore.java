package auto.ausiot.stroe;

import auto.ausiot.schedule.Schedule;

/**
 * Created by anu on 20/06/19.
 */

public interface ScheduleStore {

    public Schedule load();

    public void save(Schedule sc);
}
