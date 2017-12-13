package br.ufpe.cin.if710.podcast.services;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Rogerio Santos on 15/10/2017.
 */

public class DownloadIntentService extends IntentService {
    private static final String TAG = DownloadIntentService.class.getSimpleName(); //Nome da classe
    public static final String DOWNLOAD_COMPLETE = "br.ufpe.cin.if710.podcast.intent.action.DOWNLOAD_COMPLETE";
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "podcast_notification_channel";

    public DownloadIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ItemFeed item = (ItemFeed) intent.getSerializableExtra("ItemFeed");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Iniciando download...");

            //Criando notificationManager
            final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //Criando o canal de notificacoes para Android Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Podcast Notifications", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setDescription("Podcast Notifications");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            //Criando notificationBuilder
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle("Baixando podcast")
                    .setContentText("Download em andamento")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOnlyAlertOnce(true);

            //Iniciando download
            try {
                File root = new File(Environment.getExternalStorageDirectory() + "/Podcast");
                root.mkdirs();
                File output = new File(root, Uri.parse(item.getDownloadLink()).getLastPathSegment());
                if (output.exists()) {
                    output.delete();
                }
                URL url = new URL(item.getDownloadLink());
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.connect();
                int fileLength = c.getContentLength();
                FileOutputStream fos = new FileOutputStream(output.getPath());
                BufferedOutputStream out = new BufferedOutputStream(fos);
                try {
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[8*1024];
                    int len = 0;
                    int incr = 0;
                    int perc = 0;
                    int percant = -1;
                    while ((len = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, len);
                        incr+=len;
                        perc = incr*100/fileLength;
                        //Atualiza andamento do download
                        if (perc >= percant+1) {
                            builder.setContentText("Download em andamento (" + perc + "%)");
                            builder.setProgress(100, perc, false);
                            // Mostra a progress bar
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            percant = perc;
                        }
                    }
                    out.flush();
                }
                finally {
                    fos.getFD().sync();
                    out.close();
                    c.disconnect();
                }

                Log.v(TAG, "Download concluído");

                //Gravar no banco da dados
                ContentValues contentValues = new ContentValues();
                contentValues.put(PodcastProviderContract.EPISODE_URI, output.getPath());
                getContentResolver().update(PodcastProviderContract.EPISODE_LIST_URI, contentValues, PodcastProviderContract.DOWNLOAD_LINK +"=?" , new String[]{item.getDownloadLink()});

                //Remove a progress bar
                builder.setContentText("Download concluído.").setProgress(0,0,false);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

                //Dar um broadcast
                Intent intent2 = new Intent(DOWNLOAD_COMPLETE);
                intent2.putExtra("ItemFeed", item);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);

            } catch (IOException e) {
                Log.e(getClass().getName(), "Exception durante download", e);
                //Remove a progress bar
                builder.setContentTitle("Falha no download do podcast").setContentText("Verifique sua conexão com a internet.").setProgress(0,0,false);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
            Log.v(TAG, "FIM!!!!!!!!");
        } else {
            Log.v(TAG, "Permissão negada");
        }
    }
}
