package com.usc.cargotrackingsystem;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kevin on 1/24/2017.
 */

public class PHPHelper {

    public static String phpGet(String link){

        String result = null;

        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            InputStream inputStream = connection.getInputStream();
            StringBuffer sb = new StringBuffer("");

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }
}
