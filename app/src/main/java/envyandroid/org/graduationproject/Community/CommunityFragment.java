package envyandroid.org.graduationproject.Community;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

//------------------------------------------------
//  커뮤니티 페이지 - 커뮤니티 검색 및 리스트 출력
//------------------------------------------------
public class CommunityFragment extends Fragment {

    private View view;
    private MainActivity activity;

    //--------------------------
    //  리사이클러뷰 등록 변수
    //--------------------------
    private ArrayList<CommunityList> communityLists;
    private CommunityListAdapter communityListAdapter;

    //  검색바 변수
    private EditText communityEdit;

    // 리스트 정렬 플래그변수
    private boolean sortFlag = false;
    private boolean flag = true;
    private int page = 1;

    public static CommunityFragment newInstance(){
        return new CommunityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community, container, false);

        activity = (MainActivity)getActivity();

        //------------------------------------------
        // 스크롤뷰 등록 - 스크롤로 데이터 로드할 것임
        //------------------------------------------
        NestedScrollView communityScroll = view.findViewById(R.id.communityListScrollView);


        //-------------------------
        //  검색란 전달값 등록
        //-------------------------
        communityEdit = view.findViewById(R.id.communityEdit);
        if(getArguments() != null){

            String SearchText = getArguments().getString("SearchText");
            communityEdit.setText(SearchText);
        }

        communityEdit.setOnKeyListener((v, keyCode, event) -> {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                if("".equals(communityEdit.getText().toString())){
                    Toast.makeText(view.getContext(),"검색어를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else {
                    changeFragment(communityEdit.getText().toString());
                }
            }
            return false;
        });

