package envyandroid.org.graduationproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

//공용 함수 모아놓은 클래스
public class CommonFunction {

    public static JSONObject getJsonStringFromMap(Map<String, Object> map ) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for( Map.Entry<String, Object> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonObject.put(key, value);
        }

        return jsonObject;
    }



}
