package auto.ausiot.stroe;

import auto.ausiot.schedule.ScheduleBO;

/**
 * Created by anu on 20/06/19.
 */

public interface ScheduleStore {

    public ScheduleBO load();

    public void save(ScheduleBO sc);
}
