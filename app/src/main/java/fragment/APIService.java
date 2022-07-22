package fragment;

import com.onaopemipodimowo.O4Homes.Notifications.MyResponse;
import com.onaopemipodimowo.O4Homes.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAvyjtkz4:APA91bFhmIr0a2VBBNCK1BGZ69g_mEZYO0lH9z7VjUPQoVjwc_uP0Lo4Ed_bODknFK7iMAlPvOlMy2pTPkW7duVF85JDRCj0Ol5DO8zm8faV_nLqzIeABag1pW0j6r5vJmxZ-gtaow46"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
