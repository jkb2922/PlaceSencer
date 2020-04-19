package envyandroid.org.graduationproject.AddLibraray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ImageLoader {

    private final String serverUrl = "http://3.16.183.218/image/";

    public ImageLoader() {
        new ThreadPolicy();
    }

    public Bitmap getBitmapImg(String imgStr){

        Bitmap bitmapImg = null;

        try{
            URL url = new URL(serverUrl + URLEncoder.encode(imgStr,"utf-8"));

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setDoInput(true);
            conn.connect();

            InputStream inputStream= conn.getInputStream();
            bitmapImg = BitmapFactory.decodeStream(inputStream);

        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        return bitmapImg;
    }
}
