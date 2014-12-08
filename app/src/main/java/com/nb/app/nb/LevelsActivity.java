package com.nb.app.nb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LevelsActivity extends Activity {

    private static final long second = 1000;
    private static final long minute = second*60;
    private static final long hour = minute*60;
    private ArrayList<ScoreCard> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        JSONSerializer j = new JSONSerializer(getBaseContext());
        try{
            scores=j.loadScores();
        } catch(Exception e){
           scores=new ArrayList<ScoreCard>();
        }
//        Calendar c =Calendar.getInstance();
//        c.add(Calendar.DATE, -1);
        Date t = new Date(System.currentTimeMillis());
        Date y = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L);
        String today = ScoreCard.df.format(t);
        String yesterday = ScoreCard.df.format(y);

        int high=0;
        for(ScoreCard s : scores){
            if((s.date.equals(yesterday) || s.date.equals(today)) && s.score>high){
                high=s.score;
            }
        }

        initList(high);

        ListView lv = (ListView) findViewById(R.id.listView);

        SimpleAdapter simpleAdpt = new SimpleAdapter(this, levelsList, android.R.layout.simple_list_item_1, new String[] {"level"}, new int[] {android.R.id.text1});

        lv.setAdapter(simpleAdpt);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
                TextView clickedView = (TextView) view;

                Intent intent = new Intent(view.getContext(), TimerActivity.class);
                intent.putExtra(TimerIntentService.COUNTDOWN_TIME, timeMessage(clickedView.getText().toString()) );
                intent.putExtra(TimerIntentService.SERVICE_STATE, TimerIntentService.STATE_NOT_STARTED);
                startActivity(intent);
            }
        });
    }

    List<Map<String,String>> levelsList = new ArrayList<Map<String,String>>();

    private void initList(int high) {
      //  levelsList.add(createLevel("level", "Test"));
        levelsList.add(createLevel("level", "15 minutes"));
        if(high>1800) levelsList.add(createLevel("level", "30 minutes"));
        if(high>10800)levelsList.add(createLevel("level", "1 hour"));
        if(high>21600)levelsList.add(createLevel("level", "2 hours"));
        if(high>86400)levelsList.add(createLevel("level", "4 hours"));
        if(high>604800)levelsList.add(createLevel("level", "6 hours"));
        if(high>1008000)levelsList.add(createLevel("level", "8 hours"));
        if(high>1944000)levelsList.add(createLevel("level", "12 hours"));
        if(high>4492800)levelsList.add(createLevel("level", "16 hours"));
    }

    private HashMap<String, String> createLevel(String key, String name) {
        HashMap<String, String> level = new HashMap<String, String>();
        level.put(key,name);

        return level;
    }

    private long timeMessage(String name){
        if(name.equals("15 minutes")){return 15*minute;}
        else if(name.equals("30 minutes")){return 30*minute;}
        else if(name.equals("1 hour")){return hour;}
        else if(name.equals("2 hours")){return 2*hour;}
        else if(name.equals("4 hours")){return 4*hour;}
        else if(name.equals("6 hours")){return 6*hour;}
        else if(name.equals("8 hours")){return 8*hour;}
        else if(name.equals("12 hours")){return 12*hour;}
        else if(name.equals("16 hours")){return 16*hour;}
        else return 30*second;


    }

}
