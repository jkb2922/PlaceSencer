package envyandroid.org.graduationproject.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import envyandroid.org.graduationproject.R;

//-------------------------------
//  설정 페이지 - 공지사항 어댑터
//-------------------------------
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.CustomViewHolder> {

    private ArrayList<NoticeList> arrayList;

    public NoticeAdapter(ArrayList<NoticeList> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NoticeAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.CustomViewHolder holder, int position) {
        holder.noticeTitle.setText(arrayList.get(position).getNoticeTitle());
        holder.noticeDate.setText(arrayList.get(position).getNoticeDate());
        holder.noticeContent.setText(arrayList.get(position).getNoticeContent());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {
            LinearLayout noticeContentList = v.findViewById(R.id.noticeContentList);
            if( noticeContentList.getVisibility() == View.GONE){
                noticeContentList.setVisibility(View.VISIBLE);
            }else {
                noticeContentList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView noticeTitle;
        protected TextView noticeDate;
        protected TextView noticeContent;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.noticeTitle = itemView.findViewById(R.id.noticeTitle);
            this.noticeDate  = itemView.findViewById(R.id.noticeDate);
            this.noticeContent = itemView.findViewById(R.id.noticeContent);
        }
    }

}