package auto.ausiot.stroe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import auto.ausiot.util.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by anu on 16/07/19.
 */

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = Constants.BASE_URL;

    static Gson gson = new GsonBuilder()
            .setDateFormat("MMM dd, yyyy H:mm:ss a")
            .create();

    public static Retrofit getRetrofitInstance() {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}