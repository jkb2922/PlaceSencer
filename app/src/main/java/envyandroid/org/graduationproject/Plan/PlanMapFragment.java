package envyandroid.org.graduationproject.Plan;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

public class PlanMapFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private Geocoder geocoder;
    private NaverMap map;
    private ArrayList<String> list;

    public static PlanMapFragment newInstance(){
        return new PlanMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PlaceConfig.WriteLog(getClass(), "OnCreateView();");
        view = inflater.inflate(R.layout.fragment_plan_map, container, false);

        list = new ArrayList<>();

        //------------------------------------------------------
        //  네이버 지도 등록
        //------------------------------------------------------
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.planMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.planMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(view.getContext());

        //
        //  파일에서 데이터 읽어오기
        //
        try{
            File saveFolder = new File(view.getContext().getFilesDir() + "/files");
            File contentFile = new File(saveFolder + "/plan.txt");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(contentFile));
            String Line;

            bufferedReader.mark(500);

            while((Line = bufferedReader.readLine()) != null){
                //System.out.println("Line : " + Line);
                JSONObject object = new JSONObject(Line);

                list.add(object.getString("planLocation"));
            }

            bufferedReader.reset();
            bufferedReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //setMap(map);

        return view;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        setMap(map);
    }


    //-------------------------------------------
    //  맵 설정
    //-------------------------------------------
    private void setMap(NaverMap naverMap){
        try{
            Toast.makeText(view.getContext(), list.toString(), Toast.LENGTH_SHORT).show();
            PathOverlay pathOverlay = new PathOverlay();
            List<LatLng> location = new ArrayList<>();
            for(int i=0; i<list.size(); i++){
                String place = list.get(i);

                List<Address> list = geocoder.getFromLocationName(place, 10);

                if(list != null){
                    if (list.size() == 0) {
                        Toast.makeText(view.getContext().getApplicationContext(), getString(R.string.comment_no_address_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        // 해당되는 주소로 인텐트 날리기
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();

                        LatLng latLng = new LatLng(lat, lon);
                        location.add(latLng);

                        //System.out.println("latLng : " + latLng);

                        Marker marker = new Marker();
                        marker.setCaptionText(place);
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
