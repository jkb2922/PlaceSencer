package envyandroid.org.graduationproject.Community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import envyandroid.org.graduationproject.AddLibraray.VolleyNetwork;
import envyandroid.org.graduationproject.AddLibraray.VolleySingleton;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//-----------------------------------------
//  커뮤니티 디테일 페이지 - 리뷰 내용 출력
//-----------------------------------------
public class CommunityDetailFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private MainActivity activity;

    // 댓글 리스트
    private ArrayList<CommentList> commentLists;
    private CommentListAdapter commentListAdapter;

    // 이미지 로더
    private ImageLoader mImageLoader;

    private TextView title;
    private TextView tag;
    private TextView createTime;
    private TextView course;
    private TextView views;
    private TextView recommend;
    private LinearLayout imageViewPlus;

    // 맵
    private Geocoder geocoder;
    private NaverMap map;

    private String reviewId;

    public  static CommunityDetailFragment newInstance(){
        return new CommunityDetailFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community_detail, container, false);

        activity = (MainActivity)getActivity();

        //-----------------------------
        //  댓글 리사이클러뷰 등록
        //-----------------------------
        RecyclerView recyclerView = view.findViewById(R.id.commentListViews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentListAdapter = new CommentListAdapter(commentLists, activity, this);

        /* 변경점 : Thread 추가 */
        new Thread(() -> {
            commentLists =  new ArrayList<>();
            recyclerView.setAdapter(commentListAdapter);

            //-------------------------------------------
            //  이미지 로더 등록
            //-------------------------------------------
            mImageLoader = VolleySingleton.getInstance(view.getContext()).getImageLoader();

            //-------------------------------------------
            //  각 요소 등록
            //-------------------------------------------
            title      = view.findViewById(R.id.title);
            tag        = view.findViewById(R.id.tag);
            createTime = view.findViewById(R.id.creatTime);
            course     = view.findViewById(R.id.course);
            views      = view.findViewById(R.id.views);
            recommend  = view.findViewById(R.id.recommend);
            imageViewPlus = view.findViewById(R.id.imageViewPlus);

            // 글 번호 저장
            reviewId = (getArguments() != null) ? getArguments().getString("reviewId") : null;
        }).start();


        //------------------------------------------------------
        //  네이버 지도 등록
        //------------------------------------------------------
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.reviewDetailMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.reviewDetailMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        //------------------------------------------------------
        //  리뷰 내용 가져오기
        //------------------------------------------------------
        /* 변경점 : Thread 추가 */
        new Thread(() -> {
            /* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 */
            JSONObject jsonReviewId = new JSONObject();
            try {
                jsonReviewId.put("reviewId", reviewId);
                /* 변경점 : NetworkTasking -> sendRequest */
                sendRequest(PlaceConfig.PLACE_COMMUNITY_REVIEW_CONTENT_URL, jsonReviewId, "review");
            }catch (Exception e){
                e.printStackTrace();
            }

            //--------------------------------------------------------------------
            //  지도 스크롤 원할하게 해주는 코드
            //  지도 위에 투명 이미지 하나를 두고, 해당 이미지에 스크롤 이벤트 등록
            //--------------------------------------------------------------------
            ScrollView scrollView = view.findViewById(R.id.reviewScroll);
            ImageView transparentImage= view.findViewById(R.id.transparent_image);

            transparentImage.setOnTouchListener((v, event) -> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    default:
                        return true;
                }
            });

            geocoder = new Geocoder(view.getContext());

            //-----------------------------------------
            //  댓글 리스트 가져오기
            //-----------------------------------------
            /* 변경점 : NetworkTasking -> sendRequest */
            sendRequest(PlaceConfig.PLACE_COMMUNITY_COMMENT_LIST_URL, jsonReviewId, "commentList");

        }).start();

        /* 변경점 : Thread 추가 */
        new Thread(() -> {
            //-----------------------------------------
            //  추천 버튼 등록 및 기능 등록
            //-----------------------------------------
            ImageView recommendBtn = view.findViewById(R.id.recommendBtn);
            recommendBtn.setOnClickListener(v -> funcBtn("recommend"));

            //-----------------------------------------
            //  즐겨찾기 버튼 등록 및 기능 등록
            //-----------------------------------------
            ImageView favoriteBtn = view.findViewById(R.id.favoriteBtn);
            favoriteBtn.setOnClickListener(v -> funcBtn("favorite"));

            //-------------------------
            //  댓글등록버튼
            //-------------------------
            Button commentInsertBtn = view.findViewById(R.id.commentInsertBtn);
            commentInsertBtn.setOnClickListener(v -> {
                String userNumber = PlaceConfig.getUserNumber(getActivity());

                if("".equals(userNumber)){
                    // 로그인 정보 없을 시
                    Toast.makeText(getContext(), getString(R.string.community_need_login_msg), Toast.LENGTH_SHORT).show();
                }else{
                    // 로그인 정보가 있으면
                    try{
                        /* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 */
                        JSONObject json = new JSONObject();

                        EditText comment = view.findViewById(R.id.comment);
                        json.put("reviewId", reviewId);
                        json.put("userNumber", userNumber);
                        json.put("commentContent", comment.getText().toString());

                        int count = 0;
                        EditText place1 = view.findViewById(R.id.place1);
                        EditText place2 = view.findViewById(R.id.place2);
                        EditText place3 = view.findViewById(R.id.place3);
                        EditText place4 = view.findViewById(R.id.place4);

                        count++;
                        json.put("place1", place1.getText().toString());

                        if(!"".equals(place2.getText().toString())){ count++; json.put("place2", place2.getText().toString()); }
                        if(!"".equals(place3.getText().toString())){ count++; json.put("place3", place3.getText().toString()); }
                        if(!"".equals(place4.getText().toString())){ count++; json.put("place4", place4.getText().toString()); }

                        json.put("placeCount", count);

                        /* 변경점 : NetworkTasking -> sendRequest */
                        sendRequest(PlaceConfig.PLACE_COMMUNITY_COMMENT_INSERT_URL, json, "contents");

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    // 화면 새로고침
                    Bundle bundle = new Bundle(); // 파라미터는 전달할 데이터 개수
                    bundle.putString("reviewId", reviewId); // key , value

                    CommunityDetailFragment communityDetailFragment = new CommunityDetailFragment();
                    communityDetailFragment.setArguments(bundle);
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    activity.changeCommunityFragment();

                    ft.replace(R.id.Main_Frame, communityDetailFragment);
                    ft.commit();
                }
            });
        }).start();

        return view;
    }


    //-------------------------------------
    //  추천 / 즐겨찾기 기능 등록
    //-------------------------------------
    private void funcBtn(String switchCode){
        /* 변경점 : Thread 추가 */
        new Thread(() -> {
            String userNumber = PlaceConfig.getUserNumber(getActivity());
            if("".equals(userNumber)){
                Toast.makeText(getContext(), getString(R.string.community_need_login_msg), Toast.LENGTH_SHORT).show();
            }else{
                JSONObject object = new JSONObject();
                try {
                    object.put("contentId", reviewId);
                    object.put("userNumber", userNumber);

                }catch (Exception e){
                    e.printStackTrace();
                }

                if("recommend".equals(switchCode)){
                    sendRequest(PlaceConfig.PLACE_COMMUNITY_RECOMMEND_PROCESS_URL, object, "process");
                }else if("favorite".equals(switchCode)){
                    sendRequest(PlaceConfig.PLACE_SETTING_FAVORITE_PROCESS_URL, object, "process");
                }

            }
        }).start();
    }

    //---------------------------------
    //  화면 새로고침
    //---------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1){
            Bundle bundle = new Bundle();
            bundle.putString("reviewId", reviewId);

            CommunityDetailFragment communityDetailFragment = new CommunityDetailFragment();
            communityDetailFragment.setArguments(bundle);
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            activity.changeCommunityFragment();

            ft.replace(R.id.Main_Frame, communityDetailFragment).addToBackStack(null);
            ft.commit();
        }
    }

    //---------------------------
    //  네이버 지도 객체 받아오기
    //---------------------------
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
    }

    //--------------------------
    //  지도 설정
    //---------------------------
    private void setMap(NaverMap naverMap, String course){

        String[] place = course.split(" - ");

        Toast.makeText(view.getContext(), place[0], Toast.LENGTH_LONG);

        try{

            PathOverlay pathOverlay = new PathOverlay();
            List<LatLng> location = new ArrayList<>();
            for(int i=0; i<place.length; i++){
                List<Address> list = geocoder.getFromLocationName(place[i], 10);

                if(list != null){
                    if (list.size() == 0) {
                        Toast.makeText(view.getContext().getApplicationContext(), getString(R.string.comment_no_address_msg), Toast.LENGTH_SHORT).show();
                    } else {

                        Address address = list.get(0);
                        double lat = address.getLatitude();
                        double lon = address.getLongitude();

                        LatLng latLng = new LatLng(lat, lon);
                        location.add(latLng);

                        Marker marker = new Marker();
                        marker.setCaptionText(place[i]);
                        marker.setCaptionRequestedWidth(200);
                        marker.setCaptionAligns(Align.Top);
                        marker.setCaptionOffset(5);
                        marker.setCaptionTextSize(16);

                        if(i == 0) {
                            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng);
                            naverMap.moveCamera(cameraUpdate);
                        }
                        marker.setPosition(latLng);
                        marker.setMap(naverMap);
                    }
                }
            }
            pathOverlay.setCoords(location);
            pathOverlay.setWidth(20);
            pathOverlay.setOutlineWidth(5);
            pathOverlay.setPassedColor(Color.GRAY);
            pathOverlay.setMap(naverMap);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* 변경점 : NetworkTasking -> sendRequest */
    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------
    private void sendRequest(String url, JSONObject jsonObject, String process){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] COMMUNITY_DETAIL : RESULT = NULL");
                        return;
                    }

                    if("review".equals(process)) {
                        PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_DETAIL_REVIEW : "
                                + "RECEIVE RESPONSE DATA FROM SERVER");
                        try {
                            JSONArray jArr = new JSONArray(response);
                            JSONObject json = jArr.getJSONObject(0);

                            title.setText(json.getString("title"));
                            tag.setText(json.getString("tag"));
                            createTime.setText(json.getString("createTime"));
                            course.setText(json.getString("course"));
                            views.append(json.getString("views"));
                            recommend.setText(json.getString("recommend"));

                            int count = Integer.parseInt(json.getString("imageCount"));

                            new Thread(() -> {
                                try{
                                    //이부분에서 mainThread 에러?
                                    for (int i = 0; i < count; i++) {
                                        // 이미지 갯수에 따라 이미지뷰와 텍스트뷰를 동적으로 등록함
                                        NetworkImageView reviewImage = new NetworkImageView(view.getContext().getApplicationContext());
                                        TextView reviewContent = new TextView(view.getContext().getApplicationContext());

                                        // 텍스트뷰 색깔, 사이즈 지정
                                        reviewContent.setTextColor(Color.parseColor("#000000"));
                                        reviewContent.setTextSize(14);

                                        // 이미지뷰 좌우값, 스케일 지정
                                        reviewImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
                                        reviewImage.setScaleType(ImageView.ScaleType.FIT_XY);

                                        // 이미지뷰를 등록할 레이아웃, 마진값 설정
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(0,20,0,200);

                                        // 이미지 받아오기
                                        reviewImage.setImageUrl(PlaceConfig.ServerIP + "image/" + json.getString("image" + i), mImageLoader);

                                        // 이미지 등록
                                        imageViewPlus.addView(reviewImage);

                                        // 텍스트뷰 등록
                                        if(!"".equals(json.optString("reviewContent"+(i+1), ""))) {
                                            reviewContent.setText(json.getString("reviewContent" + (i + 1)));
                                        }else{
                                            reviewContent.setText(" ");
                                        }
                                        reviewContent.setLayoutParams(params);
                                        imageViewPlus.addView(reviewContent);

                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }).start();
                            // 지도 설정
                            setMap(map, json.getString("course"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if("commentList".equals(process)){
                        //--------------------
                        //  댓글 정보 가져오기
                        //--------------------
                        new Thread(() -> {
                            try {
                                PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_DETAIL_COMMENT : "
                                        + "RECEIVE RESPONSE DATA FROM SERVER");
                                JSONArray jArr = new JSONArray(response);
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject json = jArr.getJSONObject(i);
                                    commentLists.add(new CommentList(
                                            reviewId,
                                            json.getString("userNumber"),
                                            json.getString("commentId"),
                                            json.getString("course"),
                                            json.getString("nickName"),
                                            json.getString("createTime"),
                                            json.getString("recommend"),
                                            json.getString("commentContent")));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }).start();


                        commentListAdapter.notifyDataSetChanged();

                    }else if("process".equals(process)){
                        PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_DETAIL_PROCESS : "
                                + "RECEIVE RESPONSE DATA FROM SERVER");
                        Toast.makeText(view.getContext(), response.trim(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] COMMUNITY_DETAIL : " + error.getMessage())
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if ("review".equals(process) || "commentList".equals(process)){
                    params.put("review", String.valueOf(jsonObject));
                } else if ("contents".equals(process)){
                    params.put("commentData", String.valueOf(jsonObject));
                } else if ("process".equals(process)){
                    params.put("userDataProcess", String.valueOf(jsonObject));
                }

                return params;
            }
        };

        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] COMMUNITY_DETAIL : SEND REQUEST DATA TO SERVER");
    }
}
