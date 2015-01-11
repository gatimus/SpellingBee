package io.github.gatimus.spellingbee;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class WordService extends Service {

    final static String TAG = "WordService:";
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
        randomWord.execute("http://randomword.setgetgo.com/get.php");
        try {
            word = randomWord.get();
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

    public class RandomWord extends AsyncTask<String, Integer, String> {

        public String word;

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "PreExecute");
            word = "";
            super.onPreExecute();
        } //onPreExecute

        @Override
        protected String doInBackground(String... urls) {
            Log.v(TAG, "doInBackground");
            publishProgress(0);
            String result = "";
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.i(TAG, String.valueOf(statusCode));
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    Log.v(TAG, result);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            publishProgress(100);
            return result;
        } //doInBackground

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.v(TAG, "ProgressUpdate" + String.valueOf(progress[0]));
        } //onProgressUpdate

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "PostExecute");
            word = result;
            Log.v(TAG, word);
            super.onPostExecute(result);
        } //onPostExecute

    } //class RandomWord
}
