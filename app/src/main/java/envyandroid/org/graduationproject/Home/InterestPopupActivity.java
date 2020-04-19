package envyandroid.org.graduationproject.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//-------------------------------------
//  메인페이지 - 관심지역 팝업 액티비티
//-------------------------------------
public class InterestPopupActivity extends Activity {

    // 관심지역 등록을 편하게 하기위한 ArrayList points
    private ArrayList<String> interestPoints;

    // 선택된 관심지역이 전부 저장될 point
    private String point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_popup);
        PlaceConfig.WriteLog(this, "OnCreate();");
        //초기화
        point = "";
        interestPoints = new ArrayList<>();

        //----------------------------------------
        //  확인 버튼 누를 시
        //----------------------------------------
        final Button pointOk = findViewById(R.id.pointOk);
        pointOk.setOnClickListener(v -> {

            Intent intent = new Intent();

            // interestPoints에 저장된 값을 toString으로 받아온다.
            point = point.concat(interestPoints.toString()
                    .replace("[", "")
                    .replace("]", ""));

            SharedPreferences savePoint = getSharedPreferences("Interest", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = savePoint.edit();
            editor.putString("Point", point);
            editor.apply();

            setResult(RESULT_OK, intent);

            finish();
        });


        //----------------------------------------
        //  취소 버튼
        //----------------------------------------
        Button pointCancel = findViewById(R.id.pointCancel);
        pointCancel.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), getString(R.string.all_cancel_msg), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("result", "");
            setResult(RESULT_CANCELED, intent);
            finish();
        });

    }


    //----------------------------------------
    //  지역 선택/비선택에 따른 관심지역 저장
    //----------------------------------------
    public void onClick(View v){
        Button button = findViewById(v.getId());
        if(!v.isSelected()){
            // 버튼이 선택되었을 시 선택처리하고 해당 버튼의 text값을 points에 넣는다
            v.setSelected(true);
            interestPoints.add(button.getText().toString());
        }
        else{
            // 버튼이 해제되었을 시 선택 해제처리하고 해당 버튼의 text값을 points에서 검색, 삭제한다.
            v.setSelected(false);
            Iterator iter =  interestPoints.iterator();
            while(iter.hasNext()){
                if((button.getText().toString()).equals(iter.next())){
                    iter.remove();
                }
            }
        }
    }

}
