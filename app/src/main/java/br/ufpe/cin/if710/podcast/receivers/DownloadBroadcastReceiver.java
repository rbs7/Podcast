package br.ufpe.cin.if710.podcast.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Rogerio Santos on 16/10/2017.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Receiver", "Broadcast recebido!!!!!!!!");
    }
}
