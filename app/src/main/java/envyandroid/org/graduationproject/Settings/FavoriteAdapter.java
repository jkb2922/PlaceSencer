package envyandroid.org.graduationproject.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import envyandroid.org.graduationproject.AddLibraray.VolleySingleton;
import envyandroid.org.graduationproject.Community.CommunityDetailFragment;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//-------------------------------
//  설정 페이지 - 즐겨찾기 어댑터
//-------------------------------
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.CustomViewHolder> {

    private ArrayList<FavoriteList> arrayList;
    private MainActivity activity;

    FavoriteAdapter(ArrayList<FavoriteList> arrayList, MainActivity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FavoriteAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.CustomViewHolder holder, int position) {
        holder.reviewId = arrayList.get(position).getReviewId();
        holder.title.setText(arrayList.get(position).getTitle());
        holder.tag.setText(arrayList.get(position).getTag());
        holder.course.setText(arrayList.get(position).getCourse());
        ImageLoader mImageLoader = VolleySingleton.getInstance(holder.itemView.getContext()).getImageLoader();
        holder.image.setImageUrl(PlaceConfig.ServerIP + "image/"+arrayList.get(position).getImage(), mImageLoader);

        // 아이템 클릭 시 해당 리뷰로 이동함
        holder.itemView.setOnClickListener(v -> {

            if(holder.favoriteDetail.getVisibility() == View.GONE) {
                holder.favoriteDetail.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(v1 -> {
                    Bundle bundle = new Bundle(); // 파라미터는 전달할 데이터 개수
                    bundle.putString("reviewId", holder.reviewId); // key , value

                    CommunityDetailFragment communityDetailFragment = new CommunityDetailFragment();
                    communityDetailFragment.setArguments(bundle);
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    activity.changeCommunityFragment();

                    ft.replace(R.id.Main_Frame, communityDetailFragment).addToBackStack(null);
                    ft.commit();
                });
            }else{
                holder.favoriteDetail.setVisibility(View.GONE);
            }


        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        private String reviewId;
        private TextView title;
        private NetworkImageView image;
        private TextView tag;
        private TextView course;
        private LinearLayout favoriteDetail;


        CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            title  = itemView.findViewById(R.id.favoriteTitle);
            image  = itemView.findViewById(R.id.favoriteThumbnail);
            tag    = itemView.findViewById(R.id.favoriteTag);
            course = itemView.findViewById(R.id.favoriteCourse);
            favoriteDetail = itemView.findViewById(R.id.favoriteDetail);
        }
    }
}
