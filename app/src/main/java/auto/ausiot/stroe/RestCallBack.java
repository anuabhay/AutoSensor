package auto.ausiot.stroe;

import auto.ausiot.vo.Schedule;
import retrofit2.Callback;

/**
 * Created by anu on 17/07/19.
 */

public interface RestCallBack{
    void onResponse(Schedule sc);
    void onFailure();

}
