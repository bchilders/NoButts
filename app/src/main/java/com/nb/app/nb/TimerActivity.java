package com.nb.app.nb;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;


public class TimerActivity extends Activity {

    //vars from intent
    long remainingTime, countdownTime;
    int voteCount;
    int serviceState;
    int consecutor;
    int multiplier;
    int score;

    //local objects
    Ringtone ring;
    Vibrator vib;
    Button startButton;
    ImageButton checkButton, crossButton;
    LinearLayout linLay;
    Intent serviceIntent, endIntent;
    TextView clockView, testView;
    long[] vibtime ={1,1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //instantiate local objects
        clockView = (TextView) findViewById(R.id.clockView);
        startButton = (Button)findViewById(R.id.startButton);
        checkButton = (ImageButton)findViewById(R.id.chk);
        crossButton = (ImageButton)findViewById(R.id.x);
        linLay = (LinearLayout)findViewById(R.id.buttons);
        ring = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        serviceIntent = new Intent(getBaseContext(), TimerIntentService.class);
        endIntent = new Intent(getBaseContext(), EndActivity.class);

        //get vars from intent
        countdownTime = getIntent().getLongExtra(TimerIntentService.COUNTDOWN_TIME, -2);
        remainingTime = getIntent().getLongExtra(TimerIntentService.REMAINING_TIME, -2);
        voteCount = getIntent().getIntExtra(TimerIntentService.VOTE_COUNT, -2);
        serviceState = getIntent().getIntExtra(TimerIntentService.SERVICE_STATE,-2);
        consecutor = getIntent().getIntExtra(TimerIntentService.CONSECUTOR, -2);
        multiplier = getIntent().getIntExtra(TimerIntentService.MULTIPLIER, -2);
        score=getIntent().getIntExtra(TimerIntentService.SCORE, 0);

        if(serviceState == TimerIntentService.STATE_NOT_STARTED) {//call came from levels activity
            clockView.setText(formatForClockView(countdownTime));

            serviceIntent.setAction(TimerIntentService.START_SERVICE);
            serviceIntent.putExtra(TimerIntentService.COUNTDOWN_TIME, countdownTime);

            startButton.setVisibility(View.VISIBLE);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runCountdownTimer(countdownTime);
                    startService(serviceIntent);
                    startButton.setVisibility(View.INVISIBLE);
                }
            });
        } else if(serviceState == TimerIntentService.STATE_TIMING){
            clockView.setText(formatForClockView(remainingTime));
            runCountdownTimer(remainingTime);
            serviceIntent.setAction(TimerIntentService.RECEIVE_VOTE);
            serviceIntent.putExtra(TimerIntentService.COUNTDOWN_TIME, countdownTime);
            serviceIntent.putExtra(TimerIntentService.CONSECUTOR, consecutor);
            ring.play();
            vib.vibrate(5000);

            linLay.setVisibility(View.VISIBLE);
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startService(serviceIntent);
                    linLay.setVisibility(View.INVISIBLE);
                    ring.stop();
                    vib.cancel();
                }
            });

            crossButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linLay.setVisibility(View.INVISIBLE);
                    ring.stop();
                    vib.cancel();
                }
            });

        } else if(serviceState == TimerIntentService.LAST_RUN){
            clockView.setText(formatForClockView(remainingTime));
            linLay.setVisibility(View.VISIBLE);
        }else if(serviceState == TimerIntentService.STATE_DONE){
            clockView.setText(formatForClockView(0));
            ring.play();
            vib.vibrate(5000);

            linLay.setVisibility(View.VISIBLE);
            checkButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    voteCount ++;
                    score+=countdownTime/TimerIntentService.minute*multiplier;
                    linLay.setVisibility(View.INVISIBLE);
                    ring.stop();
                    vib.cancel();
                    endIntent.putExtra(TimerIntentService.VOTE_COUNT, voteCount);
                    endIntent.putExtra(TimerIntentService.COUNTDOWN_TIME, countdownTime);
                    endIntent.putExtra(TimerIntentService.SCORE,  score);
                    startActivity(endIntent);
                }
            });

            crossButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linLay.setVisibility(View.INVISIBLE);
                    ring.stop();
                    vib.cancel();
                    endIntent.putExtra(TimerIntentService.COUNTDOWN_TIME, countdownTime);
                    endIntent.putExtra(TimerIntentService.SCORE,  score);
                    endIntent.putExtra(TimerIntentService.VOTE_COUNT, voteCount);
                    startActivity(endIntent);

                }
            });


        } else{
            clockView.setText("Oh. Well, fuck.");
        }

//        Toast.makeText(TimerActivity.this, Integer.toString(intent.getIntExtra("the_level", 0)), Toast.LENGTH_SHORT).show();
        //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Ringtone ring = RingtoneManager.getRingtone(getApplicationContext(), notification);
    }



    public void goToEnd(View view){
        ring.stop();
        vib.cancel();
        vib.vibrate(5000);
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }

    private String formatForClockView(long timeInMillis){
        int hours,mins,secs;
        String h,m,s;

        secs = (int) timeInMillis/1000;
        mins = (int)(secs/60)%60;
        hours = (int)secs/3600;
        secs = secs%60;

        if(secs<10){s = "0"+secs;}else{s=""+secs;}
        if(mins<10){m = "0"+mins;}else{m=""+mins;}
        if(hours<10){h = "0"+hours;}else{h=""+hours;}

        return (h + ":" + m + ":" + s);
    }

    private void runCountdownTimer(long timeInMillis){
        CountDownTimer mainTimer = new CountDownTimer(timeInMillis, 1000) {


            public void onTick(long millisUntilFinished) {

                clockView.setText(formatForClockView(millisUntilFinished));
            }

            public void onFinish() {
                clockView.setText(formatForClockView(0));
            }
        }.start();
    }
}
