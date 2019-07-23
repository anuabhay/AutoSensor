package auto.ausiot.stroe;

import auto.ausiot.vo.Schedule;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by anu on 16/07/19.
 */

public interface GetDataService {
//    @GET("/schedules")
//    Call<List<Schedule>> getSchedules();

    @GET("/schedule/{id}")
    Call<Schedule> getSchedules(@Path("id") String userId);


    @POST("/schedule")
    Call<ResponseBody> addSchedule(@Body Schedule parts);

    @DELETE("/schedule/{id}")
    Call<String> deleteSchedule(@Path("id") String id);

    @POST("/sensor")
    Call<ResponseBody> addSensor(@Body String sensorID);

}
