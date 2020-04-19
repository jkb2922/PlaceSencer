package envyandroid.org.graduationproject.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.regex.Pattern;

import envyandroid.org.graduationproject.AddLibraray.VolleySingleton;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//----------------------------------------
//  메인페이지(HomeFragment) - 이벤트 팝업
//----------------------------------------
public class EventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        PlaceConfig.WriteLog(this, "OnCreate();");

        // HomeFragment에서 넘긴 값을 받아옴
        Intent getData = getIntent();

        //초기화
        NetworkImageView eventImage = findViewById(R.id.eventImage);
        TextView eventLink = findViewById(R.id.eventLink);
        Button eventOk = findViewById(R.id.eventOk);


        //------------------------------------
        //  이미지 / 링크 등록
        //------------------------------------

        //서버 아이피 가져오기
        MainActivity activity = new MainActivity();

        //이미지
        ImageLoader mImageLoader =
                VolleySingleton.getInstance(this.getApplicationContext()).getImageLoader();

        eventImage.setImageUrl(
                PlaceConfig.ServerIP + "image/" + getData.getStringExtra("eventImagePath"),
                mImageLoader);

        //링크
        Linkify.TransformFilter mTransform = (match, url) -> "";
        Pattern pattern1 = Pattern.compile("이벤트 페이지로 이동하기");
        Linkify.addLinks(eventLink, pattern1, getData.getStringExtra("eventContent"),
                null, mTransform);


        //------------------------------------
        //  뒤로 가기
        //------------------------------------
        eventOk.setOnClickListener(v -> finish());

    }
}
