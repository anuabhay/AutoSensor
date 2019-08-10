package auto.ausiot.stroe;

import auto.ausiot.schedule.AuthBody;
import auto.ausiot.schedule.User;
import auto.ausiot.vo.Schedule;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by anu on 16/07/19.
 */

public interface GetDataService {
//    @GET("/schedules")
//    Call<List<Schedule>> getSchedules();

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("/schedule/{id}")
    Call<Schedule> getSchedules(@Path("id") String userId,@Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("/schedule")
    Call<ResponseBody> addSchedule(@Body Schedule parts,@Header("Authorization") String auth );

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @DELETE("/schedule/{id}")
    Call<String> deleteSchedule(@Path("id") String id,@Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("/sensor")
    Call<ResponseBody> addSensor(@Body String sensorID,@Header("Authorization") String auth);

    @POST("api/auth/login")
    Call<ResponseBody> login(@Body AuthBody user);


    @POST("api/auth/register")
    Call<ResponseBody> register(@Body User user);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("/user/{id}")
    Call<User> getUser(@Path("id") String id,@Header("Authorization") String auth);

}
