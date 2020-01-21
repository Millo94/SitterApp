package it.uniba.di.sms.sitterapp.principale;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

import androidx.annotation.Nullable;

class MySensorsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }
}
