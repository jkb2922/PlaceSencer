package envyandroid.org.graduationproject.Home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import envyandroid.org.graduationproject.R;

//----------------------------------------
//  메인페이지 - 이벤트 리스트 어댑터
//----------------------------------------
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.CustomViewHolder> {

    private ArrayList<EventList> arrayList;

    EventListAdapter(ArrayList<EventList> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //------------------------
        //  각 값 등록
        //------------------------
        holder.eventImagePath = (arrayList.get(position).getEventImagePath());
        holder.eventContent = (arrayList.get(position).getEventContent());
        holder.eventTitle.setText(arrayList.get(position).getEventTitle());

        holder.itemView.setTag(position);

        // 아이템 클릭 시 해당 아이템의 이미지와 링크가 출력되는 EventActivty로 넘어감
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventActivity.class);
            intent.putExtra("eventImagePath", holder.eventImagePath);
            intent.putExtra("eventContent", holder.eventContent);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle;
        String   eventImagePath;
        String   eventContent;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.eventTitle   = itemView.findViewById(R.id.eventTitle);
        }
    }
}
