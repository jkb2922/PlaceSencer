package envyandroid.org.graduationproject.Settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import envyandroid.org.graduationproject.AddLibraray.VolleyNetwork;
import envyandroid.org.graduationproject.MainActivity;
import envyandroid.org.graduationproject.PlaceConfig;
import envyandroid.org.graduationproject.R;

//------------------------------------------------------------
//  설정 페이지 - 공지 / 즐겨찾기 / 알림설정 / 로그인 기능 제공
//------------------------------------------------------------
public class SettingsFragment extends Fragment {

    private View view;
    private MainActivity activity;

    //----------------------
    //  구글 파이어베이스
    //----------------------
    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    //----------------------
    //  페이스북
    //----------------------
    private CallbackManager mCallbackManager;

    //----------------------
    //  카카오
    //----------------------
    private SessionCallback callback;

    //----------------------
    //  네이버
    //----------------------
    private OAuthLogin mOAuthLoginModule;
    private Context mContext;
    private static String OAUTH_CLIENT_ID = "K4ZDpHWE4USSJmH_yfEk";
    private static String OAUTH_CLIENT_SECRET = "fgi0WeuONO";
    private static String OAUTH_CLIENT_NAME = "sohojm3636";


    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        activity = (MainActivity)getActivity();
        PlaceConfig.WriteLog(getClass(), "OnCreateView();");

        //--------------------
        // 공지 페이지로 이동
        //--------------------
        TextView settingNotice = view.findViewById(R.id.settingsNotice);
        settingNotice.setOnClickListener(v -> activity.changeNoticeFragment());


        //--------------------
        // 공지 페이지로 이동
        //--------------------
        TextView settingFavorite = view.findViewById(R.id.settingsFavorite);
        settingFavorite.setOnClickListener(v -> activity.changeFavoriteFragment());


        //-----------------
        // 알람설정
        //-----------------
        TextView settingsAlarm = view.findViewById(R.id.settingsAlarm);
        LinearLayout settingsAlarmLayout = view.findViewById(R.id.settingsAlarmLayout);
        settingsAlarm.setOnClickListener(v -> {
            if(view.findViewById(R.id.settingsAlarmLayout).getVisibility() == View.GONE){
                settingsAlarmLayout.setVisibility(View.VISIBLE);
            }else{
                settingsAlarmLayout.setVisibility(View.GONE);
            }
        });


        //---------------------------------------------------------
        //  구글 로그인 버튼 등록 / 파이어베이스 인증객체 초기화
        //---------------------------------------------------------
        SignInButton signInButton = view.findViewById(R.id.googleLoginBtn);
        mAuth = FirebaseAuth.getInstance();


        //------------------------------------------
        //  페이스북 로그인 코드
        //------------------------------------------
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginBtn = view.findViewById(R.id.facebookLoginBtn);
        facebookLoginBtn.setFragment(this);
        facebookLoginBtn.setReadPermissions("email", "public_profile");
        facebookLoginBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {            }

