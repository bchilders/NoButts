package com.nb.app.nb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HighScoreActivity extends Activity {
    ArrayList<ScoreCard> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        //initList();
        JSONSerializer serializer= new JSONSerializer(getBaseContext());

        try{
            scores = serializer.loadScores();
        } catch (Exception e){
            scores = new ArrayList<ScoreCard>();
        }

        sortScores(scores);

        ListView lv = (ListView) findViewById(R.id.listView);

        //SimpleAdapter simpleAdpt = new SimpleAdapter(this, levelsList, android.R.layout.simple_list_item_1, new String[] {"level"}, new int[] {android.R.id.text1});



        ScoreAdapter adapter = new ScoreAdapter(scores);

        lv.setAdapter(adapter);

    }

    private void sortScores(ArrayList<ScoreCard> scores){
        boolean isDone=false;
        while(!isDone){
            isDone=true;
            for(int i = 1; i<scores.size();i++){
                if(scores.get(i).score>scores.get(i-1).score){
                    scores.add(i-1,scores.get(i));
                    scores.remove(i+1);
                    isDone=false;
                }
            }
        }
    }

    private class ScoreAdapter extends ArrayAdapter<ScoreCard>{
        public ScoreAdapter(ArrayList<ScoreCard> scores){
            super(getBaseContext(), 0, scores);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = HighScoreActivity.this.getLayoutInflater()
                        .inflate(R.layout.list_item_score, null);
            }

            ScoreCard s = getItem(position);

            TextView timeTextView =
                    (TextView)convertView.findViewById(R.id.timehs);
            timeTextView.setText(s.time);
            TextView dateTextView =
                    (TextView)convertView.findViewById(R.id.datehs);
            dateTextView.setText(s.date);
            TextView scoreTextView =
                    (TextView)convertView.findViewById(R.id.scorehs);
            scoreTextView.setText(Integer.toString(s.score));

            return convertView;
        }
    }

}
