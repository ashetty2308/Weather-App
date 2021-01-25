package com.example.weatherapp;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.content.AsyncTaskLoader;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    EditText lat;
    EditText longitude;
    Button getWeather;
    Button button;
    TextView textViewCity0;
    TextView textViewCity1;
    TextView textViewCity2;
    TextView textViewCity0Weather;
    TextView textViewCity1Weather;
    TextView textViewCity2Weather;
    ImageView imageCity0;
    ImageView imageCity1;
    ImageView imageCity2;
    TextView weatherDescriptionCity0TV;
    TextView weatherDescriptionCity1TV;
    TextView weatherDescriptionCity2TV;
    TextView timeCity0TV;
    TextView timeCity1TV;
    TextView timeCity2TV;
    TextView dateCity0;
    TextView dateCity1;
    TextView dateCity2;
    ConstraintLayout layout;
    Spinner spinner;
    ArrayList<String> itemsForSpinner;
    private TextToSpeech tts;
    Button speak;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsForSpinner = new ArrayList<String>();
        getWeather = findViewById(R.id.getWeather);
        lat = findViewById(R.id.enterLat);
        longitude = findViewById(R.id.enterLong);
        button = findViewById(R.id.getWeather);
        textViewCity0 = findViewById(R.id.textView18);  
        textViewCity1 = findViewById(R.id.textView22);
        textViewCity2 = findViewById(R.id.textView20);
        textViewCity0Weather = findViewById(R.id.textView);
        textViewCity1Weather = findViewById(R.id.textView4);
        textViewCity2Weather = findViewById(R.id.textView9);
        imageCity0 = findViewById(R.id.imageView16);
        imageCity1 = findViewById(R.id.imageView18);
        imageCity2 = findViewById(R.id.imageView17);
        weatherDescriptionCity0TV = findViewById(R.id.textView2);
        weatherDescriptionCity1TV = findViewById(R.id.textView6);
        weatherDescriptionCity2TV = findViewById(R.id.textView8);
        layout =  (ConstraintLayout)findViewById(R.id.layout);
        timeCity0TV = findViewById(R.id.textView19);
        timeCity1TV = findViewById(R.id.textView23);
        timeCity2TV = findViewById(R.id.textView24);
        dateCity0 = findViewById(R.id.textView3);
        dateCity1 = findViewById(R.id.textView5);
        dateCity2 = findViewById(R.id.textView7);
        spinner = findViewById(R.id.spinner);
        speak = findViewById(R.id.speak);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    speak.setEnabled(true);
                }
            }
        });
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech();
            }
        });



        Toast.makeText(getApplicationContext(),"Welcome. Enter the desired latitude and longitude to find the weather for the 3 nearest cities to the coordinates.",Toast.LENGTH_LONG).show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFilesTask().execute();

            }
        });
        itemsForSpinner.add("C"+"\u00B0");
        itemsForSpinner.add("F"+ "\u00B0");

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,itemsForSpinner);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();


    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {

        JSONObject stringData = new JSONObject();
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String latCoor = lat.getText().toString();
                String longCoor = longitude.getText().toString();
                if(spinner.getSelectedItem().equals("C"+"\u00B0")){
                    URL url = new URL("http://api.openweathermap.org/data/2.5/find?lat="+latCoor+"&lon="+longCoor+"&cnt=3&appid=7e3eac24249fda8fc39ac0f291163bb2&units=metric");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String data = bufferedReader.readLine();
                    stringData = new JSONObject(data);

                }
                if(spinner.getSelectedItem().equals("F"+"\u00B0")){
                    URL url = new URL("http://api.openweathermap.org/data/2.5/find?lat="+latCoor+"&lon="+longCoor+"&cnt=3&appid=7e3eac24249fda8fc39ac0f291163bb2&units=imperial");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String data = bufferedReader.readLine();
                    stringData = new JSONObject(data);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

            try{

                String city0 = stringData.getJSONArray("list").getJSONObject(0).get("name").toString();
                String city1 = stringData.getJSONArray("list").getJSONObject(1).get("name").toString();
                String city2 = stringData.getJSONArray("list").getJSONObject(2).get("name").toString();

                String weather0 = stringData.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp").toString();
                String weather1 = stringData.getJSONArray("list").getJSONObject(1).getJSONObject("main").getString("temp").toString();
                String weather2 = stringData.getJSONArray("list").getJSONObject(2).getJSONObject("main").getString("temp").toString();

                textViewCity0.setText(city0);
                textViewCity1.setText(city1);
                textViewCity2.setText(city2);

                textViewCity0Weather.setText(weather0+"\u00B0");
                textViewCity1Weather.setText(weather1+"\u00B0");
                textViewCity2Weather.setText(weather2+"\u00B0");

                String weatherDescriptionCity0 = stringData.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
                String weatherDescriptionCity1 = stringData.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("main");
                String weatherDescriptionCity2 = stringData.getJSONArray("list").getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("main");

                weatherDescriptionCity0TV.setText(weatherDescriptionCity0);
                weatherDescriptionCity1TV.setText(weatherDescriptionCity1);
                weatherDescriptionCity2TV.setText(weatherDescriptionCity2);


                Long epochZero = stringData.getJSONArray("list").getJSONObject(0).getLong("dt");
                Long epochOne = stringData.getJSONArray("list").getJSONObject(1).getLong("dt");
                Long epochTwo = stringData.getJSONArray("list").getJSONObject(2).getLong("dt");


                Date dateCityZero = new Date(epochZero*1000);
                SimpleDateFormat newDate = new SimpleDateFormat("MM/dd/yyyy");
                String dateforcity0 = newDate.format(dateCityZero);
                dateCity0.setText(dateforcity0);

                Date dateCityOne = new Date(epochOne*1000);
                SimpleDateFormat newDateOne = new SimpleDateFormat("MM/dd/yyyy");
                String dateforcity1 = newDateOne.format(dateCityOne);
                dateCity1.setText(dateforcity1);

                Date dateCityTwo = new Date(epochTwo*1000);
                SimpleDateFormat newDateTwo = new SimpleDateFormat("MM/dd/yyyy");
                String dateforcity2 = newDateTwo.format(dateCityTwo);
                dateCity2.setText(dateforcity2);

                Date timeZero = new Date(epochZero*1000L);
                SimpleDateFormat timeCityZero = new SimpleDateFormat("hh:mm a");
                timeCityZero.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                String timeFinalCityZero = timeCityZero.format(timeZero);
                if(timeFinalCityZero.charAt(0)=='0'){
                    timeCity0TV.setText(timeFinalCityZero.substring(1) +" EST");

                }
                Date timeOne = new Date(epochZero*1000L);
                SimpleDateFormat timeCityOne = new SimpleDateFormat("hh:mm a");
                timeCityOne.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                String timeFinalCityOne = timeCityOne.format(timeOne);
                if(timeFinalCityOne.charAt(0)=='0'){
                    timeCity1TV.setText(timeFinalCityOne.substring(1) +" EST");

                }
                Date timeTwo = new Date(epochZero*1000L);
                SimpleDateFormat timeCityTwo = new SimpleDateFormat("hh:mm a");
                timeCityTwo.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                String timeFinalCityTwo = timeCityTwo.format(timeTwo);
                if(timeFinalCityTwo.charAt(0)=='0'){
                    timeCity2TV.setText(timeFinalCityTwo.substring(1) +" EST");

                }
                //Clear
                if(weatherDescriptionCity0.contains("Clear")){
                    if(timeCity0TV.getText().toString().contains("PM")){
                        Picasso.get().load("http://openweathermap.org/img/wn/01n@2x.png").into(imageCity0);
                        layout.setBackgroundResource(R.drawable.clearsky);
                        textViewCity0Weather.setTextColor(Color.WHITE);
                        textViewCity1Weather.setTextColor(Color.WHITE);
                        textViewCity2Weather.setTextColor(Color.WHITE);
                        textViewCity0.setTextColor(Color.WHITE);
                        textViewCity1.setTextColor(Color.WHITE);
                        textViewCity2.setTextColor(Color.WHITE);
                        timeCity0TV.setTextColor(Color.WHITE);
                        timeCity1TV.setTextColor(Color.WHITE);
                        timeCity2TV.setTextColor(Color.WHITE);
                        weatherDescriptionCity0TV.setTextColor(Color.WHITE);
                        weatherDescriptionCity1TV.setTextColor(Color.WHITE);
                        weatherDescriptionCity2TV.setTextColor(Color.WHITE);
                        dateCity0.setTextColor(Color.WHITE);
                        dateCity1.setTextColor(Color.WHITE);
                        dateCity2.setTextColor(Color.WHITE);
                        lat.setTextColor(Color.WHITE);
                        longitude.setTextColor(Color.WHITE);

                    }
                    if(timeCity0TV.getText().toString().contains("AM")){
                        Picasso.get().load("http://openweathermap.org/img/wn/01d@2x.png").into(imageCity0);
                    }
                }
                if(weatherDescriptionCity1.contains("Clear")){
                    if(timeCity1TV.getText().toString().contains("PM")){
                        Picasso.get().load("http://openweathermap.org/img/wn/01n@2x.png").into(imageCity1);
                    }
                    if(timeCity1TV.getText().toString().contains("AM")){
                        Picasso.get().load("http://openweathermap.org/img/wn/01d@2x.png").into(imageCity1);
                    }
                }
                if(weatherDescriptionCity2.contains("Clear")){
                    if(timeCity2TV.getText().toString().contains("PM")){
                        Picasso.get().load("http://openweathermap.org/img/wn/01n@2x.png").into(imageCity2);
                    }
                    if(timeCity2TV.getText().toString().contains("AM")){
                        Picasso.get().load("http://openweathermap.org/img/wn/01d@2x.png").into(imageCity2);
                    }
                }


                //group 8
                if(weatherDescriptionCity0.contains("Cloud")){
                    Picasso.get().load("http://openweathermap.org/img/wn/03d@2x.png").into(imageCity0);
                    layout.setBackgroundResource(R.drawable.cloudpics);
                    textViewCity0Weather.setTextColor(Color.BLACK);
                    textViewCity1Weather.setTextColor(Color.BLACK);
                    textViewCity2Weather.setTextColor(Color.BLACK);
                    textViewCity0.setTextColor(Color.BLACK);
                    textViewCity1.setTextColor(Color.BLACK);
                    textViewCity2.setTextColor(Color.BLACK);
                    timeCity0TV.setTextColor(Color.BLACK);
                    timeCity1TV.setTextColor(Color.BLACK);
                    timeCity2TV.setTextColor(Color.BLACK);
                    weatherDescriptionCity0TV.setTextColor(Color.BLACK);
                    weatherDescriptionCity1TV.setTextColor(Color.BLACK);
                    weatherDescriptionCity2TV.setTextColor(Color.BLACK);
                    dateCity0.setTextColor(Color.BLACK);
                    dateCity1.setTextColor(Color.BLACK);
                    dateCity2.setTextColor(Color.BLACK);
                    lat.setTextColor(Color.BLACK);
                    longitude.setTextColor(Color.BLACK);
                }
                if(weatherDescriptionCity1.contains("Cloud")){
                    Picasso.get().load("http://openweathermap.org/img/wn/03d@2x.png").into(imageCity1);
                }
                if(weatherDescriptionCity2.contains("Cloud")){
                    Picasso.get().load("http://openweathermap.org/img/wn/03d@2x.png").into(imageCity2);
                }
                ArrayList<String> group7 = new ArrayList<String>();
                group7.add("Mist");
                group7.add("Smoke");
                group7.add("Haze");
                group7.add("Dust");
                group7.add("Fog");
                group7.add("Sand");
                group7.add("Dust");
                group7.add("Ash");
                group7.add("Squall");
                group7.add("Tornado");

                for(int i = 0; i < group7.size(); i++){
                    if(weatherDescriptionCity0.contains(group7.get(i))){
                        Picasso.get().load("http://openweathermap.org/img/wn/50d@2x.png").into(imageCity0);

                    }
                    if(weatherDescriptionCity1.contains(group7.get(i))){
                        Picasso.get().load("http://openweathermap.org/img/wn/50d@2x.png").into(imageCity1);
                    }
                    if(weatherDescriptionCity2.contains(group7.get(i))){
                        Picasso.get().load("http://openweathermap.org/img/wn/50d@2x.png").into(imageCity2);
                    }
                }
                //group 6
                if(weatherDescriptionCity0.contains("Snow")){
                    Picasso.get().load("http://openweathermap.org/img/wn/13d@2x.png").into(imageCity0);
                    layout.setBackgroundResource(R.drawable.snowwallpapertwo);
                    textViewCity0Weather.setTextColor(Color.BLACK);
                    textViewCity1Weather.setTextColor(Color.BLACK);
                    textViewCity2Weather.setTextColor(Color.BLACK);
                    textViewCity0.setTextColor(Color.BLACK);
                    textViewCity1.setTextColor(Color.BLACK);
                    textViewCity2.setTextColor(Color.BLACK);
                    timeCity0TV.setTextColor(Color.BLACK);
                    timeCity1TV.setTextColor(Color.BLACK);
                    timeCity2TV.setTextColor(Color.BLACK);
                    weatherDescriptionCity0TV.setTextColor(Color.BLACK);
                    weatherDescriptionCity1TV.setTextColor(Color.BLACK);
                    weatherDescriptionCity2TV.setTextColor(Color.BLACK);
                    dateCity0.setTextColor(Color.BLACK);
                    dateCity1.setTextColor(Color.BLACK);
                    dateCity2.setTextColor(Color.BLACK);
                    lat.setTextColor(Color.BLACK);
                    longitude.setTextColor(Color.BLACK);
                }
                if(weatherDescriptionCity1.contains("Snow")){
                    Picasso.get().load("http://openweathermap.org/img/wn/13d@2x.png").into(imageCity1);
                }
                if(weatherDescriptionCity2.contains("Snow")){
                    Picasso.get().load("http://openweathermap.org/img/wn/13d@2x.png").into(imageCity2);
                }
                //group 5 Rain
                if(weatherDescriptionCity0.contains("Rain")){
                    Picasso.get().load("http://openweathermap.org/img/wn/09d@2x.png").into(imageCity0);
                    layout.setBackgroundResource(R.drawable.rain);
                    textViewCity0Weather.setTextColor(Color.WHITE);
                    textViewCity1Weather.setTextColor(Color.WHITE);
                    textViewCity2Weather.setTextColor(Color.WHITE);
                    textViewCity0.setTextColor(Color.WHITE);
                    textViewCity1.setTextColor(Color.WHITE);
                    textViewCity2.setTextColor(Color.WHITE);
                    timeCity0TV.setTextColor(Color.WHITE);
                    timeCity1TV.setTextColor(Color.WHITE);
                    timeCity2TV.setTextColor(Color.WHITE);
                    weatherDescriptionCity0TV.setTextColor(Color.WHITE);
                    weatherDescriptionCity1TV.setTextColor(Color.WHITE);
                    weatherDescriptionCity2TV.setTextColor(Color.WHITE);
                    dateCity0.setTextColor(Color.WHITE);
                    dateCity1.setTextColor(Color.WHITE);
                    dateCity2.setTextColor(Color.WHITE);
                    lat.setTextColor(Color.WHITE);
                    longitude.setTextColor(Color.WHITE);
                }
                if(weatherDescriptionCity1.contains("Rain")){
                    Picasso.get().load("http://openweathermap.org/img/wn/09d@2x.png").into(imageCity1);
                }
                if(weatherDescriptionCity2.contains("Rain")){
                    Picasso.get().load("http://openweathermap.org/img/wn/09d@2x.png").into(imageCity2);
                }
                //group 3
                 if(weatherDescriptionCity0.contains("Drizzle")){
                    Picasso.get().load("http://openweathermap.org/img/wn/09d@2x.png").into(imageCity0);
                    layout.setBackgroundResource(R.drawable.drizzle);

                    textViewCity0Weather.setTextColor(Color.rgb(255,20,147));
                    textViewCity1Weather.setTextColor(Color.rgb(255,20,147));
                    textViewCity2Weather.setTextColor(Color.rgb(255,20,147));
                    textViewCity0.setTextColor(Color.rgb(255,20,147));
                    textViewCity1.setTextColor(Color.rgb(255,20,147));
                    textViewCity2.setTextColor(Color.rgb(255,20,147));
                    timeCity0TV.setTextColor(Color.rgb(255,20,147));
                    timeCity1TV.setTextColor(Color.rgb(255,20,147));
                    timeCity2TV.setTextColor(Color.rgb(255,20,147));
                    weatherDescriptionCity0TV.setTextColor(Color.rgb(255,20,147));
                    weatherDescriptionCity1TV.setTextColor(Color.rgb(255,20,147));
                    weatherDescriptionCity2TV.setTextColor(Color.rgb(255,20,147));
                    dateCity0.setTextColor(Color.rgb(255,20,147));
                    dateCity1.setTextColor(Color.rgb(255,20,147));
                    dateCity2.setTextColor(Color.rgb(255,20,147));
                    lat.setTextColor(Color.rgb(255,20,147));
                    longitude.setTextColor(Color.rgb(255,20,147));
                }
                if(weatherDescriptionCity1.contains("Drizzle")){
                    Picasso.get().load("http://openweathermap.org/img/wn/09d@2x.png").into(imageCity1);
                }
                if(weatherDescriptionCity2.contains("Drizzle")){
                    Picasso.get().load("http://openweathermap.org/img/wn/09d@2x.png").into(imageCity2);
                }
                //group 2
                if(weatherDescriptionCity0.contains("Thunderstorm")){
                    Picasso.get().load("http://openweathermap.org/img/wn/11d@2x.png").into(imageCity0);
                    layout.setBackgroundResource(R.drawable.thunderstormtwo);
                    textViewCity0Weather.setTextColor(Color.WHITE);
                    textViewCity1Weather.setTextColor(Color.WHITE);
                    textViewCity2Weather.setTextColor(Color.WHITE);
                    textViewCity0.setTextColor(Color.WHITE);
                    textViewCity1.setTextColor(Color.WHITE);
                    textViewCity2.setTextColor(Color.WHITE);
                    timeCity0TV.setTextColor(Color.WHITE);
                    timeCity1TV.setTextColor(Color.WHITE);
                    timeCity2TV.setTextColor(Color.WHITE);
                    weatherDescriptionCity0TV.setTextColor(Color.WHITE);
                    weatherDescriptionCity1TV.setTextColor(Color.WHITE);
                    weatherDescriptionCity2TV.setTextColor(Color.WHITE);
                    dateCity0.setTextColor(Color.WHITE);
                    dateCity1.setTextColor(Color.WHITE);
                    dateCity2.setTextColor(Color.WHITE);
                    lat.setTextColor(Color.WHITE);
                    longitude.setTextColor(Color.WHITE);
                }
                if(weatherDescriptionCity1.contains("Thunderstorm")){
                    Picasso.get().load("http://openweathermap.org/img/wn/11d@2x.png").into(imageCity1);
                }
                if(weatherDescriptionCity2.contains("Thunderstorm")){
                    Picasso.get().load("http://openweathermap.org/img/wn/11d@2x.png").into(imageCity2);
                }
                
                }catch(JSONException e){
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
    }
    private void textToSpeech(){

        String tellDate = dateCity0.getText().toString().substring(3,5);
        int fifth = Integer.valueOf(tellDate)-5;

        if(tellDate.charAt(1)=='1'){
            tts.speak("Today is the "+tellDate+"st",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='2'){
            tts.speak("Today is the "+tellDate+"nd",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='3'){
            tts.speak("Today is the "+tellDate+"rd",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='4'){
            tts.speak("Today is the "+tellDate+"th",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='5'){
            tts.speak("Today is the "+fifth+"fifth",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='6'){
            tts.speak("Today is the "+tellDate+"th",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='7'){
            tts.speak("Today is the "+tellDate+"th",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='8'){
            tts.speak("Today is the "+tellDate+"th",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDate.charAt(1)=='9'){
            tts.speak("Today is the "+tellDate+"th",TextToSpeech.QUEUE_ADD,null);
        }
        String tellTime = timeCity0TV.getText().toString();
        tts.speak("The time is "+tellTime,TextToSpeech.QUEUE_ADD,null);


        String tellNameCity0 = textViewCity0.getText().toString();
        tts.speak(tellNameCity0,TextToSpeech.QUEUE_ADD,null);

        String tellWeatherCity0 = textViewCity0Weather.getText().toString();
        if(spinner.getSelectedItem().equals("C"+"\u00B0")){
            tts.speak("In "+textViewCity0.getText().toString()+"The weather is "+tellWeatherCity0+"celsius",TextToSpeech.QUEUE_ADD,null);
        }
        else{
            tts.speak("In "+textViewCity0.getText().toString()+"The weather is "+tellWeatherCity0+"fahrenheit",TextToSpeech.QUEUE_ADD,null);
        }
        String tellDescriptionCity0 = weatherDescriptionCity0TV.getText().toString();

        if(tellDescriptionCity0.equals("Mist")){
            tts.speak("The weather description is that it is "+tellDescriptionCity0+"eeeeeeee",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Smoke")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Haze")){
            tts.speak("The weather description is that there is a "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Dust")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Fog")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Sand")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Dust")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Ash")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Squall")){
            tts.speak("The weather description is that there is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Tornado")){
            tts.speak("The weather description is that there is a "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.contains("Cloud")){
            tts.speak("The weather description is that there are "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.equals("Clear")){
            tts.speak("The weather description is that it is "+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.contains("Rain")){
            tts.speak("The weather description is that it is "+tellDescriptionCity0+"ning",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.contains("Drizzle")){
            tts.speak("The weather description is that it is "+tellDescriptionCity0+"illining",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity0.contains("Thunderstorm")){
            tts.speak("The weather description is that there is a"+tellDescriptionCity0,TextToSpeech.QUEUE_ADD,null);
        }

        String tellNameCity1 = textViewCity1.getText().toString();
        tts.speak(tellNameCity1,TextToSpeech.QUEUE_ADD,null);
        String tellWeatherCity1 = textViewCity1Weather.getText().toString();
        if(spinner.getSelectedItem().equals("C"+"\u00B0")){
            tts.speak("In "+textViewCity1.getText().toString()+"The weather is "+tellWeatherCity1+"celsius",TextToSpeech.QUEUE_ADD,null);
        }
        else{
            tts.speak("In "+textViewCity1.getText().toString()+"The weather is "+tellWeatherCity1+"fahrenheit",TextToSpeech.QUEUE_ADD,null);
        }
        String tellDescriptionCity1 = weatherDescriptionCity1TV.getText().toString();
        if(tellDescriptionCity1.equals("Mist")){
            tts.speak("The weather description is that it is "+tellDescriptionCity1+"eeeeeeee",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Smoke")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Haze")){
            tts.speak("The weather description is that there is a "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Dust")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Fog")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Sand")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Dust")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Ash")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Squall")){
            tts.speak("The weather description is that there is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Tornado")){
            tts.speak("The weather description is that there is a "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.contains("Cloud")){
            tts.speak("The weather description is that it there are "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.equals("Clear")){
            tts.speak("The weather description is that it is "+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.contains("Rain")){
            tts.speak("The weather description is that it is "+tellDescriptionCity1+"ning",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.contains("Drizzle")){
            tts.speak("The weather description is that it is "+tellDescriptionCity1+"illining",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity1.contains("Thunderstorm")){
            tts.speak("The weather description is that there is a"+tellDescriptionCity1,TextToSpeech.QUEUE_ADD,null);
        }

        String tellNameCity2 = textViewCity2.getText().toString();
        tts.speak(tellNameCity2,TextToSpeech.QUEUE_ADD,null);


        String tellWeatherCity2 = textViewCity2Weather.getText().toString();
        if(spinner.getSelectedItem().equals("C"+"\u00B0")){
            tts.speak("In "+textViewCity2.getText().toString()+"The weather is "+tellWeatherCity2+"celsius",TextToSpeech.QUEUE_ADD,null);
        }
        else{
            tts.speak("In "+textViewCity2.getText().toString()+"The weather is "+tellWeatherCity2+"fahrenheit",TextToSpeech.QUEUE_ADD,null);
        }

        String tellDescriptionCity2 = weatherDescriptionCity2TV.getText().toString();

        if(tellDescriptionCity2.equals("Mist")){
            tts.speak("The weather description is that it is "+tellDescriptionCity2+"eeeeeeee",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Smoke")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Haze")){
            tts.speak("The weather description is that there is a "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Dust")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Fog")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Sand")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Dust")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Ash")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Squall")){
            tts.speak("The weather description is that there is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Tornado")){
            tts.speak("The weather description is that there is a "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.contains("Cloud")){
            tts.speak("The weather description is that there are "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.equals("Clear")){
            tts.speak("The weather description is that it is "+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.contains("Rain")){
            tts.speak("The weather description is that it is "+tellDescriptionCity2+"ning",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.contains("Drizzle")){
            tts.speak("The weather description is that it is "+tellDescriptionCity2+"illining",TextToSpeech.QUEUE_ADD,null);
        }
        if(tellDescriptionCity2.contains("Thunderstorm")){
            tts.speak("The weather description is that there is a"+tellDescriptionCity2,TextToSpeech.QUEUE_ADD,null);
        }
    }

    @Override
    protected void onDestroy() {
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

