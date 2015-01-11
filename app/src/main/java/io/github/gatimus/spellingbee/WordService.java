package io.github.gatimus.spellingbee;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class WordService extends Service {

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

    public class MyLocalBinder extends Binder {
        WordService getService() {
            return WordService.this;
        }
    }
}
