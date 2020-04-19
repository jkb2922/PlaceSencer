package envyandroid.org.graduationproject.Plan;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.R;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.CustomViewHolder> {

    private MainActivity activity;
    private ArrayList<PlanList> arrayList;
    private Boolean EditMode;
    private Fragment fragment;


    public PlanAdapter(MainActivity activity, ArrayList<PlanList> arrayList, Boolean editMode, Fragment fragment) {
        this.activity = activity;
        this.arrayList = arrayList;
        EditMode = editMode;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public PlanAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanAdapter.CustomViewHolder holder, int position) {
        holder.planId = arrayList.get(position).getPlanId();
        holder.planTitle.setText(arrayList.get(position).getPlanTitle());
        holder.planDate.setText(arrayList.get(position).getPlanDate());
        holder.planPlace.setText(arrayList.get(position).getPlanPlace());
        holder.planContent.setText(arrayList.get(position).getPlanContent());

        holder.planTitle.setOnClickListener(v -> {
            if(holder.planContent.getVisibility() == View.GONE){
                holder.planContent.setVisibility(View.VISIBLE);
            }else{
                holder.planContent.setVisibility(View.GONE);
            }
        });

        if(!EditMode){
            holder.planUpdate.setVisibility(View.GONE);
            holder.planRemove.setVisibility(View.GONE);
        }else{
            holder.planUpdate.setVisibility(View.VISIBLE);
            holder.planRemove.setVisibility(View.VISIBLE);
        }

        // 계획 수정
        holder.planUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PlanCreateActivity.class);
            intent.putExtra("process", "update");
            intent.putExtra("updatePlanNumber",   holder.planId);
            intent.putExtra("updatePlanTitle",    holder.planTitle.getText().toString());
            intent.putExtra("updatePlanTime",     holder.planDate.getText().toString());
            intent.putExtra("updatePlanDetail",   holder.planContent.getText().toString());
            intent.putExtra("updatePlanLocation", holder.planPlace.getText().toString());

            fragment.startActivityForResult(intent, 1);

        });

        // 계획 삭제
        holder.planRemove.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setMessage("해당 일정을 삭제하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {

                        File saveFile = new File(v.getContext().getFilesDir() + "/files");
                        try{
                            BufferedReader reader = new BufferedReader(new FileReader(saveFile + "/plan.txt"));
                            String Line = null;
                            String fileContent = "";

                            while((Line = reader.readLine()) != null){
                                JSONObject object = new JSONObject(Line);
                                //System.out.println("삭제 시작 : " + object.toString());
                                //System.out.println("holder : " + holder.planId);
                                //System.out.println("planNumber : " + object.getString("planNumber"));
                                if(!(holder.planId).equals(object.get("planNumber").toString())){
                                    fileContent = fileContent.concat(object.toString() + "\n");
                                }
                            }

                            //System.out.println("ADAPTER : " + fileContent);
                            reader.close();

                            // 저장한 라인을 덮어씌운다.
                            FileWriter writer = new FileWriter(saveFile + "/plan.txt");
                            //System.out.println("저장 완료");
                            writer.write(fileContent);

                            writer.close();

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        PlanFragment planFragment = new PlanFragment();

                        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        activity.changePlanMapFragment();

                        ft.replace(R.id.Main_Frame, planFragment).addToBackStack(null);
                        ft.commit();


                    })
                    .setNegativeButton("No", null)
                    .show();

        });


        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected String planId;
        protected TextView planTitle;
        protected TextView planDate;
        protected TextView planPlace;
        protected TextView planContent;
        protected TextView planUpdate;
        protected TextView planRemove;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.planTitle = itemView.findViewById(R.id.planTitle);
            this.planDate = itemView.findViewById(R.id.planDate);
            this.planPlace = itemView.findViewById(R.id.planPlace);
            this.planContent = itemView.findViewById(R.id.planContent);
            this.planUpdate = itemView.findViewById(R.id.planUpdate);
            this.planRemove = itemView.findViewById(R.id.planRemove);
        }
    }
}
