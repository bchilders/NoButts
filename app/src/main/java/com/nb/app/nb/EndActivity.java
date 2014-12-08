package com.nb.app.nb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class EndActivity extends Activity {

    TextView result;
    int totalVotes;
    int totalScore;
    long countdownTime;
    JSONSerializer s;
    ArrayList<ScoreCard> scoreCards;
    ScoreCard scoreCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        result = (TextView)findViewById(R.id.result);

        try {
            s = new JSONSerializer(this.getBaseContext());
            scoreCards = s.loadScores();
        } catch (Exception e){

        }


        totalVotes = getIntent().getIntExtra(TimerIntentService.VOTE_COUNT, -3);
        totalScore = getIntent().getIntExtra(TimerIntentService.SCORE, -3);
        countdownTime = getIntent().getLongExtra(TimerIntentService.COUNTDOWN_TIME, -3);


        int secs = (int) countdownTime/1000;
        int mins = (int)(secs/60)%60;
        int hours = (int)secs/3600;
        secs = secs%60;

        String resultString = "Congrats, you got " + totalScore + " points!\n"+totalVotes + " votes in ";

        String timeString = "";
        if(hours>0){
            timeString=timeString+hours +" hours ";
        }
        if(mins>0){
            timeString=timeString+mins+" minutes ";
        }
        if(secs>0){
            timeString=timeString+secs+" seconds";
        }

        resultString= resultString + timeString;


        String h,m,se;
        if(hours<10) h= "0"+hours; else h=""+hours;
        if(hours<10) m= "0"+mins; else m=""+mins;
        if(hours<10) se= "0"+secs; else se =""+secs;

        scoreCard = new ScoreCard();
        scoreCard.score=totalScore;
        scoreCard.time=h + ":" + m + ":"+se;
        scoreCard.votes=totalVotes;

        boolean highScore=false;
        int high=0;
        for(ScoreCard s : scoreCards){
            if(s.score>high){
                high=s.score;
            }
        }
        if(scoreCard.score>high) highScore = true;




        String highScoreString = "\n\nThat's a new high score!";

        if(highScore) resultString=resultString+highScoreString;

        result.setText(resultString);




        scoreCards.add(scoreCard);

        try{
            s.saveScore(scoreCards);
        }catch(Exception e){

        }
    }


    public void finish(View view){
        Intent intent = new Intent(this, LevelsActivity.class);
        startActivity(intent);
    }

    public void showHighScore(View view){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }


}
