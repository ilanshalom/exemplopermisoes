package com.example.exemplo6_permissoes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    //classe para implementar o "receptor de transmissão"
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.intel); //tocará o som em intel.mp3
        mp.start();
        Toast.makeText(context, "Alarme tocando, acordaaaa...", Toast.LENGTH_LONG).show();
    }
}


