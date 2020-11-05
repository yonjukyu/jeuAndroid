package com.example.jeu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Level1 extends AppCompatActivity implements SensorListener {

    public ImageView dauphin;
    public ImageView water;
    public long lastUpdate;
    public float x, y, z, last_x, last_y, last_z;
    public SensorManager sensorMgr;
    public int waterFallingSpeed = 100,cerceau_speed = 20;
    public boolean stoptojump= true, cerceauTest = false;
    public int gameNb = 1 , i =1;
    public int posY = 0, posX = 0, score = 0, bestScore;
    public ConstraintLayout layout;
    public TextView text_jump , text_score, bestScore_text;
    public ProgressBar progressBar;
    public ImageView cerceau;
    public int progress = 0 ;
    public Button button;
    public boolean rotate = false;
    public int compteurRotate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bestScore = preferences.getInt("Score",0);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
        progressBar = findViewById(R.id.progressBar);
        text_jump = findViewById(R.id.textJump);
        text_score = findViewById(R.id.score);
        bestScore_text = findViewById(R.id.bestScore_text);
        layout = findViewById(R.id.layout);
        dauphin = findViewById(R.id.dauphin);
        water = findViewById(R.id.water);
        water.setRotation(180);
    }


    public void mooveWater(float shakeSpeed){
        water.setY(water.getY()+shakeSpeed/waterFallingSpeed);
        progressBar.setProgress((int) (water.getY()/dauphin.getY() * 100 ));
    }

    public void clearWater(){
        water.setY(layout.getHeight());
    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 10) {

                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float shakeSpeed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                last_x = x;
                last_y = y;
                last_z = z;

                if (gameNb == 1){
                    if (water.getY() >= dauphin.getY()) {

                      if (stoptojump) {
                          water.setY(dauphin.getY());
                          jump(shakeSpeed);

                      }
                    } else mooveWater(shakeSpeed);
                }

                if(gameNb == 2) {
                    progressBar.setVisibility(View.INVISIBLE);
                    button = new Button(this);
                    button.setText("Flip");
                    layout.addView(button);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rotate = true;
                        }
                    });

                    if (rotate == true) {
                        Log.i("ca tourne nrmlmt", "");
                        compteurRotate += 15;
                        dauphin.setRotationX(compteurRotate);
                        dauphin.setRotationY(compteurRotate);
                        if (compteurRotate >= 360) {
                            rotate = false;
                            compteurRotate = 0;
                            dauphin.setRotationX(0);
                        }
                        score += 1000;
                    }


                    int dauphinRotate = 0;
                    if (dauphinRotate < 360) {
                        dauphin.setRotation(dauphinRotate++);
                    }


                    float newX = dauphin.getX() + 4 * values[SensorManager.DATA_X];
                    dauphin.setX(newX);

                    if (dauphin.getX() + dauphin.getWidth() > layout.getWidth())
                        dauphin.setX(layout.getWidth() - dauphin.getWidth());
                    if (dauphin.getX() < 0) dauphin.setX(0);


                    if (cerceau.getX() < dauphin.getX() + dauphin.getWidth()
                            && cerceau.getX() + cerceau.getWidth() > dauphin.getX()
                            && cerceau.getY() + cerceau.getHeight() > dauphin.getY()
                            && cerceau.getY() < dauphin.getY()) {

                        score += 100;

                        int i = new Random().nextInt(3) + 1;
                        if (i == 1) posX = 0;
                        else if (i == 2) posX = (int) (layout.getWidth() - cerceau.getWidth());
                        else posX = (layout.getWidth() - cerceau.getWidth()) / 2;

                        posY = 0;

                        if (score > 5000) {
                            endGame();
                        }

                    }
                }
                
                     if(cerceau.getY() > layout.getHeight()) {
                         posY = 0;
                         cerceau.setY(posY);
                         int i = new Random().nextInt(3) + 1;
                         if(i == 1) posX =0;
                         else if(i ==2 ) posX =  (int) (layout.getWidth() - cerceau.getWidth());
                         else posX = (layout.getWidth() - cerceau.getWidth())/2;

                     }

                    text_score.setText("Score :" + score);
                    bestScore_text.setText("Best Score : " + bestScore);
                    posY += cerceau_speed;
                    cerceau.setY(posY);
                    cerceau.setX(posX);

            }
        }
    }


    private void spawnCerceau() {
        cerceauTest = true;
        int posX;
        cerceau = new ImageView(this);
        cerceau.setImageResource(R.drawable.cerceau_rouge);

        int width = dauphin.getWidth()/2;
        int height = dauphin.getWidth()/2;

        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
        cerceau.setLayoutParams(parms);

        cerceau.setX(0);
        cerceau.setY(posY);
        layout.addView(cerceau);
    }



    public void jump(float shakeSpeed){

        if(shakeSpeed > 20000){
            Log.e("il a sauté le bougre","");
            clearWater();
            stoptojump = false;
            gameNb = 2;
            spawnCerceau();
        }
    }

    public void endGame(){
        Intent intent = new Intent(this, Win.class);
        startActivity(intent);
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        if(score > bestScore) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putInt("Score", score);
            editor.apply();
        }
        super.onStop();
    }

}