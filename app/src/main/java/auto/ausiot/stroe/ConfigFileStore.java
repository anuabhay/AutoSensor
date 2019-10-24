package auto.ausiot.stroe;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import auto.ausiot.schedule.AuthBody;
import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.User;
import auto.ausiot.util.Constants;
import auto.ausiot.vo.Schedule;
import auto.ausiot.vo.ScheduleType;
import auto.ausiot.vo.Unit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anu on 20/06/19.
 */

public class ConfigFileStore /*implements ScheduleStore*/ {

    Context context;
    String fileName;

    public static Unit m_unit = null;




    public ConfigFileStore(Context context, String fileName){
        this.context = context;
        this.fileName = fileName;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public Unit load(){
        Unit unit = null;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            // READ STRING OF UNKNOWN LENGTH
            StringBuilder sb = new StringBuilder();
            char[] inputBuffer = new char[2048];
            int l;
            // FILL BUFFER WITH DATA
            while ((l = isr.read(inputBuffer)) != -1) {
                sb.append(inputBuffer, 0, l);
            }
            // CONVERT BYTES TO STRING
            String readString = sb.toString();
            unit = createUnitFromString(readString);
            m_unit = unit;
            fis.close();
            Constants.MQTT_HOST = m_unit.getMqqttUrl();
            Constants.MQTT_USER = m_unit.getMqqttUserID();
            Constants.MQTT_PASSWD = m_unit.getMqqttPassword();

        } catch (Exception e) {

        } finally {
            if (fis != null) {
                fis = null;
            }
        }

        return unit;
    }

    public Unit createUnitFromString(String unitstr) throws ParseException {
        //unitstr = "unit1::http://abc.com/:8080::anu::passowrd";
        StringTokenizer tokenizer = new StringTokenizer(unitstr,";");
        Unit unit = null;
        int count = 0;
        while (tokenizer.hasMoreTokens())
        {
            if (count == 0){
                unit = new Unit(tokenizer.nextToken(),"");
            }
            if (count == 1){
                unit.setMqqttUrl(tokenizer.nextToken());
            }
            if (count == 2){
                unit.setMqqttUserID(tokenizer.nextToken());
            }
            if (count == 3){
                unit.setMqqttPassword(tokenizer.nextToken());
            }
            count++;
        }
        return unit;

    }

    public void save(String unitstr){
        // GRA000::soldier.cloudmqtt.com:14397::kipxjpmj::bqdexBJKLHa1
        //Context context = MainActivity.this.getApplicationContext();
        FileOutputStream fos = null;
        try {

            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            //String dumpString = unit.getId() + "::" + unit.getMqqttUrl() + "::" + unit.getMqqttUserID() + "::" + unit.getMqqttPassword();
            fos.write(unitstr.getBytes());
            fos.flush();
            m_unit = createUnitFromString(unitstr);
            fos.close();


        } catch (Exception e) {

        } finally {
            if (fos != null) {
                fos = null;
            }
        }

    }

    public void delete(){
        //Context context = MainActivity.this.getApplicationContext();
        FileOutputStream fos = null;
        try {

            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            //String dumpString = unit.getId() + "::" + unit.getMqqttUrl() + "::" + unit.getMqqttUserID() + "::" + unit.getMqqttPassword();
            fos.write("".getBytes());
            fos.flush();
            m_unit = null;
            fos.close();


        } catch (Exception e) {

        } finally {
            if (fos != null) {
                fos = null;
            }
        }

    }

}
