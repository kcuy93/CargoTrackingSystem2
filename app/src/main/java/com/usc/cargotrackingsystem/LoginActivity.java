package com.usc.cargotrackingsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.usc.cargotrackingsystem.POJO.Driver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText username, password;
    String hostname = "";
    String  portNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on click login button verify
                login();
/*                if(login()){

                }else{
                    Toast.makeText(LoginActivity.this, "Login Failed!" , Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }

    public boolean login(){

        new AsyncTask<Void, Void, Void>(){

            ProgressDialog dialog;
            String user, pw, resultString;
            JSONArray jsonArray = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(LoginActivity.this, "Log in",
                        "Authenticating...", true);

                user = username.getText().toString();
                pw = password.getText().toString();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                try {

                    String address;

                    //retrieve host and port from preferences

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    hostname = sharedPref.getString("hostname", "");
                    portNumber = sharedPref.getString("portNumber", "");

                    if(portNumber.isEmpty())
                        address = hostname;
                    else
                        address = hostname + ":" + portNumber;

                    String link="http://"+ address + "/login.php";
                    String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                            URLEncoder.encode(user, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                            URLEncoder.encode(pw, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    resultString = sb.toString();

                } catch(Exception e){
                    Log.e("Login", e.getMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Driver driver = null;

                try {
                    if (resultString != null) {

                        JSONObject jsonObj = new JSONObject(resultString);

                        jsonArray = jsonObj.getJSONArray("result");

                        if(jsonArray!=null && !jsonArray.isNull(0)){
                            JSONObject c = jsonArray.getJSONObject(0);

                            driver = new Driver();

                            driver.setId(c.getString(Driver.ID_TAG));
                            driver.setFname(c.getString(Driver.FNAME_TAG));
                            driver.setLname(c.getString(Driver.LNAME_TAG));
                            driver.setMname(c.getString(Driver.MNAME_TAG));
                            driver.setLicense(c.getString(Driver.LICENSE_TAG));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(driver!=null) {
                    //driver is not null, login successful
                    Toast.makeText(LoginActivity.this, "Login Success!: " + driver.getFname(), Toast.LENGTH_SHORT).show();
/*                    Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
                    startActivity(intent);*/
                }else{
                    //driver is null, login failed!
                    Toast.makeText(LoginActivity.this, "Login Failed!" , Toast.LENGTH_SHORT).show();
                }

                if(dialog!=null)
                    dialog.dismiss();
            }
        }.execute();



        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId()){
            case R.id.settings: {
                Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
            break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
