package auto.ausiot.util;

/**
 * Created by anu on 22/06/19.
 */

public class Constants {
    public static String ACTION_TOPIC = "test";
    public static String STATUS_TOPIC = "status_topic";


    public static String ACTION_R1_OPEN = "R1ON";
    public static String ACTION_R1_CLOSE = "R1OFF";

    public static String ACTION_R2_OPEN = "R2ON";
    public static String ACTION_R2_CLOSE = "R2OFF";

    public static String ACTION_GET_STATUS = "STATUS";

    //MQTT Login
//    public static String MQTT_HOST = "tcp://m12.cloudmqtt.com:13727";
//    public static String MQTT_USER = "sbpmtfqc";
//    public static String MQTT_PASSWD = "GGGMHoXnNMz-";

    public static String MQTT_HOST = "tcp://postman.cloudmqtt.com:15305";
    public static String MQTT_USER = "vckugjvl";
    public static String MQTT_PASSWD = "2zJw2vbb-eUH";


//    public static String MQTT_HOST = "tcp://postman.cloudmqtt.com:16469";
//    public static String MQTT_USER = "aiaarkdj";
//    public static String MQTT_PASSWD = "yXQw1rW71vp4";


    //No longer needed
    public static int ALARM_FREQUENCY = 15;
    public static int ALARM_REQUEST_CODE=101;

    // QR Code value per user
    public static String QR_CODE_FOR_USER = "USER_1";

    //Connect URL to rest service
    public static final String BASE_URL = "http://10.0.2.2:8080";
    //public static final String BASE_URL = "http://35.194.38.92:8080";

//    // Frequency of checking the status in mili seconds
//    public static int STATUS_CHECK_FREQUENCY = 120000;
//    // iN MINUTES
//    public static int MAX_HEART_BEAT_MISS_DURATION = 5;

    //
    public static String SENSOR_STATUS_ON_MSG = "NetworkON";

    public static String STATUS_R1_OPEN = "STATUS_R1ON";
    public static String STATUS_R1_CLOSE = "STATUS_R1OFF";

    public static String STATUS_R2_OPEN = "STATUS_R2ON";
    public static String STATUS_R2_CLOSE = "STATUS_R2OFF";

    // Frequency of checking the status in mili seconds
    public static int STATUS_CHECK_FREQUENCY = 20000;
    // In mili seconds
    public static int MAX_HEARTBEAT_MISSES = 2;


    public static String ERROR_MSG_SCHEDULE_NOT_LOADED = "Could not load the schedule.Try restarting the app";

}
