package com.nb.app.nb;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ScoreCard{
    private static final String JSON_VOTES = "votes";
    private static final String JSON_SCORE = "score";
    private static final String JSON_TIME = "time";
    private static final String JSON_DATE = "date";

    public static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    public String date;
    public int votes, score;
    public String time;

    public ScoreCard(){
        Date today = Calendar.getInstance().getTime();
        date = df.format(today);
    }

    public ScoreCard(JSONObject json) throws JSONException {
        date = json.getString(JSON_DATE);
        time = json.getString(JSON_TIME);
        score = json.getInt(JSON_SCORE);
        votes = json.getInt(JSON_SCORE);
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_DATE, date);
        json.put(JSON_SCORE, score);
        json.put(JSON_TIME, time);
        json.put(JSON_VOTES, votes);
        return json;
    }
}