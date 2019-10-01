package auto.ausiot.stroe;

import java.util.List;

import auto.ausiot.schedule.AuthBody;
import auto.ausiot.schedule.User;
import auto.ausiot.vo.Schedule;
import auto.ausiot.vo.Unit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @POST("/unit")
    Call<Unit> addUnit(@Body Unit unit, @Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("/units/{id}")
    Call <List<Unit>> getUnits(@Path("id") String id, @Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @DELETE("/units/{id}")
    Call<String> deleteUnits(@Path("id") String id,@Header("Authorization") String auth);


    @POST("api/auth/login")
    Call<ResponseBody> login(@Body AuthBody user);


    @POST("api/auth/register")
    Call<ResponseBody> register(@Body User user);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("/user/{id}")
    Call<User> getUser(@Path("id") String id,@Header("Authorization") String auth);


    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("/user/schedules/{id}")
    Call<List<Schedule>> getUserSchedules(@Path("id") String userId,@Header("Authorization") String auth);


    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("api/auth/resettoken")
    Call<String> resetToken(@Query("email") String id);
}
