package io.github.gatimus.spellingbee;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.wordnik.client.api.WordsApi;
import com.wordnik.client.common.ApiException;
import com.wordnik.client.model.WordObject;

import java.util.concurrent.ExecutionException;

public class WordService extends Service {

    final static String TAG = "WordService:";
    final static String KEY = ""; //TODO
    private final IBinder myBinder = new MyLocalBinder();
    private String myWords[] = new String[100];
    private int currInd=0,maxInd=3;

    public WordService() {
        myWords[0] = "House";
        myWords[1] = "Car";
        myWords[2] = "Door";
        myWords[3] = "Barn";
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    public String getWord() {
        if (currInd > 3)
            currInd = 0;
        return myWords[currInd++];
    }

    public String getRandomWord() {
        RandomWord randomWord = new RandomWord();
        String word = "";
        //randomWord.execute(KEY);
        try {
            word = randomWord.get().getWord();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        } catch (ExecutionException e) {
            Log.e(TAG, e.toString());
        }
        Log.v(TAG, word);
        return word;
    }

    public class MyLocalBinder extends Binder {
        WordService getService() {
            return WordService.this;
        }
    }

    public class RandomWord extends AsyncTask<Void, Integer, WordObject> {

        public String word;
        private WordsApi wordsApi;


        @Override
        protected void onPreExecute() {
            Log.v(TAG, "PreExecute");
            word = "";
            wordsApi = new WordsApi();
            super.onPreExecute();
        } //onPreExecute

        @Override
        protected WordObject doInBackground(Void... prams) {
            Log.v(TAG, "doInBackground");
            publishProgress(0);
            wordsApi.addHeader("api_key", KEY);
            WordObject wordObject = new WordObject();
            try {
                wordObject = wordsApi.getRandomWord(null, null, "false", 0, -1, 0, -1, 0 , -1);
            } catch (ApiException e) {
                Log.e(TAG, e.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            publishProgress(100);
            return wordObject;
        } //doInBackground

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.v(TAG, "ProgressUpdate" + String.valueOf(progress[0].intValue()));
        } //onProgressUpdate

        @Override
        protected void onPostExecute(WordObject result) {
            Log.v(TAG, "PostExecute");
            word = result.getWord();
            Log.v(TAG, word);
            super.onPostExecute(result);
        } //onPostExecute

    } //class RandomWord
}
