package com.nb.app.nb;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


public class TimerIntentService extends IntentService {
    //Parameters
    public static final String REMAINING_TIME = "com.bignerdranch.android.IS.var.REMAINING_TIME";
    public static final String VOTE_COUNT = "com.bignerdranch.android.myintentservice.var.VOTE_COUNT";
    public static final String SERVICE_STATE = "com.bignerdranch.android.myintentservice.var.SERVICE_STATE";
    public static final String COUNTDOWN_TIME = "com.bignerdranch.android.myintentservice.var.COUNTDOWN_TIME";
    public static final String SCORE = "com.bignerdranch.android.myintentservice.var.SCORE";
    public static final String CONSECUTOR = "com.bignerdranch.android.myintentservice.var.CONSECUTOR";
    public static final String MULTIPLIER = "com.bignerdranch.android.myintentservice.var.MULTIPLIER";

    //Constants
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_TIMING = 1;
    public static final int STATE_DONE = 2;
    public static final int LAST_RUN = 3;
    public static final long second = 1000;
    public static final long minute = second*60;
    public static final long hour = minute*60;


    //actions
    public static final String START_SERVICE = "com.bignerdranch.android.IS.action.START_SERVICE";
    public static final String RECEIVE_VOTE = "com.bignerdranch.android.IS.action.RECEIVE_VOTE";

    //vars
    private int serviceState;
    private long remainingTime;
    private int voteCount;
    private int numAlarms = 3;
    private long alarmTime = 10000;
    private long[] alarmTimeArr;
    private long countdownTime;
    private int score=0;
    private int consecutor=1;
    private int multiplier=1;

    //objects
    PendingIntent pi;
    CountDownTimer isTimer;
    Intent mainIntent;
    AlarmManager alarmManager;

    public TimerIntentService(){
        super("IS");
        Log.d("IS", "Intent Service Created");
        remainingTime=-1;
        voteCount=0;
    }

    @Override
    public void onDestroy(){
        mainIntent = new Intent(getBaseContext(), TimerActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.putExtra(VOTE_COUNT, voteCount);
        mainIntent.putExtra(SCORE, score);
        if(consecutor==numAlarms+1){
            mainIntent.putExtra(MULTIPLIER,++multiplier);
        }else{
            mainIntent.putExtra(MULTIPLIER,1);
        }
        mainIntent.putExtra(COUNTDOWN_TIME, countdownTime);
        mainIntent.putExtra(REMAINING_TIME, remainingTime);
        mainIntent.putExtra(SERVICE_STATE, STATE_DONE);
        getBaseContext().startActivity(mainIntent);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.d("TimerIntentService","onHandleIntent called");
        if (intent != null){//intent is received

            //android.os.Debug.waitForDebugger();

            final String action = intent.getAction();
            if(START_SERVICE.equals(action)){//action is startservice
                setup(intent);
                for(int i=1;i<=numAlarms;i++){
                    consecutor++;
                    remainingTime -= alarmTimeArr[i-1];

                    buildMainIntent(i==numAlarms);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+alarmTimeArr[i-1], pi);
                    SystemClock.sleep(alarmTimeArr[i-1]);
                    Log.d("TimerIntentService","Sleep over");
                }
            }else if (RECEIVE_VOTE.equals(action)){
                voteCount++;
                if(intent.getIntExtra(CONSECUTOR, 0) == consecutor+1){
                    multiplier++;
                    score+=countdownTime/second*multiplier;
                }else{
                    multiplier=1;
                    score+=countdownTime/second;
                }
                consecutor = intent.getIntExtra(CONSECUTOR,0);

            }else{//intent has no/unidentified action
                Log.d("TimerIntentService","bad action for intent");
            }
        }else{//intent is not received
            Log.d("TimerIntentService","no intent received");
        }
    }

    private void setup(Intent intent){
        countdownTime = intent.getLongExtra(COUNTDOWN_TIME, 0);
        remainingTime = countdownTime;
        voteCount=1;//user pressed start, counts as one vote
        score = (int)(countdownTime/second);
        alarmManager = (AlarmManager)getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
        //change numAlarms and AlarmTime based on countDownTime
        if(countdownTime==15*minute){
            alarmTime = 5*minute;
            numAlarms = 3;
        }else if(countdownTime==30*minute){
            alarmTime = (long)7.5*minute;
            numAlarms = 4;
        }else if(countdownTime==1*hour){
            alarmTime = 15*minute;
            numAlarms = 4;
        }else if(countdownTime>=2*hour){
            alarmTime = 20*minute;
            numAlarms = (int)(countdownTime/hour*3);
        }

        //randomized times
        Random rand = new Random();
        alarmTimeArr = new long[numAlarms];
        alarmTimeArr[0]=alarmTime;
        for(int i = 1; i<numAlarms; i++){
            int posMin = 1;
            if(rand.nextBoolean()) posMin=-1;
            long timeVariance = (long) rand.nextInt((int)alarmTime/7*3)*posMin;

            alarmTimeArr[i-1]+=timeVariance;
            alarmTimeArr[i]= alarmTime-timeVariance;
        }

    }

    private void buildMainIntent(boolean isLastRun){
        mainIntent = new Intent(getBaseContext(), TimerActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.putExtra(CONSECUTOR, consecutor);
        mainIntent.putExtra(REMAINING_TIME, remainingTime);
        if(isLastRun){
            mainIntent.putExtra(SERVICE_STATE, LAST_RUN);
            consecutor=1;
        }else {
            mainIntent.putExtra(SERVICE_STATE, STATE_TIMING);
        }
        pi = PendingIntent.getActivity(getBaseContext(), 0 , mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
