package envyandroid.org.graduationproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

//앱 설정과 관련된 정보 모아놓은 클래 (프리페런스 읽고쓰기, URL 정보)
public class PlaceConfig {
    //true 로그남김, false 로그 안남김
    public static final boolean DEBUG = true;
    // 로그 TAG
    public static final String TAG = "PlaceSensor_";

    // server 주소
    public static final String ServerIP = "http://3.16.183.218/";

    // PlatForm 종류
    public static final String PLATFORM_TYPE_NAVER = "NAVER";
    public static final String PLATFORM_TYPE_FACEBOOK = "FACEBOOK";
    public static final String PLATFORM_TYPE_GOOGLE = "GOOGLE";
    public static final String PLATFORM_TYPE_KAKAO = "KAKAO";

    //URL 주소
    public static final String PLACE_HOME_MAINPAGE_URL = ServerIP + "Application/mainPage.jsp";
    public static final String PLACE_SETTING_CHKUSER_URL = ServerIP + "Application/checkUserList.jsp";
    public static final String PLACE_SETTING_FAVORITE_LIST_URL = ServerIP + "Application/favoriteList.jsp";
    public static final String PLACE_SETTING_FAVORITE_PROCESS_URL = ServerIP + "Application/favoriteProcess.jsp";
    public static final String PLACE_SETTING_NOTICE_URL = ServerIP + "Application/notice.jsp";
    public static final String PLACE_COMMUNITY_COMMENT_INSERT_URL = ServerIP + "Application/commentInsert.jsp";
    public static final String PLACE_COMMUNITY_COMMENT_LIST_URL = ServerIP + "Application/commentList.jsp";
    public static final String PLACE_COMMUNITY_COMMENT_PROCESS_URL = ServerIP + "Application/commentProcess.jsp";
    public static final String PLACE_COMMUNITY_RECOMMEND_PROCESS_URL = ServerIP + "Application/recommendProcess.jsp";
    public static final String PLACE_COMMUNITY_REVIEW_CONTENT_URL = ServerIP + "Application/reviewContent.jsp";
    public static final String PLACE_COMMUNITY_SEARCH_COMMUNITY_URL = ServerIP + "Application/searchCommunity.jsp";

    //파라미터 형식
    public static final String[] PLACE_HOME_MAINPAGE_PARAM = {"interestPoint"};
    public static final String[] PLACE_SETTING_CHKUSER_PARAM = {"userId","platform","nickname"};
    public static final String[] PLACE_SETTING_FAVORITE_LIST_PARAM = {"userNumber"};
    public static final String[] PLACE_SETTING_FAVORITE_PROCESS_PARAM = {"contentId","userNumber"};
    public static final String[] PLACE_SETTING_NOTICE_PARAM = {"reviewId"};
    public static final String[] PLACE_COMMUNITY_COMMENT_INSERT_PARAM = {"reviewId","userNumber","commentContent"};
    public static final String[] PLACE_COMMUNITY_COMMENT_LIST_PARAM = {"reviewId"};
    public static final String[] PLACE_COMMUNITY_COMMENT_PROCESS_PARAM = {"processData"};
    public static final String[] PLACE_COMMUNITY_RECOMMEND_PROCESS_PARAM = {"contentId","userNumber"};
    public static final String[] PLACE_COMMUNITY_REVIEW_CONTENT_PARAM = {"reviewId"};
    public static final String[] PLACE_COMMUNITY_SEARCH_COMMUNITY_PARAM = {"searchText","userNumber","sort","paging"};

    //MainActivity 하단 메뉴 id
    public static final String Main_bottom_menu_home = "R.id.menu_home";
    public static final String Main_bottom_menu_community = "R.id.menu_community";
    public static final String Main_bottom_menu_plan = "R.id.menu_plan";
    public static final String Main_bottom_menu_settings = "R.id.menu_settings";

    //FireBaseMessage KEY 값
    public static final String PLACE_MSG_NOTICE_KEY="PLACE_MSG_NOTICE";
    public static final String PLACE_MSG_COMMENT_KEY="PLACE_MSG_COMMENT";

    //SharePreference KEY 값
    public static final String PREFS_USER_TOKEN_ID = "PLACE_USER_TOKEN_ID";
    public static final String PREFS_USER_ID = "PLACE_USER_ID";
    public static final String PREFS_USER_NICKNAME = "PLACE_USER_NICKNAME";
    public static final String PREFS_USER_NUMBER = "PLACE_USER_NUMBER";
    //public static final String PREFS_USER_PLATFORM = "PLACE_USER_PLATFORM";


    // 코드 작성된 액티비티파일  이름 구함 Context 부분 this나 getApplicationContext() 사용
    public static void WriteLog(Context c, String body) {
        // TAG에 실행 액티비티명 포함
        if (DEBUG) Log.d(TAG+ ((Activity)c).getLocalClassName(),body);
    }
    // 코드 작성된 프래그먼트파일 이름 구함 getClass() 사용
    public static void WriteLog(Class c, String body) {
        // 코드 실행된 프래그먼트 이름 구하기용
        if (DEBUG) Log.d(TAG + c.getSimpleName(),body);
    }

    public static void WriteLog(String body) {
        // TAG에 실행 액티비티 미포함 내용만 출력
        if (DEBUG) Log.d(TAG,body);
    }


    // FirebaseMessage 관련 Token ID 프리페런스 값 불러오기
    public static String getTokenID(Context c){
        WriteLog(c,"getTokenID() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        return mPrefs.getString(PlaceConfig.PREFS_USER_TOKEN_ID,"");
    }
    // FirebaseMessage 관련 Token ID 프리페런스 저장
    public static void setTokenID(Context c, String token){
        WriteLog("setTokenID() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mPrefs.edit().putString(PlaceConfig.PREFS_USER_TOKEN_ID,token).commit();
    }

    // NICKNAME 불러오기
    public static String getUserNickName(Context c){
        WriteLog("getUserNickName() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        return mPrefs.getString(PlaceConfig.PREFS_USER_NICKNAME,"");
    }
    // NICKNAME 저장
    public static void setUserNickName(Context c, String nickname){
        WriteLog("setUserNickName() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mPrefs.edit().putString(PlaceConfig.PREFS_USER_NICKNAME,nickname).commit();
    }

    // NICKNAME 불러오기
    public static String getUserID(Context c){
        WriteLog("getUserID() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        return mPrefs.getString(PlaceConfig.PREFS_USER_ID,"");
    }
    // NICKNAME 저장
    public static void setUserID(Context c, String nickname){
        WriteLog("setUserID() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mPrefs.edit().putString(PlaceConfig.PREFS_USER_ID,nickname).commit();
    }

    // NICKNAME 불러오기
    public static String getUserNumber(Context c){
        WriteLog("getUserNumber() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        return mPrefs.getString(PlaceConfig.PREFS_USER_NUMBER,"");
    }
    // NICKNAME 저장
    public static void setUserNumber(Context c, String nickname){
        WriteLog("setUserNumber() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mPrefs.edit().putString(PlaceConfig.PREFS_USER_NUMBER,nickname).commit();
    }
    //로그인 성공된 정보 제거
    public static void removeLoginUserData(Context c){
        WriteLog("removeLoginUserData() Call");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mPrefs.edit().remove(PlaceConfig.PREFS_USER_ID).commit();
        mPrefs.edit().remove(PlaceConfig.PREFS_USER_NICKNAME).commit();
        mPrefs.edit().remove(PlaceConfig.PREFS_USER_NUMBER).commit();
    }
}
