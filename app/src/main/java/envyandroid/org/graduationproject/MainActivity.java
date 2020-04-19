package envyandroid.org.graduationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import java.util.ArrayList;

import envyandroid.org.graduationproject.Community.CommunityFragment;
import envyandroid.org.graduationproject.Home.HomeFragment;
import envyandroid.org.graduationproject.Plan.PlanFragment;
import envyandroid.org.graduationproject.Plan.PlanMapFragment;
import envyandroid.org.graduationproject.Settings.SettingsFavoriteFragment;
import envyandroid.org.graduationproject.Settings.SettingsFragment;
import envyandroid.org.graduationproject.Settings.SettingsNoticeFragment;

public class MainActivity extends AppCompatActivity {

    // 하단메뉴바
    private BottomNavigationView bottomNavigationView;

    // 하단 메뉴바 아이템 선택용
    private ArrayList<String> menuHistory;

    //
    //public String ServerIP = "http://3.16.183.218/";

/*    public String getServerIP(){
        return ServerIP;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log 예시 (kosh 20.03.27)
        //if(PlaceConfig.DEBUG) Log.d(PlaceConfig.TAG + this.getLocalClassName(),"OnCreate();");
        PlaceConfig.WriteLog(this,"onCreate()");
        //by kosh 200329 firbase cloud message add
        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                       // String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        //Log.d("PlaceSensor_", token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });*/

        menuHistory = new ArrayList<>();

        //-------------------
        //  하단메뉴바 등록
        //-------------------
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.Main_Frame, HomeFragment.newInstance()).commit();

        // menuHistory에 menu_home 기본 등록
        menuHistory.add(PlaceConfig.Main_bottom_menu_home);
    }

    //-----------------------------
    //  하단 메뉴바 리스너 선언
    //-----------------------------
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.menu_home:                         // 홈으로 이동
                    replaceFragment(HomeFragment.newInstance());
                    menuHistory.add(PlaceConfig.Main_bottom_menu_home);
                    return true;

                case R.id.menu_community:                    // 커뮤니티로 이동
                    replaceFragment(CommunityFragment.newInstance());
                    menuHistory.add(PlaceConfig.Main_bottom_menu_community);
                    return true;

                case R.id.menu_plan:                         // 계획으로 이동
                    replaceFragment(PlanFragment.newInstance());
                    menuHistory.add(PlaceConfig.Main_bottom_menu_plan);
                    return true;

                case R.id.menu_settings:                     // 설정으로 이동
                    replaceFragment(SettingsFragment.newInstance());
                    menuHistory.add(PlaceConfig.Main_bottom_menu_settings);
                    return true;
            }
            return false;
        }
    };

    //----------------------
    // 프래그먼트 갈아끼우기
    //-----------------------
    private void replaceFragment(Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Main_Frame, fragment).addToBackStack(null).commit();
    }

    //-------------------------
    //  뒤로가기 버튼 기능
    //-------------------------
    @Override
    public void onBackPressed() {

        int historyCount = menuHistory.size();

        if (historyCount == 1) {

            // menuHistory에 값이 main_home밖에 없으면 다음 메세지 출력
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.main_end_msg)) // String.xml 에서 불러오게 수정 by kosh 2020.03.27
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", null)
                    .show();

        } else {
            if (historyCount > 1) {

                // 프래그먼트 현재 스택 제거, menuHistory 최상단 제거
                getSupportFragmentManager().popBackStack();
                menuHistory.remove(historyCount-1);

                // menuHistory 최상단 switch
                switch(menuHistory.get(historyCount-2)){
                    case PlaceConfig.Main_bottom_menu_home:                         // 홈으로 이동
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        break;

                    case PlaceConfig.Main_bottom_menu_community:                    // 커뮤니티로 이동
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        break;

                    case PlaceConfig.Main_bottom_menu_plan:                         // 여행계획으로 이동
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        break;

                    case PlaceConfig.Main_bottom_menu_settings :                    // 설정으로 이동
                        bottomNavigationView.getMenu().getItem(3).setChecked(true);
                        break;
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    /*  */
    //-------------------------------------------------
    //  프래그먼트 내에서 다른 프래그먼트로 갈아끼우기용
    //-------------------------------------------------
    public void changeCommunityFragment(){
        menuHistory.add(PlaceConfig.Main_bottom_menu_community);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    public void changePlanMapFragment(){
        menuHistory.add(PlaceConfig.Main_bottom_menu_plan);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        replaceFragment(PlanMapFragment.newInstance());
    }

    public void changeNoticeFragment(){
        menuHistory.add(PlaceConfig.Main_bottom_menu_settings);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        replaceFragment(SettingsNoticeFragment.newInstance());
    }

    public void changeFavoriteFragment(){
        menuHistory.add(PlaceConfig.Main_bottom_menu_settings);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        replaceFragment(SettingsFavoriteFragment.newInstance());
    }


    /*  */
    //---------------------
    //  여행계획용, 실험중
    //---------------------
    public void planSetMode(boolean mode){
        TextView planUpdate = findViewById(R.id.planUpdate);
        TextView planRemove = findViewById(R.id.planRemove);
        if(!mode){
            planUpdate.setVisibility(View.VISIBLE);
            planRemove.setVisibility(View.VISIBLE);
        }else{
            planUpdate.setVisibility(View.GONE);
            planRemove.setVisibility(View.GONE);
        }

    }

}

