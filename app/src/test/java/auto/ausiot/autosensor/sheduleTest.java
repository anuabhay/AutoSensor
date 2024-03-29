package auto.ausiot.autosensor;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleItemBO;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class sheduleTest {
    @Test
    public void create_shedule_from_string() throws Exception {
        ScheduleBO c = new ScheduleBO();
        String schedule = "WEEKLY::1,13:00,9,TRUE;2,13:00,9,TRUE;3,13:00,9,TRUE;4,13:00,9,TRUE;5,13:00,9,TRUE;6,13:00,9,TRUE;0,13:00,9,TRUE";
        c.createSheduleFromString(schedule);
    }

    @Test
    public void dump_shedule_string() throws Exception {
        String sDate1="31/12/1998:" + "13:25";
        Date time = new SimpleDateFormat("dd/MM/yyyy:HH:mm").parse(sDate1);

        String s = new SimpleDateFormat("HH:mm").format(time);
       // time.getHours() + time.getMinutes()


        ScheduleBO c = new ScheduleBO();
        String schedule = "WEEKLY::1,13:00,9,TRUE;2,13:00,9,TRUE;3,13:00,9,TRUE;4,01:00,9,TRUE;5,13:00,9,TRUE;6,13:00,9,TRUE;0,13:00,9,TRUE";
        c.createSheduleFromString(schedule);
        String dumpstring = c.dumpSheduleString();
        System.out.println(dumpstring);

        ScheduleBO c1 = new ScheduleBO();
        c1.createSheduleFromString(dumpstring);
        int x = 1;
    }

    @Test
    public void is_shedule_event() throws Exception {

        Calendar calendar = Calendar.getInstance();
        String sDate1="21/06/2019:" + "13:25";
        Date time = new SimpleDateFormat("dd/MM/yyyy:HH:mm").parse(sDate1);


        calendar.setTime(time);
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));


        calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        calendar.get(Calendar.HOUR);


        ScheduleBO c = new ScheduleBO();
        String schedule = "WEEKLY::1,13:00,9,TRUE;2,13:00,9,TRUE;3,13:00,9,TRUE;4,23:15,9,TRUE;5,00:30,9,TRUE;6,13:00,9,TRUE;0,13:00,9,TRUE";
        c.createSheduleFromString(schedule);

        Date date = new Date(); // this object contains the current date value
        int x = LocalDate.now().getDayOfWeek().ordinal();
        ScheduleItemBO si = c.hasScheduleItem(LocalDate.now().getDayOfWeek().ordinal(), new Date());
        String msg = si.getTime().toString() + si.getDuration();
        x = 1000;

    }

    @Test
    public void testLogs(){
        ArrayList<String> s = new ArrayList<String>();
        s.add("a");
        s.add("b");

        String[] array = s.toArray(new String[s.size()]);

        int x = 1;


    }

    @Test
    public void xxx(){
        String s = "Jul 17, 2019 4:11:49 PM";
        s = "Jul 17, 2019 16:30:38 PM";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMM dd, yyyy H:mm:ss a");
         try {
                Date x = dateFormat.parse(s);
//                Date x = new Date();
//                String z = dateFormat.format(x);
                int c = 1;
            } catch (Exception e) {
             int c = 1;
                // Handle exception here
            }
    }
}