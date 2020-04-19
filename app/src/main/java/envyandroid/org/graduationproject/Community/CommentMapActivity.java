package envyandroid.org.graduationproject.Community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.List;

import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;


//-----------------------------------------
//  커뮤니티 디테일 페이지 - 댓글 지도 팝업
//-----------------------------------------
public class CommentMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_map);
        PlaceConfig.WriteLog(this, "OnCreate();");
        //------------------
        //  네이버맵 등록
        //------------------
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.commentMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.commentMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);

    }

    //-------------------------------------------
    //  맵 객체 받아오기
    //-------------------------------------------
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        Intent getIntent = getIntent();
        String course = getIntent.getStringExtra("course");

        setMap(naverMap, course);
    }


    //-------------------------------------------
    //  맵 설정
    //-------------------------------------------
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
}
