package auto.ausiot.util;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

import auto.ausiot.autosensor.InitViewer;
import auto.ausiot.stroe.RestStore;

public class UserConfig {
    public static String checkInitialized(AppCompatActivity activity){
        String unitID = null;
        if((RestStore.units != null) && (RestStore.units.size() != 0)){
            unitID = RestStore.units.get(0).getId();
        }
        if (unitID == null){
            Intent i = new Intent(activity, InitViewer.class);
            activity.startActivity(i);

        }
        return unitID;
    }

    public static boolean isInitialized(){
        if(RestStore.units == null) {
            return false;
        }else if (RestStore.units.size() == 0){
            return false;
        }else{
            return true;
        }
    }

    public static String getFirstUnit(){
        if((RestStore.units != null) && (RestStore.units.size() != 0)){
            return RestStore.units.get(0).getId();
        }else{
            return null;
        }
    }

}
