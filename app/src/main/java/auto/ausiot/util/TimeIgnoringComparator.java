package auto.ausiot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class TimeIgnoringComparator implements Comparator<Date> {
    public int compare(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean beforeIncudingCurrentDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        boolean ret = false;
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)){
            if (((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) < 0)){
                ret = true;
            }
        }else if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)){
            if (((c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH)) < 0)){
                ret = true;
            }
        }else if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            if (((c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH)) < 0)) {
                ret = true;
            }
        }else if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)){
            ret = true;
        }

        return ret;
    }

    public static boolean before(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        boolean ret = false;
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)){
            if (((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) < 0)){
                    ret = true;
            }
        }else if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)){
            if (((c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH)) < 0)){
                ret = true;
            }
        }else if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            if (((c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH)) < 0)) {
                ret = true;
            }
        }
//        }else if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)){
//            ret = true;
//        }

        return ret;
    }


    public static boolean after(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        boolean ret = false;
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)){
            if (((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) > 0)){
                ret = true;
            }
        }else if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)){
            if (((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) > 0)){
                ret = true;
            }
        }else if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)){
            if (((c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH)) > 0)){
                ret = true;
            }
        }
//        else if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)){
//            ret = true;
//        }

        return ret;
    }

    public static void main( String[] args){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
        String dateInString1 = "Friday, Jun 7, 2013 12:10:56 PM";
        String dateInString2 = "Friday, Jun 7, 2013 12:20:56 PM";

        String dateInString3 = "Friday, Jun 7, 2013 12:10:56 PM";
        String dateInString4 = "Friday, Jun 8, 2013 12:20:56 PM";
        try {

            Date d1 = formatter.parse(dateInString1);
            Date d2 = formatter.parse(dateInString2);
            boolean x = TimeIgnoringComparator.before(d1,d2);

            Date d3 = formatter.parse(dateInString3);
            Date d4 = formatter.parse(dateInString4);
            x = TimeIgnoringComparator.before(d3,d4);



        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
//    public static int compare(Date d1, Date d2) {
//        Calendar c1 = Calendar.getInstance();
//        c1.setTime(d1);
//        Calendar c2 = Calendar.getInstance();
//        c2.setTime(d2);
//        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
//            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
//        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
//            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
//        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
//    }
//}

