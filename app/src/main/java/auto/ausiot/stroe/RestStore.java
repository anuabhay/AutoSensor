package auto.ausiot.stroe;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import auto.ausiot.util.Constants;
import auto.ausiot.vo.Schedule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anu on 20/06/19.
 */

public class RestStore /*implements ScheduleStore*/ {

    Context context;
    String fileName;
    Schedule sc;

    static String DEFUALT_SCHEDULE = "WEEKLY::1,13:00,9,TRUE;2,13:00,9,TRUE;3,13:00,9,TRUE;4,13:00,9,TRUE;5,13:00,9,TRUE;6,13:00,9,TRUE;0,13:00,9,TRUE";

    public RestStore(Context context){
        this.context = context;
    }

    public Schedule loadfromservice(String sensorID, final RestCallBack restcallback){
//        try {
//            sensorID = URLEncoder.encode(sensorID,"utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        //@TODO Need hooking this up with QR code
        Call<Schedule> call = service.getSchedules(sensorID);
        call.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                sc = (Schedule) response.body();
                restcallback.onResponse(sc);
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                //@TODO Do error check
            }
        });

        return sc;
    }

    public void savefromservice(Schedule sc,final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.addSchedule(sc);
        call.enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                //@TODO Do We need to do anything here , save success
                restcallback.onResponse(null);

            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                //@TODO Do error check
                restcallback.onFailure();
            }
        });
    }

    public void deletefromservice(String sensorID,final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.deleteSchedule(sensorID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //@TODO Do We need to do anything here , save success
                restcallback.onResponse(null);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                restcallback.onFailure();
                //@TODO Do error check
            }
        });
    }


    public void addSensorfromservice(String sensorID,final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.addSensor(sensorID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //@TODO Do We need to do anything here , save success
                restcallback.onResponse(null);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //@TODO Do error check
                restcallback.onFailure();
            }
        });
    }

}
