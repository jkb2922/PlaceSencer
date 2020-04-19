package envyandroid.org.graduationproject.Home;

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
import envyandroid.org.graduationproject.Community.CommunityDetailFragment;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//-------------------------------------------------
//  메인페이지 - 관심지역, 추천 리사이클러뷰 어댑터
//-------------------------------------------------
public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.CustomViewHolder> {

    private ArrayList<HomeList> arrayList;
    private MainActivity activity;

    HomeListAdapter(ArrayList<HomeList> arrayList, MainActivity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HomeListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //-----------------
        //  각 값 등록
        //-----------------
        holder.reviewId  = (arrayList.get(position).getReviewId());
        holder.title.setText(arrayList.get(position).getTitle());

        // 이미지 등록
        ImageLoader mImageLoader = VolleySingleton.getInstance(holder.itemView.getContext()).getImageLoader();
        holder.image.setImageUrl(
                PlaceConfig.ServerIP +"image/"+arrayList.get(position).getImage(),
                mImageLoader);

        holder.itemView.setTag(position);

        // 해당 아이템 클릭 시 커뮤니티 내용으로 이동함
        holder.itemView.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("reviewId", holder.reviewId);

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

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected String reviewId;
        protected NetworkImageView image;
        protected TextView title;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.image = itemView.findViewById(R.id.homeImage);
            this.title = itemView.findViewById(R.id.homeTitle);
        }
    }

}
