package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Podcast.class}, version = 1)
public abstract class PodcastDatabase extends RoomDatabase {
    //Retorna o DAO
    public abstract PodcastDao podcastDao();
    //A instancia
    public static PodcastDatabase sInstance;

    //Retorna a instancia singleton do database
    public static synchronized PodcastDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), PodcastDatabase.class, "podcasts")
                    .build();
        }
        return sInstance;
    }
}
