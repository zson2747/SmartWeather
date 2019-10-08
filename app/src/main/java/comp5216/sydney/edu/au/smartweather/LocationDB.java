/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package comp5216.sydney.edu.au.smartweather;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * This class contains the database holder and serves as the main access point for the underlying
 * connection to the app’s persisted, relational data.
 */
@Database(entities = {Location.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class LocationDB extends RoomDatabase {
    private static final String DATABASE_NAME = "location_db";
    private static LocationDB DBINSTANCE;

    public abstract LocationDao locationDao();

    public static LocationDB getDatabase(Context context) {
        if (DBINSTANCE == null) {
            synchronized (LocationDB.class) {
                DBINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        LocationDB.class, DATABASE_NAME).build();
            }
        }
        return DBINSTANCE;
    }

    public static void destroyInstance() {
        DBINSTANCE = null;
    }
}

