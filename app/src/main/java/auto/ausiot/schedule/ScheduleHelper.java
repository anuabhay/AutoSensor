package auto.ausiot.schedule;

import android.content.Context;

import auto.ausiot.stroe.FileStore;
import auto.ausiot.stroe.ScheduleStore;

/**
 * Created by anu on 20/06/19.
 */

public class ScheduleHelper {
    public Schedule loadSchedule(Context context){
        ScheduleStore sstore = new FileStore(context, "schedulestore.txt");
        return sstore.load();
    }

    public void saveSchedule(Context context, Schedule sc){
        ScheduleStore sstore = new FileStore(context, "schedulestore.txt");
        sstore.save(sc);
    }
}
