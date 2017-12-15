package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.support.annotation.NonNull;

@Entity
public class Podcast {
    public String title;
    public String pubDate;
    @NonNull
    @PrimaryKey
    public String link;
    public String description;
    public String downloadLink;
    public String downloadUri;

    public static Podcast fromContentValues(ContentValues values) {
        final Podcast podcast = new Podcast();
        podcast.title = values.getAsString(PodcastProviderContract.TITLE);
        podcast.pubDate = values.getAsString(PodcastProviderContract.DATE);
        podcast.description = values.getAsString(PodcastProviderContract.DESCRIPTION);
        podcast.link = values.getAsString(PodcastProviderContract.EPISODE_LINK);
        podcast.downloadLink = values.getAsString(PodcastProviderContract.DOWNLOAD_LINK);
        podcast.downloadUri = values.getAsString(PodcastProviderContract.EPISODE_URI);
        return podcast;
    }
}
