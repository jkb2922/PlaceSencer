package envyandroid.org.graduationproject.Home;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import envyandroid.org.graduationproject.AddLibraray.VolleyNetwork;
import envyandroid.org.graduationproject.Community.CommunityFragment;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

import static android.content.Context.MODE_PRIVATE;


//--------------------------------------------------
//  플레이스 센서 메인 페이지(프래그먼트)
//--------------------------------------------------
public class HomeFragment extends Fragment {

    private View view;
    private MainActivity activity;

    // 관심지역 설정 및 검색부분
    private TextView interestPoint;
    private EditText searchText;

    //  관심지역 리스트
    private ArrayList<HomeList> interestList;
    private HomeListAdapter interestAdapter;

    //  추천 리스트
    private ArrayList<HomeList> recommendList;
    private HomeListAdapter recommendAdapter;

    //  이벤트 리스트
    private ArrayList<EventList> eventList;
    private EventListAdapter eventAdapter;


    public static HomeFragment newInstance(){
        return new HomeFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PlaceConfig.WriteLog(getClass(), "OnCreateView();");
        // 프래그먼트 등록
        view = inflater.inflate(R.layout.fragment_home, container, false);

        activity = (MainActivity)getActivity();

        // 처음 한번만 등록 ( 차후 로고 화면 옮김 )
        if(VolleyNetwork.requestQueue == null){
            // 리퀘스트큐 생성
            VolleyNetwork.requestQueue = Volley.newRequestQueue(view.getContext().getApplicationContext());
        }

        //-------------------------------
        //  관심 지역 팝업
        //-------------------------------
        interestPoint = view.findViewById(R.id.interestPoint);
        LinearLayout interestLayout = view.findViewById(R.id.interestLayout);
        interestLayout.setOnClickListener(v -> {

            // 관심지역 선택 팝업
            Intent intent = new Intent(v.getContext(), InterestPopupActivity.class);
            startActivityForResult(intent, 1);
        });


        //-------------------------------------
        // 공유 객체에 저장된 관심 지역을 불러옴
        //-------------------------------------
        SharedPreferences savedPoint = activity.getSharedPreferences("Interest", MODE_PRIVATE);
        interestPoint.setText(savedPoint.getString("Point", ""));


        //-------------------------------
        //  검색바, 검색 이미지 등록
        //-------------------------------
        searchText = view.findViewById(R.id.searchText);
        searchText.setOnKeyListener((v, keyCode, event) -> {
            // 키보드 엔터키가 눌렸을 시
            if(keyCode == KeyEvent.KEYCODE_ENTER){ checkSearchText("search"); }
            return false;
        });

        // 검색 이미지 눌렸을 시
        ImageView searchBtn = view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(v -> checkSearchText("search"));


        //-------------------------------
        //  리뷰 더보기(관심지역 포함)
        //-------------------------------
        ImageView pointReviewMore = view.findViewById(R.id.pointReviewMore);
        pointReviewMore.setOnClickListener(v -> checkSearchText("point"));


        //-------------------------------
        //  관심지역 여행 리스트 등록
        //-------------------------------
        interestList = new ArrayList<>();
        interestAdapter = new HomeListAdapter(interestList, activity);
        RecyclerView interestRecyclerView = view.findViewById(R.id.interestView);
        setHomeRecyclerView(interestRecyclerView, interestAdapter);


        //-------------------------------
        //  추천순 리스트 등록
        //-------------------------------
        recommendList = new ArrayList<>();
        recommendAdapter = new HomeListAdapter(recommendList, activity);
        RecyclerView recommendRecyclerView = view.findViewById(R.id.recommendedView);
        setHomeRecyclerView(recommendRecyclerView, recommendAdapter);


        //-------------------------------
        //  이벤트 리스트 등록
        //-------------------------------
        RecyclerView eventRecyclerView = view.findViewById(R.id.eventList);
        LinearLayoutManager eventManager = new LinearLayoutManager(view.getContext());
        eventRecyclerView.setLayoutManager(eventManager);

        eventList = new ArrayList<>();
        eventAdapter = new EventListAdapter(eventList);
        eventRecyclerView.setAdapter(eventAdapter);


        //---------------
        //  서버 통신
        //---------------
        ContentValues jsonData = new ContentValues();
        //----------------------------
        //  통신 데이터 json 입력
        //----------------------------
        //  기존
        //  jsonData.put("interestPoint", interestPoint.getText().toString());
        //
        //  변경 ->
        //
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Point", interestPoint.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        sendRequest(jsonObject);


        return view;
    }


