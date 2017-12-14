package br.ufpe.cin.if710.podcast.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    //private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    private final String RSS_FEED = "http://feeds.feedburner.com/ProjetoXPodcasts?format=xml";
    //TODO teste com outros links de podcast

    public final Context self = this;

    private ListView items;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("Receiver", "Broadcast recebido!!!!!!!!");
            Toast.makeText(context, "Download concluído", Toast.LENGTH_SHORT).show();

            new GetDataFromDatabaseTask().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = (ListView) findViewById(R.id.items);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("br.ufpe.cin.if710.podcast.intent.action.DOWNLOAD_COMPLETE"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new DownloadXmlTask().execute(RSS_FEED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        if (adapter != null) {
            adapter.clear();
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Preparando feed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();

            try {
                if (isFeedModified(params[0])) {
                    try {
                        itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            if (!feed.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Gravando dados no banco de dados...", Toast.LENGTH_SHORT).show();
            }

            for (ItemFeed feedItem : feed) {
                Cursor cursor = getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI, null, PodcastProviderContract.EPISODE_LINK+"= ?", new String[]{feedItem.getLink()}, null);
                if (cursor.getCount() == 0) { //Verifica se ja existe no banco de dados
                    ContentValues cv = new ContentValues();
                    cv.put(PodcastProviderContract.TITLE, feedItem.getTitle());
                    cv.put(PodcastProviderContract.DATE, feedItem.getPubDate());
                    cv.put(PodcastProviderContract.DESCRIPTION, feedItem.getDescription());
                    cv.put(PodcastProviderContract.EPISODE_LINK, feedItem.getLink());
                    cv.put(PodcastProviderContract.DOWNLOAD_LINK, feedItem.getDownloadLink());
                    cv.put(PodcastProviderContract.EPISODE_URI, "");

                    getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, cv);
                }
                cursor.close();
            }

            new GetDataFromDatabaseTask().execute();
        }
    }

    private class GetDataFromDatabaseTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Iniciando leitura do banco de dados...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();

            Cursor cursor = getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI, null, null, null, null);

            while(cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.TITLE));
                String link = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_LINK));
                String pubDate = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DATE));
                String description = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DESCRIPTION));
                String downloadLink = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DOWNLOAD_LINK));
                String fileUri = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_URI));
                itemList.add(new ItemFeed(title, link, pubDate, description, downloadLink, fileUri));
            }

            cursor.close();
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //Adapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(self, R.layout.itemlista, feed);

            //atualizar o list view
            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);
            /*
            items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    XmlFeedAdapter adapter = (XmlFeedAdapter) parent.getAdapter();
                    ItemFeed item = adapter.getItem(position);
                    String msg = item.getTitle() + " " + item.getLink();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
            /**/
        }
    }


    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    private boolean isFeedModified(String feed) throws IOException {
        boolean ans;

        URL url = new URL(feed);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String lastMod = sharedPref.getString("lastMod", "");
        if (lastMod.isEmpty()) { //Primeira execucao
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lastMod", conn.getHeaderField("Last-Modified"));
            editor.apply();
            ans = true;
        } else {
            conn.setRequestProperty("If-Modified-Since", lastMod);
            if (conn.getResponseCode() == 304) {
                ans = false;
            } else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastMod", conn.getHeaderField("Last-Modified"));
                editor.apply();
                ans = true;
            }
        }

        return ans;
    }
}
