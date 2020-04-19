package envyandroid.org.graduationproject.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import envyandroid.org.graduationproject.AddLibraray.VolleyNetwork;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

public class SettingsFavoriteFragment extends Fragment {

    private View view;

    // 즐겨찾기 리사이클러뷰
    private ArrayList<FavoriteList> favoriteArrayList;
    private FavoriteAdapter favoriteAdapter;

    public static SettingsFavoriteFragment newInstance() {
        return new SettingsFavoriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PlaceConfig.WriteLog(getClass(), "OnCreateView();");

        view = inflater.inflate(R.layout.fragment_settings_favorite, container, false);
        MainActivity activity = (MainActivity) getActivity();

        //-------------------------------
        //  즐겨찾기 리사이클러뷰 등록
        //-------------------------------
        RecyclerView recyclerView = view.findViewById(R.id.favoriteLayout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        favoriteArrayList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(favoriteArrayList, activity);
        recyclerView.setAdapter(favoriteAdapter);

        SharedPreferences shared = activity.getSharedPreferences("LOG_DATA", Context.MODE_PRIVATE);
        String userNumber = shared.getString("userNumber", "");

        if("".equals(userNumber)){
            Toast.makeText(view.getContext(), getString(R.string.community_need_login_msg), Toast.LENGTH_SHORT).show();
        }else{
            //----------------------------
            //  통신 데이터 json 입력
            //----------------------------
            /* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 */
            JSONObject object = new JSONObject();
            try {
                object.put("userNumber", userNumber);
                /* 변경점 : NetworkTasking -> sendRequest */
                sendRequest(object);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return view;
    }


    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------
    /* 변경점 : NetworkTasking -> sendRequest */
    public void sendRequest(JSONObject jsonObject){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                PlaceConfig.PLACE_SETTING_FAVORITE_LIST_URL,
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] FAVORITE : RESULT = NULL");
                        return;
                    }else{
                        PlaceConfig.WriteLog("[NETWORK STATUS] FAVORITE : RECEIVE RESPONSE DATA FROM SERVER");
                    }
                    //-----------------------------------
                    //  서버에서 가져온 데이터 처리 코드
                    //-----------------------------------
                    if (response != null) {
                        try{
                            JSONArray jArr = new JSONArray(response);
                            JSONObject json;
                            for(int i=0; i<jArr.length(); i++) {
                                json = jArr.getJSONObject(i);
                                System.out.println(json.toString());
                                favoriteArrayList.add(new FavoriteList(
                                        json.getString("reviewId"),
                                        json.getString("title"),
                                        json.getString("imagePath"),
                                        json.getString("tagName"),
                                        json.getString("place")));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        favoriteAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(view.getContext(), "서버와의 통신에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] FAVORITE : " + error.getMessage())
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("favoriteList", String.valueOf(jsonObject));
                return params;
            }
        };
        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] FAVORITE : SEND REQUEST DATA TO SERVER");
    }


}
