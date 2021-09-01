package com.example.weatherapp;
import android.content.Context;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;


public class MainActivity extends AppCompatActivity {
    EditText cityNAme;
    TextView textView;
    String city;

    public void clearAll(View view){
        cityNAme.setText("");
        textView.setText("");
        city = "";

    }
    public void clicked(View view){
        city  = cityNAme.getText().toString();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityNAme.getWindowToken(),0);
        final String[] result = {""};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String urls = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=631ae1b724080c5a42def3cbe78a64f2";
        executor.execute(() -> {
            try {
                URL url = new URL(urls);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                int data = reader.read();
                Log.i("urlConnection",urlConnection.toString());
                while (data!=-1){
                    char current = (char) data;
                    result[0] += current;
                    data = reader.read();
                }
                Log.i("result",result[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Background work here

            boolean post = handler.post(() -> {
                try {
                    JSONObject json = new JSONObject(result[0]);
                    String weather = json.getString("weather");
                    String temp = "["+json.getString("main")+"]";
                    Log.i("weather",temp);
                    JSONArray arr = new JSONArray(weather);
                    JSONArray arr1 = new JSONArray(temp);
                    String s = "";
                    for(int i = 0;i<arr.length();i++)
                    {
                        JSONObject jsonPart = arr.getJSONObject(i);
                        s = s+"main:\t"+jsonPart.getString("main") +"\ndescription:\t" + jsonPart.getString("description");
                    }
                    for(int i = 0;i<arr1.length();i++)
                    {
                        JSONObject jsonPart = arr1.getJSONObject(i);
                        double temp1 = Math.round((Double.parseDouble(jsonPart.getString("temp")) - 273.15d)*100)/100.00;
                        double feels_like = Math.round((Double.parseDouble(jsonPart.getString("feels_like"))- 273.15d)*100)/100.00;
                        double minTemp = Math.round((Double.parseDouble(jsonPart.getString("temp_min"))- 273.15d)*100)/100.00;
                        double maxTemp = Math.round((Double.parseDouble(jsonPart.getString("temp_max"))- 273.15d)*100)/100.00;
                        s = s+"\ntemp:\t"+temp1+" °C\nfeels like:\t"+ feels_like
                                +" °C\nMin temp:\t"+minTemp
                                +" °C\nMax temp:\t"+maxTemp+" °C\npressure:\t" + jsonPart.getString("pressure")
                                +" hPa\nhumidity:\t" + jsonPart.getString("humidity") + "%";
                    }
                    textView.setText(s);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("error",e.toString());
                    Toast.makeText(MainActivity.this,"Sorry, could not find the city :(",Toast.LENGTH_SHORT).show();
                    cityNAme.setText("");
                    city = "";
                }//UI Thread work here
            });
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityNAme = findViewById(R.id.cityName);
        textView = findViewById(R.id.textView);
    }
}
