package io.github.gatimus.spellingbee;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.wordnik.client.api.WordsApi;
import com.wordnik.client.common.ApiException;
import com.wordnik.client.model.WordObject;

import java.util.concurrent.ExecutionException;

public class WordService extends Service {

    private final static String TAG = "WordService";
    private final static String KEY = "03f2cf8c42306121b91591ef5730891f698fbc554e66ee9af";

    private final IBinder wordServiceBinder = new WordServiceLocalBinder();
    private RandomWord randomWord;
    private WordsApi wordsApi;
    private SharedPreferences sharedPreferences;

    public String word;

    public WordService() {
        Log.v(TAG, "construct");
        randomWord = new RandomWord();
        wordsApi = new WordsApi();
        wordsApi.addHeader("api_key", KEY);
        word = "";

    } //constructor

    @Override
    public IBinder onBind(Intent arg0) {
        Log.v(TAG, "onBind");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return wordServiceBinder;
    } //onBind

    public class WordServiceLocalBinder extends Binder {
        private final static String TAG = "WordServiceLocalBinder";
        WordService getService() {
            Log.v(TAG, "getService");
            return WordService.this;
        } //getService
    } //class MyLocalBinder

    public String getRandomWord() {
        Log.v(TAG, "getRandomWord");
        randomWord = new RandomWord();
        randomWord.execute(sharedPreferences.getString("pref_difficulty", "Medium"));
        try {
            word = randomWord.get().getWord();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        } catch (ExecutionException e) {
            Log.e(TAG, e.toString());
        }
        Log.v(TAG, word);
        return word;
    } //getRandomWord

    public boolean cancelGetRandomWord(){
        Log.v(TAG, "cancelGetRandomWord");
        randomWord.cancel(true);
        return randomWord.isCancelled();
    } //cancelGetRandomWord

    public class RandomWord extends AsyncTask<String, String, WordObject> {

        private final static String TAG = "RandomWord";

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "PreExecute");
            word = "";
            super.onPreExecute();
        } //onPreExecute

        @Override
        protected WordObject doInBackground(String... prams) {
            Log.v(TAG, "doInBackground");
            publishProgress("start");
            WordObject wordObject = new WordObject();
            try {
                switch (prams[0]){
                    case "Easy":
                        wordObject = wordsApi.getRandomWord(null, null, "true", 1_000_000, -1, 1, -1, 1, 4);
                        break;
                    case "Medium":
                        wordObject = wordsApi.getRandomWord(null, null, "true", 100_000, -1, 1, -1, 2, 6);
                        break;
                    case "Hard":
                        wordObject = wordsApi.getRandomWord(null, null, "true", 10_000, -1, 1, -1, 4, 10);
                        break;
                    default:
                        wordObject = wordsApi.getRandomWord(null, null, "true", 1_000_000, -1, 1, -1, 2, 6);
                        break;
                }
            } catch (ApiException e) {
                Log.e(TAG, e.toString());
                publishProgress(e.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                publishProgress(e.toString());
            }
            publishProgress("finish");
            return wordObject;
        } //doInBackground

        @Override
        protected void onProgressUpdate(String... values) {
            Log.i(TAG, "ProgressUpdate " + values[0]);
        } //onProgressUpdate

        @Override
        protected void onCancelled (WordObject result){
            Log.v(TAG, "Cancelled");
            try {
                word = result.getWord();
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
            }
        } //onCancelled

        @Override
        protected void onPostExecute(WordObject result) {
            Log.v(TAG, "PostExecute");
            word = result.getWord();
            super.onPostExecute(result);
        } //onPostExecute

    } //class RandomWord

} //class
