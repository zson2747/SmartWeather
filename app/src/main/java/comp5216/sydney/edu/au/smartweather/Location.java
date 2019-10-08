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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * This class is the model (entity) for a location which represents a table
 * named location within the database.
 */
@Entity(tableName = "location")
public class Location {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "locationID")
    private int locationID;

    @ColumnInfo(name = "locationInfo")
    private LocationsWeather locationInfo;

    /**
     * Constructs a LocationsWeather object
     *
     * @param locationInfo LocationsWeather: a LocationsWeather object
     */
    public Location(LocationsWeather locationInfo) {
        this.locationInfo = locationInfo;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public LocationsWeather getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationsWeather locationInfo) {
        this.locationInfo = locationInfo;
    }
}
