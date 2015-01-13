package io.github.gatimus.spellingbee;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class Main extends ActionBarActivity {

    private static final String TAG = "Main";
    private WordService wordService;
    private Resources res;
    private FragmentManager fragMan;
    private DialogFragment about;
    private DialogFragment help;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Create");
        res = getApplicationContext().getResources();
        fragMan = this.getFragmentManager();
        about = new About();
        help = new Help();
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });
        List<TextToSpeech.EngineInfo> engines = tts.getEngines();
        for (TextToSpeech.EngineInfo engine : engines){
            Log.v(TAG, engine.label);
        }
        setContentView(R.layout.main_layout);
        Intent intent = new Intent(this,WordService.class);
        bindService(intent, wordServiceConnection, Context.BIND_AUTO_CREATE);
    } //onCreate

    @Override
    public void onPause(){
        Log.v(TAG, "Pause");
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        wordService.cancelGetRandomWord();
        super.onPause();
    } //onPause

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "CreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } //onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, item.getTitle().toString() + " Selected");
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings :
                Intent intent = new Intent(Main.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.action_about :
                about.show(fragMan, res.getString(R.string.action_about));
                break;
            case R.id.action_help :
                help.show(fragMan, res.getString(R.string.action_help));
                break;
            case R.id.action_quit : System.exit(0);
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    } //onOptionsItemSelected

    public void showWord(View view) {
        Log.v(TAG, "showWord");
        if(!tts.isSpeaking()) {
            String wd = wordService.getRandomWord();
            TextView myTextView = (TextView) findViewById(R.id.myTextView);
            myTextView.setText(wd);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(wd, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                tts.speak(wd, TextToSpeech.QUEUE_FLUSH, null, "speak");
            } //if else
        } //if
    } //showWord

    private ServiceConnection wordServiceConnection = new ServiceConnection() {

        private static final String TAG = "wordServiceConnection";

        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v(TAG, "ServiceConnected");
            WordService.WordServiceLocalBinder binder = (WordService.WordServiceLocalBinder) service;
            wordService = binder.getService();
        } //onServiceConnected

        public void onServiceDisconnected(ComponentName arg0) {
            Log.v(TAG, "ServiceDisconnected");
        } //onServiceDisconnected

    };

} //class