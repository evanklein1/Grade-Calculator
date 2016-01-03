package me.evanklein.finalexamcalculator;

/**
 * Created by Evan on 2015-12-29.
 */
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    public static String charset = "UTF-8";

    public JSONParser(){

    }
    public JSONObject makeHttpRequest(String url){
        try {
            android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
            android.os.StrictMode.setThreadPolicy(policy);
            URL myURL = new URL(url);
            HttpURLConnection con = (HttpURLConnection) myURL.openConnection();
            con.setRequestProperty("Accept-Charset",charset);
            int responseCode = con.getResponseCode();

            is = con.getInputStream();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            json = sb.toString();
            try {
                jobj = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        catch (MalformedURLException e) {
            // new URL() failed
            // ...
            e.printStackTrace();
        }
        catch (IOException e) {
            // openConnection() failed
            // ...
            Log.e("MYAPP", "exception", e);
            e.printStackTrace();
        }
        return jobj;

    }

}
