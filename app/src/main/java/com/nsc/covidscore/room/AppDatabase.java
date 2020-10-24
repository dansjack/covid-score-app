package com.nsc.covidscore.room;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nsc.covidscore.Converters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Location.class, CovidSnapshot.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();
    public abstract CovidSnapshotDao covidSnapshotDao();

    private static volatile AppDatabase INSTANCE;
    private static Context ctx;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context) {
        ctx = context;
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "covid_snapshot_database")
                            .addCallback(appDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static AppDatabase.Callback appDatabaseCallback = new AppDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                LocationDao locationDao = INSTANCE.locationDao();
                CovidSnapshotDao covidSnapshotDao = INSTANCE.covidSnapshotDao();
                fillLocationTable(locationDao);

            });
        }
    };

    private static void fillLocationTable(LocationDao locationDao) {
        String jsonString;
        JSONArray jsonArray;
        AssetManager assetManager = ctx.getAssets();
        try {
            InputStream inputStream = assetManager.open("county_fips.json");
            byte[] buffer = new byte[inputStream.available()];
            int read = inputStream.read(buffer);
            if (read == -1) {
                inputStream.close();
            }
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(jsonString);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONArray currentArray = jsonArray.getJSONArray(i);
                // split county and state names
                String[] nameArray = currentArray.getString(0).split(",");
                String countyName = nameArray[0].trim();
                countyName = countyName.replaceAll("(\\sCounty|\\sParish|\\sBorough|\\sMunicipio|\\scity)", "");
                String stateName = nameArray[1].trim();
                String stateFips = currentArray.getString(1);
                String countyFips = currentArray.getString(2);
                Location location = new Location(countyName, stateName, countyFips, stateFips);
                locationDao.insert(location);
            }
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }
}
