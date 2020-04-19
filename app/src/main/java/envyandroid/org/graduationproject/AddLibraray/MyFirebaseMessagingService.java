package envyandroid.org.graduationproject.AddLibraray;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import envyandroid.org.graduationproject.CommonFunction;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PlaceSensor_Firebase";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // key , data 정상 수신 된경우
        if (remoteMessage.getData().size() > 0) {
            //받은 key , data 값중에 PlaceConfig에 저장되어있는 키가 있는경우
            // 공지알림
            if(remoteMessage.getData().containsKey(PlaceConfig.PLACE_MSG_NOTICE_KEY)){
            // 해당 공지 Activity 이동
            } else if (remoteMessage.getData().containsKey(PlaceConfig.PLACE_MSG_COMMENT_KEY)){
                // 댓글알림
                // 댓글 Activity 이동
            }
/*            JSONObject test = (JSONObject) remoteMessage.getData();
            try {
                Log.d(TAG, test.getString("key1"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
    @Override
    public void onNewToken(String token) {
        //token = 디바이스 고유 값
        //Log.d(TAG, "Refreshed token: " + token);
        PlaceConfig.WriteLog(this,"Call OnNewToken()");
        PlaceConfig.WriteLog(this,"token ID : " + token);
        //토큰 저장
        PlaceConfig.setTokenID(this,token);
        //서버 디바이스 토큰 등록 함수
        //sendRegistrationToServer(token);
    }


    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "a")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("dfdsf")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channelId",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}