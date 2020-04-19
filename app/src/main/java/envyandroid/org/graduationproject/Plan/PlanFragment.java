package envyandroid.org.graduationproject.Plan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import envyandroid.org.graduationproject.Community.CommunityFragment;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

public class PlanFragment extends Fragment {

    public static PlanFragment newInstance() {
        return new PlanFragment();
    }

    private MainActivity activity;
    //
    private ArrayList<PlanList> planLists;
    private PlanAdapter planAdapter;
    private RecyclerView recyclerView;
    private Boolean EditMode = false;

    int listCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        activity = (MainActivity) getActivity();
        PlaceConfig.WriteLog(getClass(), "OnCreateView();");

        Button planAdd = view.findViewById(R.id.planAdd);
        planAdd.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext().getApplicationContext(), PlanCreateActivity.class);
            intent.putExtra("Count", String.valueOf(listCount));
            //System.out.println("AAAAAA : " + listCount);
            startActivityForResult(intent, 1);
        });


        Button planMap = view.findViewById(R.id.planMap);
        planMap.setOnClickListener(v -> activity.changePlanMapFragment() );


        //-------------------------------------------------
        // 리사이클러뷰 등록 코드
        //-------------------------------------------------
        recyclerView = view.findViewById(R.id.planView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        planLists = new ArrayList<>();
        planAdapter = new PlanAdapter(activity, planLists, EditMode, this);
        recyclerView.setAdapter(planAdapter);

        planAdapter.notifyDataSetChanged();

        //
        //  파일에서 데이터 읽어오기
        //
        try{
            File saveFolder = new File(view.getContext().getFilesDir() + "/files");
            if(!saveFolder.exists()){
               // System.out.println("폴더 없음");
                saveFolder.mkdir();
            }

            File contentFile = new File(saveFolder + "/plan.txt");
            if(!contentFile.exists()){
                //System.out.println("파일 없음");
                contentFile.createNewFile();
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(contentFile));
            String Line = null;

            bufferedReader.mark(500);

            while((Line = bufferedReader.readLine()) != null){
                //System.out.println("Line : " + Line);
                JSONObject object = new JSONObject(Line);
                planLists.add(new PlanList(
                        object.getString("planNumber"),
                        object.getString("planTitle"),
                        object.getString("planTime"),
                        object.getString("planDetail"),
                        object.getString("planLocation")
                ));
                listCount += 1;
            }

            planAdapter.notifyDataSetChanged();

            bufferedReader.reset();
            bufferedReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }



        Button planEditMode = view.findViewById(R.id.planEditMode);
        planEditMode.setOnClickListener(v -> {
            if (!EditMode) {
                // 일반 모드 -> 편집 모드
                EditMode = true;
                planEditMode.setText("편집 모드");
                setRecyclerView(EditMode);
            } else {
                // 편집 모드 -> 일반 모드
                EditMode = false;
                planEditMode.setText("일정 편집");
                setRecyclerView(EditMode);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1) {
            if (resultCode == MainActivity.RESULT_OK) {
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

    private void setRecyclerView(Boolean mode){
        planAdapter = new PlanAdapter(activity, planLists, mode, this);
        recyclerView.setAdapter(planAdapter);
        planAdapter.notifyDataSetChanged();
    }

}
