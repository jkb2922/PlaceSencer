package envyandroid.org.graduationproject.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


import java.util.ArrayList;

import envyandroid.org.graduationproject.AddLibraray.VolleySingleton;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//-----------------------------------------
//  커뮤니티 페이지 - 커뮤니티 리스트 어댑터
//-----------------------------------------
public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.CustomViewHolder> {

    private ArrayList<CommunityList> arrayList;
    private MainActivity activity;

    CommunityListAdapter(ArrayList<CommunityList> arrayList, MainActivity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CommunityListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommunityListAdapter.CustomViewHolder holder, int position) {
        //--------------------------------------------
        //  각 값 등록
        //--------------------------------------------
        holder.title.setText(arrayList.get(position).getTitle());
        holder.tag.setText(arrayList.get(position).getTagName());
        holder.recommend.setText(arrayList.get(position).getRecommend());
        holder.views.setText(arrayList.get(position).getViews());
        holder.course.setText(arrayList.get(position).getCourse());
        holder.commentCount.setText(arrayList.get(position).getCommentCount());

        //-------------------------------------------------
        //  서버에서 가져온 이미지등록
        //-------------------------------------------------
        ImageLoader mImageLoader = VolleySingleton.getInstance(holder.itemView.getContext()).getImageLoader();
        holder.image.setImageUrl(PlaceConfig.ServerIP + "image/"+arrayList.get(position).getImage(), mImageLoader);
        holder.reviewId = arrayList.get(position).getReviewId();

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {

            Bundle bundle = new Bundle(); // 파라미터는 전달할 데이터 개수
            bundle.putString("reviewId", holder.reviewId); // key , value

            CommunityDetailFragment communityDetailFragment = new CommunityDetailFragment();
            communityDetailFragment.setArguments(bundle);
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            activity.changeCommunityFragment();

            ft.replace(R.id.Main_Frame, communityDetailFragment).addToBackStack(null);
            ft.commit();
        });



    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected String reviewId;
        protected TextView title;
        protected TextView recommend;
        protected TextView views;
        protected TextView tag;
        protected TextView course;
        protected TextView commentCount;
        protected NetworkImageView image;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.reviewTitle);
            this.recommend = itemView.findViewById(R.id.reviewRecommend);
            this.views = itemView.findViewById(R.id.reviewViews);
            this.commentCount = itemView.findViewById(R.id.comment);
            this.tag = itemView.findViewById(R.id.reviewTag);
            this.course = itemView.findViewById(R.id.courseList);
            this.image = itemView.findViewById(R.id.reviewThumbnail);
        }
    }
}
