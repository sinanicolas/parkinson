package com.example.getaccgyrapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static android.hardware.Sensor.TYPE_GYROSCOPE;

public class MainActivity extends AppCompatActivity {
    TextView textX, textY, textZ;

    Button simpleButton1, simpleButton2;
    //TextView textXg, textYg,textZg;
    TextView content;
    TextView timerView;
    TextView stepC;
    SensorManager sensorManager;
    boolean checkSensor = false;
    File file;
    public int counter;
    Sensor sensor, sensor2;
    boolean isOn = false;
    MediaPlayer mediaPlayer;
    private double MagnitudePrevious = 0;
    private int cpt = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor2 = sensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textZ = findViewById(R.id.textZ);
        timerView = findViewById(R.id.timer);
        stepC = findViewById(R.id.step);
        simpleButton1 = (Button) findViewById(R.id.buttonBegin);//get id of button 1
        simpleButton2 = (Button) findViewById(R.id.buttonStop);//get id of button 2

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mario);
        content = (TextView) findViewById(R.id.textView);

        simpleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Write began...", Toast.LENGTH_LONG).show();//display the text of button1
                isOn=true;
                new CountDownTimer(20000, 1000){
                    public void onTick(long millisUntilFinished){
                        timerView.setText(String.valueOf(counter));
                        counter++;
                    }
                    public  void onFinish(){
                        timerView.setText("FINISH!!");
                        isOn = false;

                        mediaPlayer.start();
                    }
                }.start();
            }
        });
        simpleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Process stopped", Toast.LENGTH_LONG).show();//display the text of button2
                isOn = false;

            }
        });

        //file = new File(getFilesDir(), FILENAME);

     /*   try {
            if (file.createNewFile()) {
                Toast.makeText(getApplicationContext(), "File Created",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

    }
    public void onResume() {
        super.onResume();
        //sensorManager.registerListener(accListener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(accListener, sensorManager.getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accListener, sensorManager.getDefaultSensor(TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(accListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }




    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(accListener);

    }

    boolean isAccelData = false;
    boolean isGyroData = false;
    SensorEvent v1;
    SensorEvent v2;
    public SensorEventListener accListener = new SensorEventListener() {
        private long lastTimestamp;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isOn) {

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    isAccelData = true;
                    v1= event;
                }
                if (event.sensor.getType() == TYPE_GYROSCOPE) {
                    isGyroData = true;
                    v2 = event;
                }
                if (isAccelData & isGyroData) {
                    float x = v1.values[0];
                    float y = v1.values[1];
                    float z = v1.values[2];
                    float x2 = v2.values[0];
                    float y2 = v2.values[1];
                    float z2 = v2.values[2];

                    //System.out.println("Comparaison = "+v1.values[0]+" : "+ v2.values[0]);
                   // textX.setText("" +(int) x);
                   // textY.setText("" +(int) y);
                   // textZ.setText("" +(int) z);

                    String value = ""+x + ";" + y + ";" + z + ";" + x2 + ";" + y2 + ";" + z2 + "\n";
                    textX.setText("aX : " + (int) x + " gX :"+(int) x2);
                    textY.setText("aY : " + (int) y + " gY :"+(int) y2);
                    textZ.setText("aZ : " + (int) z + " gZ :"+(int) z2);

                    //StepCounter
                    double Magnitude = Math.sqrt(x*x + y*y + z*z);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 6){
                        cpt++;
                       // stepC.setText(String.valueOf(cpt));
                    }


                    long currentTimestamp = event.timestamp;
                    if (currentTimestamp - lastTimestamp >= TimeUnit.MILLISECONDS.toNanos(5000)) {
                        lastTimestamp = currentTimestamp;
                        System.out.println(value);
                        System.out.println("Step = " + cpt);
                        writeFile(value);
                    }
                    // DO something here
                    isAccelData = false;
                    isGyroData = false;
                }
                // readFromFile rf = new readFromFile(file);
                // rf.execute(value);
                //content.setText(value);
                //runthread();
                //writeFile(value);
                //readFile();
                //System.out.println(" chemin =  " + MainActivity.this.getFilesDir().getAbsolutePath());
            }

        }

        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

    };



    public void writeFile(String data) {
        String textToSave = data;

        try {
            FileOutputStream fileOutputStream = openFileOutput("mytextfile.txt", MODE_APPEND);
            fileOutputStream.write(textToSave.getBytes());
            fileOutputStream.close();


            //Toast.makeText(getApplicationContext(), "Text Saved", Toast.LENGTH_SHORT).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile() {
        try {
            FileInputStream fileInputStream = openFileInput("mytextfile2.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }
            String ind = stringBuffer.toString();
            content.setText(stringBuffer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
