package com.example.safetohome;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PowerButtonPressed extends BroadcastReceiver {

    int number;

    static int countPowerOff=0;
    private Activity activity=null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            countPowerOff++;
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            if (countPowerOff==3){
                String dial = "tel:" + number;
                activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }
}