    //------------------------------------------------------
    //  관심지역 팝업에서 리턴된 정보를 받아 저장함
    //------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1){
            if(resultCode == MainActivity.RESULT_OK){

                //관심지역 데이터 받아오기
                SharedPreferences returnPoint = view.getContext().getSharedPreferences("Interest", MODE_PRIVATE);
                String result = returnPoint.getString("Point", "");
                interestPoint.setText(result);

                //화면 다시띄우기
                FragmentTransaction ft = null;
                if (getFragmentManager() != null) {
                    ft = getFragmentManager().beginTransaction();
                }
                if (ft != null) {
                    ft.detach(this).attach(this).commit();
                }
            }
        }
    }

    //------------------------------------
    //  리사이클러뷰 등록(관심지역, 추천)
    //------------------------------------
    private void setHomeRecyclerView(RecyclerView recyclerView, HomeListAdapter homeListAdapter){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(homeListAdapter);
    }

    //------------------
    //  검색 전 체크
    //------------------
    private void checkSearchText(String keyword){

        if("search".equals(keyword)){               // 검색어를 community로 넘김
            if("".equals(searchText.getText().toString())){
                Toast.makeText(view.getContext(),getString(R.string.community_need_search_msg),Toast.LENGTH_SHORT).show();
            }else {
                changeFragment(searchText.getText().toString());
            }
        }else if("point".equals(keyword)){            // 관심지역을 community로 넘김
            if("".equals(interestPoint.getText().toString())){
                Toast.makeText(view.getContext(),getString(R.string.community_need_favorite_msg),Toast.LENGTH_SHORT).show();
            }else {
                changeFragment(interestPoint.getText().toString());
            }
        }
    }


    //--------------------------------
    //  Community로 이동(검색/더보기)
    //--------------------------------
    private void changeFragment(String data){

        Bundle bundle = new Bundle();
        bundle.putString("SearchText", data);

        CommunityFragment communityFragment = new CommunityFragment();
        communityFragment.setArguments(bundle);

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        activity.changeCommunityFragment();
        ft.replace(R.id.Main_Frame, communityFragment).addToBackStack(null);
        ft.commit();
    }


    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------
    /* 변경점 : NetworkTasking -> sendRequest */
    private void sendRequest(JSONObject jsonObject){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                PlaceConfig.PLACE_HOME_MAINPAGE_URL,    //url
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] HOME : RESULT = NULL");
                        return;
                    }else{
                        PlaceConfig.WriteLog("[NETWORK STATUS] HOME : RECEIVE RESPONSE DATA FROM SERVER");
                    }

                    try{
                        JSONArray jsonArray  = new JSONArray(response);
                        JSONObject interest  = jsonArray.getJSONObject(0);
                        JSONObject recommend = jsonArray.getJSONObject(1);
                        JSONObject event     = jsonArray.getJSONObject(2);

                        JSONArray interestArray  = interest.getJSONArray("interest");
                        JSONArray recommendArray = recommend.getJSONArray("recommend");
                        JSONArray eventListArray = event.getJSONArray("eventList");

                        //----------------------------------
                        //  관심지역 리스트 데이터 등록
                        //----------------------------------
                        for(int i=0; i<interestArray.length(); i++){
                            JSONObject interestObj = interestArray.getJSONObject(i);

                            interestList.add(new HomeList(
                                    interestObj.getString("InterestReviewId"),
                                    interestObj.getString("InterestImagePath"),
                                    interestObj.getString("InterestTitle")));
                        }

                        //----------------------------------
                        //  추천 리스트 데이터 등록
                        //----------------------------------
                        for(int i=0; i<recommendArray.length(); i++){
                            JSONObject recommendObj = recommendArray.getJSONObject(i);

                            recommendList.add(new HomeList(
                                    recommendObj.getString("RecommendReviewId"),
                                    recommendObj.getString("RecommendImagePath"),
                                    recommendObj.getString("RecommendTitle")));
                        }

                        //----------------------------------
                        //  이벤트 리스트 데이터 등록
                        //----------------------------------
                        for(int i=0; i<eventListArray.length(); i++){
                            JSONObject eventListObj = eventListArray.getJSONObject(i);

                            eventList.add(new EventList(
                                    eventListObj.getString("EventTitle"),
                                    eventListObj.getString("EventImagePath"),
                                    eventListObj.getString("EventContent")));
                        }
                        interestAdapter.notifyDataSetChanged();
                        recommendAdapter.notifyDataSetChanged();
                        eventAdapter.notifyDataSetChanged();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] HOME : " + error.getMessage())
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("interestPoint", String.valueOf(jsonObject));
                return params;
            }
        };

        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] HOME : SEND REQUEST DATA TO SERVER");
    }

}
