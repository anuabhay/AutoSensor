package auto.ausiot.schedule;

import android.content.Context;

import auto.ausiot.stroe.FileStore;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.stroe.ScheduleStore;
import auto.ausiot.vo.Schedule;

/**
 * Created by anu on 20/06/19.
 */

public class ScheduleHelper {
    public ScheduleBO loadSchedule(Context context){
        ScheduleStore sstore = new FileStore(context, "schedulestore.txt");
        return sstore.load();
    }

    public void saveSchedule(Context context, ScheduleBO sc){
        ScheduleStore sstore = new FileStore(context, "schedulestore.txt");
        sstore.save(sc);
    }

    public void loadScheduleFromService(String sensorID, Context context , RestCallBack rcalback){
        RestStore rs = new RestStore(context);
        rs.loadfromservice(sensorID, rcalback);
    }

    public void saveScheduleFromService(Context context, RestCallBack rcalback ,Schedule sc){
        RestStore rs = new RestStore(context);
        rs.savefromservice(sc,rcalback);
    }

    public void deleteScheduleFromService(Context context, RestCallBack rcalback, String sensorID ){
        RestStore rs = new RestStore(context);
        rs.deletefromservice(sensorID,rcalback);
    }

    public void addSensorfromservice(Context context, RestCallBack rcalback ,String sensorID){
        RestStore rs = new RestStore(context);
        rs.addSensorfromservice(sensorID,rcalback);
    }


    public void login(String useremail, String password, Context context , RestCallBack rcalback){
        RestStore rs = new RestStore(context);
        rs.login(useremail, password ,rcalback);
    }

    public void register(User user, Context context , RestCallBack rcalback){
        RestStore rs = new RestStore(context);
        rs.register(user ,rcalback);
    }

    public void getUser(String useremail, Context context , RestCallBack rcalback){
        RestStore rs = new RestStore(context);
        rs.getUser(useremail, rcalback);
    }
}
