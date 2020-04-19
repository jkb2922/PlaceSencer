package envyandroid.org.graduationproject.Community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import envyandroid.org.graduationproject.AddLibraray.VolleyNetwork;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//-----------------------------------------
//  커뮤니티 디테일 페이지 - 댓글 수정 팝업
//-----------------------------------------
public class CommentModifyActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 네이버 지도 및 위경도 변환기 선언
    private NaverMap map;
    private Geocoder geocoder;

    // 댓글, 장소 4칸
    private EditText commentModifyContent;
    private EditText commentModifyPlace1;
    private EditText commentModifyPlace2;
    private EditText commentModifyPlace3;
    private EditText commentModifyPlace4;

    // 전체 장소가 저장되는 코스
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_modify);
        PlaceConfig.WriteLog(this, "OnCreate();");
        // 보낸 데이터 받기
        Intent getIntent = getIntent();

        course                = getIntent.getStringExtra("course");
        String commentId      = getIntent.getStringExtra("commentID");
        String commentContent = getIntent.getStringExtra("commentContent");

        //--------------------
        // 네이버 맵 등록
        //--------------------
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_view, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);

        // EditText 모두 등록, 리스너 등록
        commentModifyContent = findViewById(R.id.commentModifyContent);
        commentModifyPlace1  = findViewById(R.id.commentModifyPlace1);
        commentModifyPlace2  = findViewById(R.id.commentModifyPlace2);
        commentModifyPlace3  = findViewById(R.id.commentModifyPlace3);
        commentModifyPlace4  = findViewById(R.id.commentModifyPlace4);

        commentModifyPlace1.setOnKeyListener(onKeyListener);
        commentModifyPlace2.setOnKeyListener(onKeyListener);
        commentModifyPlace3.setOnKeyListener(onKeyListener);
        commentModifyPlace4.setOnKeyListener(onKeyListener);

        // 이전 댓글 내용 등록
        commentModifyContent.setText(commentContent);

        // eidtText를 관리하기 위한 commentPlace
        EditText[] commentPlace = {commentModifyPlace1, commentModifyPlace2, commentModifyPlace3, commentModifyPlace4};
        String[] place = course.split(" - ");

        int listSize = place.length;

        for(int i=0;i<listSize; i++){
            commentPlace[i].setText(place[i]);
        }

        Button commentModifyOK = findViewById(R.id.commentModifyOk);
        Button commentModifyCancel = findViewById(R.id.commentModifyCancel);

        // 확인 버튼 눌렀을 시
        commentModifyOK.setOnClickListener(v -> {
            String Content = commentModifyContent.getText().toString();
            int size = 0;

            for(int i=0; i<4; i++){
                size = (!"".equals(commentPlace[i].getText().toString())) ? size+1 : size;
            }

            StringBuilder changeCourse = new StringBuilder();
            for(int i=0; i<size; i++){
                changeCourse.append(commentPlace[i].getText().toString());
                if(i+1 != size){
                    changeCourse.append(" - ");
                }
            }

            process(commentId, Content, changeCourse.toString());
            finish();
        });

        // 취소 버튼 눌렀을 시
        commentModifyCancel.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), getString(R.string.all_cancel_msg), Toast.LENGTH_SHORT).show();

            finish();
        });

    }

    //--------------------------------------
    //  editText Listener = Enter 눌렀을 시
    //--------------------------------------
    private EditText.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                EditText[] commentPlace = {commentModifyPlace1, commentModifyPlace2, commentModifyPlace3, commentModifyPlace4};
                int size = 0;

                for(int i=0; i<4; i++){
                    size = (!"".equals(commentPlace[i].getText().toString())) ? size+1 : size;
                }

                StringBuilder changeCourse = new StringBuilder();
                for(int i=0; i<size; i++){
                    changeCourse.append(commentPlace[i].getText().toString());
                    if(i+1 != size){
                        changeCourse.append(" - ");
                    }
                }

                // 지도 설정
                setMap(map, changeCourse.toString());
            }

            return false;
        }
    };


    // 서버 - 클라이언트 데이터 전송 데이터 작성
    private void process(String commentId, String commentContent, String changeCourse){

        //----------------------
        //  json 형태로 데이터 생성
        //----------------------

        JSONObject json = new JSONObject();
        try {
            json.put("target", commentId);

            json.put("process", "update");
            json.put("commentContent", commentContent);
            json.put("course", changeCourse);

            String[] countItem = changeCourse.split(" - ");
            int count = 0;

            for(int i=0; i<countItem.length; i++){
                count++;
            }
            json.put("count", count);


        }catch (Exception e){
            e.printStackTrace();
        }


        //------------------------
        //  서버 통신
        //-------------------------
        /* 변경점 : NetworkTasking -> sendRequest */
        sendRequest(json, "update");

    }

    // 네이버 지도 객체 받아오기
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        setMap(map, course);
    }

    // 지도 설정
    private void setMap(NaverMap naverMap, String course){
        Toast.makeText(this, course, Toast.LENGTH_SHORT).show();

        String[] place = course.split(" - ");

        try{

            PathOverlay pathOverlay = new PathOverlay();
            List<LatLng> location = new ArrayList<>();
            for(int i=0; i<place.length; i++){
                List<Address> list = geocoder.getFromLocationName(place[i], 10);

                if(list != null){
                    if (list.size() == 0) {
                        Toast.makeText(this.getApplicationContext(), getString(R.string.comment_no_address_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        // 해당되는 주소로 인텐트 날리기
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();

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
    public void sendRequest(JSONObject jsonObject, String process){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                PlaceConfig.PLACE_COMMUNITY_COMMENT_PROCESS_URL,
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] COMMENT_MODIFY : RESULT = NULL");
                        return;
                    }else{
                        PlaceConfig.WriteLog("[NETWORK STATUS] COMMENT_MODIFY : RECEIVE RESPONSE DATA FROM SERVER");
                    }
                    if("update".equals(process)){
                        Toast.makeText(getApplicationContext(), "수정되었습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] COMMENT_MODIFY : " + error.getMessage())
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("processData", String.valueOf(jsonObject));
                return params;
            }
        };
        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] COMMENT_MODIFY : SEND REQUEST DATA TO SERVER");
    }

}
