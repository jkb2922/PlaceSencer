package envyandroid.org.graduationproject.Plan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Objects;

import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

public class PlanCreateActivity extends AppCompatActivity {

    int listCount = 0;
    private EditText mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener; //12일 추가
    private EditText mDisplayTime; //12일 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_create);
        PlaceConfig.WriteLog(this, "OnCreate();");

        Intent intent = getIntent();

        // 날짜, 위치, 타이틀, 활동내용으로 구성된 내용을 받아 파일에 저장함.
        EditText planTime     = findViewById(R.id.planTime);
        EditText planTitle    = findViewById(R.id.planTitle);
        EditText planLocation = findViewById(R.id.planLocation);
        EditText planDetail   = findViewById(R.id.planDetail);

        if(intent.getStringExtra("process") != null){
            TextView activityTitle = findViewById(R.id.planActivityTitle);
            activityTitle.setText("일정 수정");
            planTime.setText(intent.getStringExtra("updatePlanTime"));
            planTitle.setText(intent.getStringExtra("updatePlanTitle"));
            planLocation.setText(intent.getStringExtra("updatePlanLocation"));
            planDetail.setText(intent.getStringExtra("updatePlanDetail"));
        }else{
            listCount = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("Count")));
        }

        // OK
        Button planSave = findViewById(R.id.planSave);
        planSave.setOnClickListener(v -> {
            // 입력사항 중 공백이 있을 시.

            //----------------------------------------------------------------
            //  파일에 데이터 저장
            //  data/data/envyandroid.org.graduationproject/files/plan.txt
            //----------------------------------------------------------------
            File saveFile = new File(getApplicationContext().getFilesDir() + "/files");

            try{
                // 폴더가 없을 경우
                if(!saveFile.exists()){
                    saveFile.mkdir();
                }

                // 제목/위치가 빠진 경우 입력 요청
                if("".equals(planTitle.getText().toString()) || "".equals(planLocation.getText().toString())){
                    Toast.makeText(this, getString(R.string.plan_need_edit_msg), Toast.LENGTH_SHORT).show();
                }else {

                    JSONObject object = new JSONObject();
                    if(intent.getStringExtra("process") != null){
                        object.put("planNumber", intent.getStringExtra("updatePlanNumber"));
                    }else{
                        object.put("planNumber", "PNB00" + (listCount + 1));
                    }
                    object.put("planTitle", planTitle.getText().toString());
                    object.put("planTime", planTime.getText().toString());
                    object.put("planLocation", planLocation.getText().toString());
                    object.put("planDetail", planDetail.getText().toString());

                    //System.out.println(object.toString());

                    if(intent.getStringExtra("process") != null){
                        BufferedReader reader = new BufferedReader(new FileReader(saveFile + "/plan.txt"));
                        String Line = null;
                        String fileContent = "";

                        while((Line = reader.readLine()) != null){
                            JSONObject target = new JSONObject(Line);
                            if((object.getString("planNumber")).equals(target.getString("planNumber"))){
                                fileContent = fileContent.concat(object.toString() + "\n");
                            }
                        }

                        //System.out.println("ADAPTER : " + fileContent);
                        reader.close();

                        // 저장한 라인을 덮어씌운다.
                        FileWriter writer = new FileWriter(saveFile + "/plan.txt");
                        //System.out.println("수정 완료");
                        writer.write(fileContent);

                        writer.close();
                    }else {
                        BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile + "/plan.txt", true));
                        buf.append(object.toString() + "\n");
                        buf.close();
                    }

                    //System.out.println("데이터 입력 성공");
                    Intent backIntentResult = new Intent();
                    setResult(RESULT_OK, backIntentResult);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        });



        // Cancel
        Button planCancel = findViewById(R.id.planCancel);
        planCancel.setOnClickListener(v -> finish());


        mDisplayDate = (EditText) findViewById(R.id.planday);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        PlanCreateActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //      ----------------다이얼로그 날짜 선택----------------     달에는 + 1 왜냐 1월은 0 으로 인식하기때문
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day +"/" + year );

                String date = year + "년" +""+ month + "월" +""+ day + "일" ;
                mDisplayDate.setText(date);
            }
        };

        //=============================2020년 4월 12일 완성(추가)=============================
        //TimePicker======================================================================
        mDisplayTime = (EditText)findViewById(R.id.planTime);
        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog
                        (PlanCreateActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog,            //테마 -> Theme_Holo_Light_Dialog 로 변경 4월 12일
                                mTimeSetListener
                                ,hour,minute, false);      //false값은 24시간제를 안하겠다함 그래서 am/pm으로 나눠서나옴 근데왜안돼
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        //      ----------------다이얼로그 시간 선택----------------
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                //   Log.d(TAG, "onTimeSet: " + hour + " 시 " + minute + " 분 " );
                String time = hour + " 시 " + " " + minute + " 분 " + " " ;
                mDisplayTime.setText(time);
            }
        };

    }
}