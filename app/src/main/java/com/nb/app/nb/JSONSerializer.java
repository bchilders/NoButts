package com.nb.app.nb;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class JSONSerializer {
    private Context context;
    private String fileName = "score.json";

    public JSONSerializer(Context c) {
        context = c;
    }

    public void saveScore( ArrayList<ScoreCard> scoreCards)
            throws JSONException, IOException{
        JSONArray array = new JSONArray();

        for (ScoreCard scoreCard : scoreCards)
            array.put(scoreCard.toJSON());

        Writer writer = null;
        try{
            OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }

    }

    public ArrayList<ScoreCard> loadScores() throws IOException, JSONException {
        ArrayList<ScoreCard> scores = new ArrayList<ScoreCard>();
        BufferedReader reader = null;
        try {
            InputStream in = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                jsonString.append(line);
            }
            JSONTokener jToke = new JSONTokener(jsonString.toString());
            JSONArray array;
            array = (JSONArray) jToke.nextValue();

            for (int i = 0; i<array.length();i++){
                scores.add(new ScoreCard(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e){

        } finally {
            if (reader != null)
                reader.close();
        }
        return scores;
    }
}
