package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

public class EpisodeDetailActivity extends Activity {
    TextView detailTitle;
    TextView detailDescription;
    TextView detailPubDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        //Inicializando itens do layout
        this.detailTitle = (TextView) findViewById(R.id.detail_title);
        this.detailDescription = (TextView) findViewById(R.id.detail_description);
        this.detailPubDate = (TextView) findViewById(R.id.detail_date);

        //Recebendo objeto passado no intent
        ItemFeed item = (ItemFeed) getIntent().getExtras().getSerializable("ItemFeed");

        //Configurando layout
        this.detailTitle.setText(item.getTitle());
        this.detailDescription.setText(item.getDescription());
        this.detailPubDate.setText(getString(R.string.published_in) + " " + this.convertPubDate(item.getPubDate()));
    }

    /* Método para converter a hora GMT para UTC-3 (Recife) */
    public String convertPubDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        try {
            TimeZone timeZone = TimeZone.getTimeZone("UTC-3");
            formatter.setTimeZone(timeZone);
            Date result = formatter.parse(date);
            return result.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return  "ERROR";
        }
    }
}