            @Override
            public void onError(FacebookException error) {            }
        });// ...


        //--------------------------------
        //  구글 로그인 코드
        //--------------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);
        signInButton.setOnClickListener(view -> signIn());


        //-------------------------------------
        // 네이버 로그인 코드
        //-------------------------------------
        mContext = getActivity();
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                activity,
                OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME
        );
        OAuthLoginButton naverLogInBtn = view.findViewById(R.id.naverLoginBtn);
        naverLogInBtn.setOAuthLoginHandler(mOAuthLoginHandler);


        //--------------------------------------
        // 카카오톡 로그인 콜백 등록
        //--------------------------------------
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);


        //--------------------------------------
        // 로그인 시 변동사항 / 로그아웃 버튼
        //--------------------------------------
        TextView settingsLoginBtn = view.findViewById(R.id.settingsLoginBtn);
        LinearLayout SettingsFavoriteLayout = view.findViewById(R.id.SettingsFavoriteLayout);
        if(mAuth.getCurrentUser() != null) {

            //  파이어베이스 (구글/페북)
            settingsLoginBtn.setText("로그아웃");
            view.findViewById(R.id.LoginLayout).setVisibility(View.GONE);
            SettingsFavoriteLayout.setVisibility(View.VISIBLE);


            settingsLoginBtn.setOnClickListener(v -> {
                //  구글 로그아웃
                signOut();

                //  페이스북 로그아웃
                LoginManager.getInstance().logOut();

                Toast.makeText(view.getContext(), getString(R.string.all_logout_msg), Toast.LENGTH_SHORT).show();

                removeSharedData();
                updateFragment();
            });
        }else if("OK".equals(mOAuthLoginModule.getState(mContext).toString())){

            //  네이버
            new RequestApiTask().execute();

            settingsLoginBtn.setText("로그아웃");
            view.findViewById(R.id.LoginLayout).setVisibility(View.GONE);
            SettingsFavoriteLayout.setVisibility(View.VISIBLE);

            settingsLoginBtn.setOnClickListener(v -> {
                // 네이버 로그아웃
                mOAuthLoginModule.logoutAndDeleteToken(mContext);
                Toast.makeText(view.getContext(), getString(R.string.all_logout_msg), Toast.LENGTH_SHORT).show();
                removeSharedData();
                updateFragment();
            });
        }else if (Session.getCurrentSession().isOpened()){

            //  카카오
            settingsLoginBtn.setText("로그아웃");
            view.findViewById(R.id.LoginLayout).setVisibility(View.GONE);
            SettingsFavoriteLayout.setVisibility(View.VISIBLE);

            settingsLoginBtn.setOnClickListener(v -> {
                Toast.makeText(view.getContext(), getString(R.string.all_logout_msg), Toast.LENGTH_SHORT).show();
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        removeSharedData();
                        updateFragment();
                    }
                });
            });
        }else{
            //--------------------------------
            //  로그인 정보가 없을 시
            //--------------------------------
            view.findViewById(R.id.settingsLoginBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(view.findViewById(R.id.LoginLayout).getVisibility() == View.GONE)
                        view.findViewById(R.id.LoginLayout).setVisibility(View.VISIBLE);
                    else{
                        view.findViewById(R.id.LoginLayout).setVisibility(View.GONE);
                    }
                }
            });
            SettingsFavoriteLayout.setVisibility(View.GONE);
        }
        return view;
    }


    // 구글 로그인
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // 구글 로그아웃
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //카카오
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        //페북
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        //구글
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) { firebaseAuthWithGoogle(account); }
            } catch (ApiException ignored) {
            }
        }
    }


    //-----------------------------------------
    //  구글 파이어베이스 연동 로그인 코드
    //-----------------------------------------
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(view.getContext(), getString(R.string.all_login_ok_msg), Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();

                        //서버로 플랫폼, 이메일, 닉네임 전달
                        checkUserList(PlaceConfig.PLATFORM_TYPE_GOOGLE,
                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                PlaceConfig.getTokenID(activity));

                        if (user != null) {
                            updateFragment();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(view.getContext(), getString(R.string.all_login_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //-----------------------------------------
    //  페이스북 파이어베이스 연동 코드
    //-----------------------------------------
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        //로그인이 성공 시
                        FirebaseUser user = mAuth.getCurrentUser();


                        //서버로 플랫폼, 이메일, 닉네임 전달
                        checkUserList(PlaceConfig.PLATFORM_TYPE_FACEBOOK,
                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                PlaceConfig.getTokenID(activity));

                        if (user != null) {
                            updateFragment();
                        }
                    }
                });
    }

    //-----------------------------------------
    //  카카오 로그인 코드
    //-----------------------------------------
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Toast.makeText(view.getContext(), getString(R.string.all_login_error_msg), Toast.LENGTH_SHORT).show();
            }
            updateFragment();                        // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }


    private void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                updateFragment();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                //성공 시
                //서버로 플랫폼, 이메일, 닉네임 전달
                checkUserList(PlaceConfig.PLATFORM_TYPE_KAKAO,
                        result.getKakaoAccount().getEmail(),
                        result.getKakaoAccount().getProfile().getNickname(),
                        PlaceConfig.getTokenID(activity));

                updateFragment();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) { activity.finish(); }
                else { updateFragment(); }
            }
        });
    }

    //--------------------------------------
    //  네이버 로그인 코드
    //--------------------------------------
    @SuppressLint("HandlerLeak")
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
//                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
//                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
//                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);

                updateFragment();
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);

                Toast.makeText(mContext, getString(R.string.all_login_error_msg), Toast.LENGTH_SHORT).show();
                PlaceConfig.WriteLog(getClass(),"errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc);
            }
        }
    };


    @SuppressLint("StaticFieldLeak")
    private class RequestApiTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginModule .getAccessToken(mContext);
            return mOAuthLoginModule .requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject response = jsonObject.getJSONObject("response");

                //서버로 플랫폼, 이메일, 닉네임 전달
                checkUserList(PlaceConfig.PLATFORM_TYPE_NAVER,
                        response.getString("email"),
                        response.getString("nickname"),
                        PlaceConfig.getTokenID(activity));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    //--------------------------
    //  화면 새로고침
    //--------------------------
    private void updateFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }



    //------------------------
    //  공유객체 정보삭제
    //------------------------
    private void removeSharedData(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("LOG_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("nickName");
        editor.remove("userNumber");
        editor.remove("platform");

        editor.apply();
    }


    //----------------------------------
    //  안드로이드 - 서버 JSON 통신
    //----------------------------------
    private void checkUserList(String platform, String userId, String nickname, String deviceId){

        try {
            JSONObject json = new JSONObject();

            json.put("userId",   userId);
            json.put("platform", platform);
            json.put("nickname", nickname);
            json.put("deviceId", deviceId);

//            ContentValues contentValues = new ContentValues();
//            contentValues.put("CheckData", String.valueOf(json));
            /* 변경점 : NetworkTasking -> sendRequest에 따른 ContentData 삭제 */
            sendRequest(PlaceConfig.PLACE_SETTING_CHKUSER_URL, json);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //--------------------------------
    //  Volley 라이브러리 네트워크 통신
    //--------------------------------
    /* 변경점 : NetworkTasking -> sendRequest */
    public void sendRequest(String url, JSONObject jsonObject){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    if(response == null){
                        PlaceConfig.WriteLog("[NETWORK ERROR] SETTINGS : RESULT = NULL");
                        return;
                    }else{
                        PlaceConfig.WriteLog("[NETWORK STATUS] SETTINGS : RECEIVE RESPONSE DATA FROM SERVER");
                    }

                    //-----------------------------------
                    //  서버에서 가져온 데이터 처리 코드
                    //-----------------------------------
                    try {
                        JSONArray jArr = new JSONArray(response);
                        JSONObject json = jArr.getJSONObject(0);

                        //---------------------------------------
                        //  공유객체에 로그인 정보를 저장
                        //---------------------------------------
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("LOG_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("nickName",   json.getString("nickname"));
                        editor.putString("userNumber", json.getString("userNumber"));
                        editor.putString("platform",   json.getString("platform"));

                        editor.apply();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> PlaceConfig.WriteLog("[NETWORK ERROR] SETTINGS : " + error.getMessage())
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("CheckData", String.valueOf(jsonObject));
                return params;
            }
        };

        request.setShouldCache(false);
        VolleyNetwork.requestQueue.add(request);
        PlaceConfig.WriteLog("[NETWORK STATUS] SETTINGS : SEND REQUEST DATA TO SERVER");
    }


}

