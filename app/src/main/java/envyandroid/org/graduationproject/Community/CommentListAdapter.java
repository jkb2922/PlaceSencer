package envyandroid.org.graduationproject.Community;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import envyandroid.org.graduationproject.AddLibraray.VolleyNetwork;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//------------------------------------------
//  커뮤니티 디테일 페이지 - 댓글 어댑터
//------------------------------------------
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CustomViewHolder> {

    private ArrayList<CommentList> arrayList;
    private MainActivity activity;
    private Fragment fragment;


    CommentListAdapter(ArrayList<CommentList> arrayList, MainActivity activity, Fragment fragment) {
        this.arrayList = arrayList;
        this.activity = activity;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CommentListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.CustomViewHolder holder, int position) {
        //--------------------------
        //  각 값 등록
        //---------------------------
        holder.course.setText(arrayList.get(position).getCourse());
        holder.commentContent.setText(arrayList.get(position).getCommentContent());
        holder.recommend.setText(arrayList.get(position).getRecommend());
        holder.createTime.setText(arrayList.get(position).getCreateTime());
        holder.userName.setText(arrayList.get(position).getUserName());
        holder.commentID = arrayList.get(position).getCommentID();
        holder.userNumber = arrayList.get(position).getUserNumber();
        holder.reviewId = arrayList.get(position).getReviewId();

        //----------------------------------------
        //  공유객체에 저장된 로그인 정보를 가져옴
        //----------------------------------------
        SharedPreferences shared = activity.getSharedPreferences("LOG_DATA", Context.MODE_PRIVATE);

        //--------------------------------------------------------------------------------
        // 현재 로그인한 ID와 댓글의 유저ID를 비교하여 다음 작업 수행 가능(댓글 수정/삭제)
        //--------------------------------------------------------------------------------
        if(holder.userNumber.equals(shared.getString("userNumber", ""))){

            LinearLayout commentControl = holder.itemView.findViewById(R.id.commentControl);
            ImageView commentRecommend = holder.itemView.findViewById(R.id.commentRecommend);
            commentRecommend.setVisibility(View.GONE);
            commentControl.setVisibility(View.VISIBLE);

            holder.commentUpdate.setOnClickListener(v -> {
                //----------------------
                // 수정 시 - 팝업으로
                //----------------------
                Intent intent = new Intent(v.getContext(), CommentModifyActivity.class);
                intent.putExtra("commentID",      holder.commentID);
                intent.putExtra("reviewId",       holder.reviewId);
                intent.putExtra("course",         holder.course.getText().toString());
                intent.putExtra("commentContent", holder.commentContent.getText().toString());

                fragment.startActivityForResult(intent, 1);
            });

            holder.commentRemove.setOnClickListener(v -> {
                //----------------------
                // 삭제 시 - 확인 팝업
                //----------------------
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("확인창");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        (dialog, which) -> {            // "확인"
                            process(holder.commentID, holder.reviewId);  });
                builder.setNegativeButton("아니오",
                        (dialog, which) -> {            // "취소"
                            Toast.makeText(activity.getApplicationContext(), "취소하셨습니다.", Toast.LENGTH_LONG).show();
                        });
                builder.show();
            });

        }else{
            //--------------------------------------------------------------------------------
            // 로그인정보 != 댓글 유저 정보
            // 추천기능. (본인글 추천막음)
            //--------------------------------------------------------------------------------
            ImageView commentRecommend = holder.itemView.findViewById(R.id.commentRecommend);
            commentRecommend.setOnClickListener(v -> {
                String userNumber = shared.getString("userNumber", "");
                if("".equals(userNumber)){
                    Toast.makeText(v.getContext(), "로그인해주세요", Toast.LENGTH_SHORT).show();
                }else{
//                    ContentValues value = new ContentValues();
                    JSONObject value = new JSONObject();
                    try {
                        value.put("contentId", holder.commentID);
                        value.put("userNumber", userNumber);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    /* 변경점 : NetworkTasking -> sendRequest */
                    sendRequest(PlaceConfig.PLACE_COMMUNITY_RECOMMEND_PROCESS_URL, value, "recommend");

//                    NetworkTasking networkTasking = new NetworkTasking(
//                            PlaceConfig.PLACE_COMMUNITY_RECOMMEND_PROCESS_URL, value, "recommend");
//                    networkTasking.execute();
                }
            });
        }
        //-------------------------------------------
        // 댓글의 코스 부분을 누르면 액티비티 시작
        //-------------------------------------------
        holder.course.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CommentMapActivity.class);
            intent.putExtra("course", holder.course.getText().toString());
            fragment.startActivity(intent);
        });

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView course;
        String reviewId;
        String commentID;
        String userNumber;
        TextView userName;
        TextView createTime;
        TextView recommend;
        TextView commentContent;
        TextView commentRemove;
        TextView commentUpdate;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.course = itemView.findViewById(R.id.commentCourse);
            this.commentContent = itemView.findViewById(R.id.commentContent);
            this.recommend = itemView.findViewById(R.id.recommendNum);
            this.createTime = itemView.findViewById(R.id.createdDateText);
            this.userName = itemView.findViewById(R.id.commentUserName);
            this.commentRemove = itemView.findViewById(R.id.commentRemove);
            this.commentUpdate = itemView.findViewById(R.id.commentUpdate);
        }
    }

    //-------------------------------------------
    // 서버 - 클라이언트 데이터 전송 데이터 작성
    //-------------------------------------------
    private void process(String commentId, String reviewId){

        //----------------------
        //  json 형태로 데이터 생성
        //----------------------
        /* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 */
        JSONObject json = new JSONObject();
        try {
            json.put("target", commentId);
            json.put("process", "delete");
        }catch (Exception e){
            e.printStackTrace();
        }
        //------------------------
        //  서버 통신
        //-------------------------
        /* 변경점 : NetworkTasking -> sendRequest */
        sendRequest(PlaceConfig.PLACE_COMMUNITY_COMMENT_PROCESS_URL, json, "delete");

        //----------------------
        //  화면 새로고침
        //----------------------
        refresh(reviewId);
    }

    //-------------------------------------------
    // 화면 새로고침
    //-------------------------------------------
    private void refresh(String reviewId){
        Bundle bundle = new Bundle();
        bundle.putString("reviewId", reviewId);

        CommunityDetailFragment communityDetailFragment = new CommunityDetailFragment();
        communityDetailFragment.setArguments(bundle);
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.Main_Frame, communityDetailFragment);
        ft.commit();
    }

    /* 변경점 : NetworkTasking -> sendRequest */
    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------
    public void sendRequest(String url, JSONObject jsonObject, String process){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] COMMENT_PROCESS : RESULT = NULL");
                        return;
                    }else{
                        PlaceConfig.WriteLog("[NETWORK STATUS] COMMENT_PROCESS : RECEIVE RESPONSE DATA FROM SERVER");
                    }

                    if("delete".equals(process)){
                        Toast.makeText(activity.getApplicationContext(), "댓글이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }else if("update".equals(process)){
                        Toast.makeText(activity.getApplicationContext(), "댓글이 수정되었습니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(activity.getApplicationContext(), response.trim(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] COMMENT_PROCESS : " + error.getMessage())
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if("delete".equals(process)){
                    params.put("processData", String.valueOf(jsonObject));
                }else if ("recommend".equals(process)){
                    params.put("userDataProcess", String.valueOf(jsonObject));
                }

                return params;
            }
        };

        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] COMMENT_PROCESS : SEND REQUEST DATA TO SERVER");
    }
}
