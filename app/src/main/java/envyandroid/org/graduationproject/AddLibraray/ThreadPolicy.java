package envyandroid.org.graduationproject.AddLibraray;

import android.os.StrictMode;

public class ThreadPolicy {

    public ThreadPolicy(){
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }
}
