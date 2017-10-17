package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class PodcastProvider extends ContentProvider {
    private PodcastDBHelper dbHelper;

    public PodcastProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (isEpisodiosUri(uri)) {
            long id = dbHelper.getWritableDatabase().insert(PodcastDBHelper.DATABASE_TABLE,null,values);
            return Uri.withAppendedPath(PodcastProviderContract.EPISODE_LIST_URI,Long.toString(id));
        } else {
            return null;
        }

    }

    @Override
    public boolean onCreate() {
        dbHelper = PodcastDBHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        if (isEpisodiosUri(uri)) {
            cursor = dbHelper.getReadableDatabase().query(PodcastProviderContract.EPISODE_TABLE,projection,selection,selectionArgs,null,null,sortOrder);
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (isEpisodiosUri(uri)) {
            return dbHelper.getWritableDatabase().update(PodcastDBHelper.DATABASE_TABLE,values,selection,selectionArgs);
        } else {
            return 0;
        }
    }

    private boolean isEpisodiosUri (Uri uri) {
        return uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE);
    }
}
