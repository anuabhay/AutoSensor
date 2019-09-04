package auto.ausiot.stroe;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import auto.ausiot.schedule.AuthBody;
import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.User;
import auto.ausiot.vo.Schedule;
import auto.ausiot.vo.Unit;
import okhttp3.ResponseBody;
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
    public static User user = null;
    public static List<Unit> units = null;
    public static  List<Schedule> userSchedules = null;
    public static  List<ScheduleBO> userScheduleBOs = null;

    static String DEFUALT_SCHEDULE = "WEEKLY::1,13:00,9,TRUE;2,13:00,9,TRUE;3,13:00,9,TRUE;4,13:00,9,TRUE;5,13:00,9,TRUE;6,13:00,9,TRUE;0,13:00,9,TRUE";
    public static String authToken = null;
    public RestStore(Context context){
        this.context = context;
    }

    public Schedule loadfromservice(String sensorID, final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        //@TODO Need hooking this up with QR code
        Call<Schedule> call = service.getSchedules(sensorID,authToken);
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
        Call call = service.addSchedule(sc,authToken);
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
        Call call = service.deleteSchedule(sensorID,authToken);
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
        Unit unit = new Unit(sensorID,user.getId());
        Call call = service.addUnit(unit,authToken);
        call.enqueue(new Callback<Unit>() {
            @Override
            public void onResponse(Call<Unit> call, Response<Unit> response) {
                //@TODO Do We need to do anything here , save success
                Unit u = (Unit) response.body();
                restcallback.onResponse(u);

            }

            @Override
            public void onFailure(Call<Unit> call, Throwable t) {
                //@TODO Do error check
                restcallback.onFailure();
            }
        });
    }


    public void login(final String useremail, String password , final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        AuthBody ab = new AuthBody();
        ab.setEmail(useremail);
        ab.setPassword(password);
        Call call = service.login(ab);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //@TODO Do We need to do anything here , save success
                String token = null;
                try {
                    if(response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        token = json.getString("token");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }

                restcallback.onResponse(token,useremail);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //@TODO Do error check
                restcallback.onFailure();
            }
        });
    }

    public void register(User user , final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.register(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //@TODO Do We need to do anything here , save success
                String token = null;
                try {
                    if(response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        token = json.getString("token");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }

                restcallback.onResponse(token,null);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //@TODO Do error check
                restcallback.onFailure();
            }
        });
    }

    public Unit getUnits(String userID, final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        //@TODO Need hooking this up with QR code
        Call call = service.getUnits(userID,authToken);
        call.enqueue(new Callback<List<Unit>>() {
            @Override
            public void onResponse(Call<List<Unit>> call, Response<List<Unit>> response) {
                units = (List<Unit>) response.body();
                restcallback.onResponse("Success");
            }

            @Override
            public void onFailure(Call<List<Unit>> call, Throwable t) {

            }
        });
        return null;
    }

    public User getUser(String id, final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<User> call = service.getUser(id,authToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = (User) response.body();
                restcallback.onResponse(user);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //@TODO Do error check
            }
        });

        return user;
    }


    public void deleteUnit(String unitID,final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.deleteUnits(unitID,authToken);
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

    public Schedule getUserSchedules(String userID, final RestCallBack restcallback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        //@TODO Need hooking this up with QR code
        Call<List<Schedule>> call = service.getUserSchedules(userID,authToken);
        call.enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                userSchedules = (List<Schedule>) response.body();
                userScheduleBOs = new ArrayList<>();
                for (int i=0;i<userSchedules.size();i++){
                    userScheduleBOs.add(ScheduleBO.getScheduleBO(userSchedules.get(i)));
                }
                restcallback.onResponse(userSchedules);
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                //@TODO Do error check
            }
        });
        return sc;
    }

    public static ScheduleBO getScheduleByID(String id){
        ScheduleBO ret = null;
        for (int i = 0 ; i < userScheduleBOs.size(); i++){
            if (id.compareTo(userScheduleBOs.get(i).getId()) == 0){
                    ret = userScheduleBOs.get(i);
            }
        }
        return ret;
    }

    public static List<ScheduleBO> getScheduleByUnitLine(String unitID,String lineID){
        List<ScheduleBO> ret = new ArrayList<>();
        for (int i = 0 ; i < userScheduleBOs.size(); i++){
            if (unitID.compareTo(userScheduleBOs.get(i).getUnitID()) == 0){
                if (lineID.compareTo(userScheduleBOs.get(i).getLineID()) == 0) {
                    ret.add(userScheduleBOs.get(i));
                }
            }
        }
        return ret;
    }


    public static void deleteBO(String id){
        for (int i = 0 ; i < userScheduleBOs.size(); i++){
            if (id.compareTo(userScheduleBOs.get(i).getId()) == 0){
                userScheduleBOs.remove(i);
                break;
            }
        }

        for (int i = 0 ; i < userSchedules.size(); i++){
            if (id.compareTo(userSchedules.get(i).getId()) == 0){
                userSchedules.remove(i);
                break;
            }
        }
    }

    public static void addBO(ScheduleBO bo){
        boolean found = false;
        for (int i = 0 ; i < userScheduleBOs.size(); i++){
            if (bo.getId().compareTo(userScheduleBOs.get(i).getId()) == 0){
                found = true;
            }
        }
        if(found == false) {
            userSchedules.add(bo.getScheduleVO());
            userScheduleBOs.add(bo);
        }
    }

    public static void addUnit(Unit unit){
        units.add(unit);
    }

    public static void deleteUnit(String id){
        for (int i = 0 ; i < units.size(); i++){
            if (id.compareTo(units.get(i).getId()) == 0){
                units.remove(i);
                break;
            }
        }
    }


}
