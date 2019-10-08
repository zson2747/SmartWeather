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

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * This class is the Data Access Object (DAO) which contain methods for accessing the database.
 */
@Dao
public interface LocationDao {
    @Query("SELECT * FROM location")
    List<Location> listAll();

    @Insert
    void insert(Location location);

    @Insert
    void insertAll(Location... locations);

    @Query("DELETE FROM location")
    void deleteAll();
}