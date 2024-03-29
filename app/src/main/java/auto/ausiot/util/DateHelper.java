package auto.ausiot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by anu on 22/06/19.
 */

public class DateHelper {

    public static String convertMinutes2HHmm(int minitues){
        Date date = new Date();
        date.setMinutes(minitues);
        date.setHours(0);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        return df.format(date);


    }

    public static String convertHoursMinutes2HHmm(int minitues, int hours){
        Date date = new Date();
        date.setMinutes(minitues);
        date.setHours(hours);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        return df.format(date);


    }

    public static int getMinutesFromDateString(String duration){
        int mints = 0;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        try {
            Date snewitime = df.parse(duration);
            mints = snewitime.getMinutes() + snewitime.getHours() * 60;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mints;

    }

    public static Date getDateFromString(String datestr){
        Date snewitime = null;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        try {
            snewitime = df.parse(datestr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return snewitime;
    }

    public static String getPrintableDate(Date date){
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        return sdf.format(date);
    }

    public static Date getDateFromPrintableDate(String s) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        return sdf.parse(s);
     }

    public static Date getEndDate(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.YEAR,2100);
        Date endDate = c.getTime();
        return endDate;
    }
}
