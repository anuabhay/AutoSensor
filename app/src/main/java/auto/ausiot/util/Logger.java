package auto.ausiot.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by anu on 23/06/19.
 */



public class Logger {

    Logger logger = null;
    Context context = null;
    public static String fileName = "LOG.TXT";

    public Logger(Context context) {
        this.context = context;
    }

    public String[] readLog(){
        ArrayList<String> arrayList = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        FileInputStream fis = null;
        BufferedReader reader;
        try {
              fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line = br.readLine()) != null) {
                arrayList.add(line);
            }

        } catch (Exception e) {
                int x = 1;
        } finally {
            if (fis != null) {
              fis = null;
            }
        }

        return  arrayList.toArray(new String[arrayList.size()]);
    }


    public void log(String log){
        FileOutputStream fos = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        log = dateFormat.format(date) + " :: " + log + "\n";
        try {

            fos = context.openFileOutput(fileName, context.MODE_APPEND  | Context.MODE_PRIVATE);
            fos.write(log.getBytes() );
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
