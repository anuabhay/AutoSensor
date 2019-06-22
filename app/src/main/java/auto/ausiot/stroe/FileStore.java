package auto.ausiot.stroe;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import auto.ausiot.schedule.Schedule;

/**
 * Created by anu on 20/06/19.
 */

public class FileStore implements ScheduleStore {

    Context context;
    String fileName;

    static String DEFUALT_SCHEDULE = "WEEKLY::1,13:00,9,TRUE;2,13:00,9,TRUE;3,13:00,9,TRUE;4,13:00,9,TRUE;5,13:00,9,TRUE;6,13:00,9,TRUE;0,13:00,9,TRUE";

    public FileStore(Context context, String fileName){
        this.context = context;
        this.fileName = fileName;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule load(){
        Schedule sc = new Schedule();
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
            sc.createSheduleFromString(readString);
            fis.close();}

        catch (Exception e) {

        } finally {
            if (fis != null) {
                fis = null;
            }
        }
        if (!sc.isInitialized()){
            try {
                sc.createSheduleFromString(DEFUALT_SCHEDULE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return sc;
   }



    public void save(Schedule sc){
        //Context context = MainActivity.this.getApplicationContext();
        FileOutputStream fos = null;
        try {

            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(sc.dumpSheduleString().getBytes());
            fos.flush();
            fos.close();

        } catch (Exception e) {

        } finally {
            if (fos != null) {
                fos = null;
            }
        }

    }
}
