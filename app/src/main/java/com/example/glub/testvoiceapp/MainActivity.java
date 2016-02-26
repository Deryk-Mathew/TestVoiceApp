package com.example.glub.testvoiceapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button pushToTalk;
    private Button pushToTalk2;
    private Button pushToPlayback;
    private Button queryButton;

    private TextView textBox;
    private TextView textBox2;
    private TextToSpeech tts;

    // DB stuff
    private DatabaseHelper database = new DatabaseHelper(MainActivity.this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Boxes
        textBox = (TextView)findViewById(R.id.speechOutPutBox);
        textBox2 = (TextView)findViewById(R.id.dbOutputBox);

        // Buttons
        pushToTalk = (Button)findViewById(R.id.pushToTalk);
        //pushToTalk2 = (Button)findViewById(R.id.pushToTalk2);
        pushToPlayback = (Button)findViewById(R.id.pushToTalk2);
        queryButton = (Button) findViewById(R.id.queryButton);


        tts = new TextToSpeech(MainActivity.this, MainActivity.this);

        pushToTalk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-us");

                try{
                    startActivityForResult(intent, 1);

                    //textBox.setText("");

                }catch(ActivityNotFoundException exception){
                    Toast toast = Toast.makeText(getApplicationContext(),"Your application has failed!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        });

        pushToPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tts.isSpeaking()){
                    HashMap<String, String>  params = new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"Sampletext");
                    tts.speak(textBox.getText().toString(), TextToSpeech.QUEUE_ADD,params);
                } else {
                    tts.stop();
                }
            }


        });
        // SQL test query
        queryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Query LUIS
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(luisTestQuery());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    textBox.setText(jsonObj.toString(3));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Query DB
                Cursor cursor = database.executeTestQuery("Starbucks");
                if (cursor.moveToFirst()) {
                    String result = cursor.getString(cursor.getColumnIndex("shop_id"));
                    result += ", " + cursor.getString(cursor.getColumnIndex("shop_name"));
                    result += ", " + cursor.getString(cursor.getColumnIndex("shop_type"));
                    result += ", " + cursor.getString(cursor.getColumnIndex("shop_x"));
                    result += ", " + cursor.getString(cursor.getColumnIndex("shop_y"));
                    textBox2.setText(result);
                }
            }
        });
    }

    private String luisTestQuery() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL returnedJSON = null;
        try {
            returnedJSON = new URL("https://api.projectoxford.ai/luis/v1/application?id=a1a41692-96f0-47ce-afe4-a1386b20d42c&subscription-key=69d39a473ab2473b8660eb917d6c0c97&q=how%20do%20I%20get%20to%20starbucks");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            returnedJSON.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputLine;
        StringBuilder builder = new StringBuilder();

        try {
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
                System.out.println(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(builder.toString());
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
    public void onButtonClick(View v){
        if(v.getId() == R.id.pushToTalk2)
        {
            promptSpeechInput();
        }
    }


    private void promptSpeechInput() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!");

        try {
            startActivityForResult(i, 100);
        }
        catch(ActivityNotFoundException a)
        {
            Toast.makeText(MainActivity.this,"Fucked!", Toast.LENGTH_SHORT).show();
        }
    }
    */
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
            switch(requestCode){

                // PushToTalk1
                case 1:{
                    if(requestCode== RESULT_OK && data != null){
                        ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        textBox.setText(text.get(0));
                    }
                }
                // PushToTalk2
                    /*
                case 100: {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textBox.setText(result.get(0));
                }
                */
                default:
                    break;
            }
    }

    @Override
    public void onInit(int status) {

    }
}
