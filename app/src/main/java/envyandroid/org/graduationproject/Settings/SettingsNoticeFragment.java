package envyandroid.org.graduationproject.Settings;

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

//--------------------------------
//  설정 페이지 - 공지사항 출력
//---------------------------------
public class SettingsNoticeFragment extends Fragment {

    private View view;

    // 공지사항 리사이클러뷰
    private ArrayList<NoticeList> noticeArrayList;
    private NoticeAdapter noticeAdapter;

    public static SettingsNoticeFragment newInstance(){
        return new SettingsNoticeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PlaceConfig.WriteLog(getClass(), "OnCreateView();");
        view = inflater.inflate(R.layout.fragment_settings_notice, container, false);

        MainActivity activity = (MainActivity) getActivity();

        //--------------------------
        //  공지사항 리스트 등록
        //--------------------------
        RecyclerView recyclerView = view.findViewById(R.id.noticeLayout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        noticeArrayList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeArrayList);
        recyclerView.setAdapter(noticeAdapter);

        //----------------------------------
        //  서버 통신
        //----------------------------------

        JSONObject object = new JSONObject();
        try {
            object.put("Code", "OK");
            sendRequest(object);
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;

    }

    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------
    public void sendRequest(JSONObject jsonObject){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                PlaceConfig.PLACE_SETTING_NOTICE_URL,
                response -> {
                    if (response != null) {
                        PlaceConfig.WriteLog("[NETWORK STATUS] NOTICE : RECEIVE RESPONSE DATA FROM SERVER");
                        try{
                            JSONArray jArr = new JSONArray(response);
                            JSONObject json;
                            for(int i=0; i<jArr.length(); i++) {
                                json = jArr.getJSONObject(i);
                                noticeArrayList.add(new NoticeList(json.getString("title"),
                                        json.getString("createTime"),
                                        json.getString("noticeContent")));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        noticeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(view.getContext(), getString(R.string.all_network_error_msg), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] NOTICE : " + error.getMessage())
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("confirmCode", String.valueOf(jsonObject));
                return params;
            }
        };
        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] NOTICE : SEND REQUEST DATA TO SERVER");
    }


}