        //------------------
        //  검색버튼 등록
        //------------------
        ImageView communityImage = view.findViewById(R.id.communityImage);
        communityImage.setOnClickListener(v -> {
            if ("".equals(communityEdit.getText().toString())) {
                Toast.makeText(view.getContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                changeFragment(communityEdit.getText().toString());
            }
        });



        //-------------------------------------------------
        // 리사이클러뷰 등록 코드
        //-------------------------------------------------
        RecyclerView recyclerView = view.findViewById(R.id.communityList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        communityLists = new ArrayList<>();
        communityListAdapter = new CommunityListAdapter(communityLists, activity);
        recyclerView.setAdapter(communityListAdapter);

        communityListAdapter.notifyDataSetChanged();


        //---------------
        //  서버 통신
        //---------------
        /* 변경점 : Thread 추가 */

        // --------------성현 ----------

        sendRequest2(communityEdit.getText().toString(),"","").start();
/*        new Thread(() -> {
            try{
                *//* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 *//*
                JSONObject object = new JSONObject();
                object.put("searchText", communityEdit.getText().toString());

                *//* 변경점 : NetworkTasking -> sendRequest *//*
                PlaceConfig.WriteLog(getActivity() ,"[NETWORK CONNECT] COMMUNITY_SEARCH_PAGE_URL CALL()");
                sendRequest(object);
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();*/
        //---------------------------
        //------------------------------------------
        //  시간순 / 추천순 정렬 코드 ** 재사용 고민 **
        //------------------------------------------
        Button sortCreateTime = view.findViewById(R.id.sortCreateTime);
        Button sortRecommend  = view.findViewById(R.id.sortRecommend);

        // 시간순 정렬
        /* 변경점 : Thread 추가 */
        //성현 ----------------------------------------------
        sortCreateTime.setOnClickListener(view -> {
            sendRequest2(communityEdit.getText().toString(),"","").start();
        });
/*        sortCreateTime.setOnClickListener(v -> new Thread(() -> {
            try{
                *//* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 *//*
                JSONObject object = new JSONObject();
                object.put("searchText", communityEdit.getText().toString());

                *//* 변경점 : NetworkTasking -> sendRequest *//*
                PlaceConfig.WriteLog(getActivity() ,"[NETWORK CONNECT] COMMUNITY_SORT_TIME_URL CALL()");
                sendRequest(object);
            }catch (Exception e){
                e.printStackTrace();
            }

            communityLists = new ArrayList<>();
            communityListAdapter = new CommunityListAdapter(communityLists, activity);
            recyclerView.setAdapter(communityListAdapter);

            communityListAdapter.notifyDataSetChanged();

            sortFlag = false;
        }).start());*/
        //----------------------------------------------

        // 추천순 정렬
        /* 변경점 : Thread 추가 */
        //-------------성현 -----------------
        sortRecommend.setOnClickListener(view -> {
         sendRequest2(communityEdit.getText().toString(),"true","").start();
        });
/*        sortRecommend.setOnClickListener(v -> new Thread(() -> {
            try{
                *//* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 *//*
                JSONObject object = new JSONObject();
                object.put("searchText", communityEdit.getText().toString());
                object.put("sort", "true");

                *//* 변경점 : NetworkTasking -> sendRequest *//*
                PlaceConfig.WriteLog(getActivity() ,"[NETWORK CONNECT] COMMUNITY_SORT_RECOMMEND_URL CALL()");
                sendRequest(object);
            }catch (Exception e){
                e.printStackTrace();
            }

            communityLists = new ArrayList<>();
            communityListAdapter = new CommunityListAdapter(communityLists, activity);
            recyclerView.setAdapter(communityListAdapter);

            communityListAdapter.notifyDataSetChanged();

            sortFlag = true;
        }).start());*/
        //---------------------------------------
        //--------------------------------------
        //  최하단 스크롤 시 리스트 가져오기
        //---------------------------------------
        /* 변경점 : Thread 추가 */

        //성현 --------------------------------
        communityScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!communityScroll.canScrollVertically(1)) {
                if (flag || page < 17) {    // page 무한증가 방지
                    // 최하단 리스트 추가
                    page++;
                    sendRequest2(communityEdit.getText().toString(),"true",String.valueOf(page)).start();
                }
            }

        });
 /*       communityScroll.getViewTreeObserver().addOnScrollChangedListener(() -> new Thread(() -> {
            if (!communityScroll.canScrollVertically(1)) {
                if (flag || page < 17) {    // page 무한증가 방지
                    // 최하단 리스트 추가
                    page++;
                    try {
                        *//* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 *//*
                        JSONObject object = new JSONObject();
                        object.put("searchText", communityEdit.getText().toString());
                        object.put("paging", page);
                        if (sortFlag) object.put("sort", "true");
                        *//* 변경점 : NetworkTasking -> sendRequest *//*
                        PlaceConfig.WriteLog(getActivity() ,"[NETWORK CONNECT] COMMUNITY_PAGING_URL CALL()");
                        sendRequest(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start());*/

        //-------------------------
        return view;
    }

    //--------------------------------
    //  검색 시 리스트 새로고침
    //--------------------------------
    private void changeFragment(String data){

        Bundle bundle = new Bundle();
        bundle.putString("SearchText", data);

        CommunityFragment communityFragment = new CommunityFragment();
        communityFragment.setArguments(bundle);

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.Main_Frame, communityFragment);
        ft.commit();
    }

    /* 변경점 : NetworkTasking -> sendRequest */
    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------

    private Thread sendRequest2(String _searchText, String _sort, String _paging){
        return new Thread(){
            @Override
            public void run() {
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        PlaceConfig.PLACE_COMMUNITY_SEARCH_COMMUNITY_URL,
                        response -> {
                            if(response == null){
                                PlaceConfig.WriteLog("[NETWORK ERROR] COMMUNITY_SEARCH : RESULT = NULL");
                                return;
                            }else{
                                PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_SEARCH : RECEIVE RESPONSE DATA FROM SERVER");
                            }

                            // 네트워크 통신시 아무것도 안오면 33이 찍혔음
                            if (response.trim().length() > 33) {
                                try {
                                    JSONArray jArr = new JSONArray(response);
                                    JSONObject json;

                                    for (int i = 0; i < jArr.length(); i++) {
                                        json = jArr.getJSONObject(i);

                                        communityLists.add(new CommunityList(
                                                json.getString("reviewId"),
                                                json.getString("title"),
                                                json.getString("recommend"),
                                                json.getString("views"),
                                                json.getString("tag"),
                                                json.getString("course"),
                                                json.getString("image"),
                                                json.getString("commentCount")));
                                    }
                                    flag = true;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                ProgressBar pb_loading = view.findViewById(R.id.pb_loading);
                                pb_loading.setVisibility(View.GONE);
                                Toast.makeText(view.getContext(),  getString(R.string.community_no_list_msg), Toast.LENGTH_SHORT).show();
                            }
                            communityListAdapter.notifyDataSetChanged();
                        },
                        error -> PlaceConfig.WriteLog("[NETWORK ERROR] COMMUNITY_SEARCH : " + error.getMessage())
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        try {
                            JSONObject object = new JSONObject();
                            object.put("searchText", _searchText);
                            object.put("paging", _paging);
                            object.put("sort", _sort);
                            PlaceConfig.WriteLog(getClass(),"searchtext : " + _searchText + " / paging : " + _paging + " / sort : " + _sort  );
                            Map<String, String> params = new HashMap<>();
                            params.put("search", String.valueOf(object));
                            return params;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                request.setShouldCache(false);
                VolleyNetwork.requestQueue.add(request);
                PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_SEARCH : SEND REQUEST DATA TO SERVER");

            }
        };
    }
    private void sendRequest(JSONObject jsonObject){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                PlaceConfig.PLACE_COMMUNITY_SEARCH_COMMUNITY_URL,
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] COMMUNITY_SEARCH : RESULT = NULL");
                        return;
                    }else{
                        PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_SEARCH : RECEIVE RESPONSE DATA FROM SERVER");
                    }

                    // 네트워크 통신시 아무것도 안오면 33이 찍혔음
                    if (response.trim().length() > 33) {
                        try {
                            JSONArray jArr = new JSONArray(response);
                            JSONObject json;

                            for (int i = 0; i < jArr.length(); i++) {
                                json = jArr.getJSONObject(i);

                                communityLists.add(new CommunityList(
                                        json.getString("reviewId"),
                                        json.getString("title"),
                                        json.getString("recommend"),
                                        json.getString("views"),
                                        json.getString("tag"),
                                        json.getString("course"),
                                        json.getString("image"),
                                        json.getString("commentCount")));
                            }
                            flag = true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        ProgressBar pb_loading = view.findViewById(R.id.pb_loading);
                        pb_loading.setVisibility(View.GONE);
                        Toast.makeText(view.getContext(),  getString(R.string.community_no_list_msg), Toast.LENGTH_SHORT).show();
                    }
                    communityListAdapter.notifyDataSetChanged();
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] COMMUNITY_SEARCH : " + error.getMessage())
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search", String.valueOf(jsonObject));
                return params;
            }
        };
        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_SEARCH : SEND REQUEST DATA TO SERVER");
    }
}
