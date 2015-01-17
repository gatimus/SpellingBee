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

    private final static String TAG = "WordService";
    private final static String KEY = "03f2cf8c42306121b91591ef5730891f698fbc554e66ee9af";

    private final IBinder wordServiceBinder = new WordServiceLocalBinder();
    private RandomWord randomWord;
    private WordsApi wordsApi;

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
        randomWord.execute();
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

    public class RandomWord extends AsyncTask<Void, String, WordObject> {

        private final static String TAG = "RandomWord";

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "PreExecute");
            word = "";
            super.onPreExecute();
        } //onPreExecute

        @Override
        protected WordObject doInBackground(Void... prams) {
            Log.v(TAG, "doInBackground");
            publishProgress("start");
            WordObject wordObject = new WordObject();
            try {
                wordObject = wordsApi.getRandomWord(null, null, "true", 1_000_000, -1, 1, -1, 0, 5);
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
