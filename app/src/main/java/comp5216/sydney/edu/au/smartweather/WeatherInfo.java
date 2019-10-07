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

import java.util.Date;

public class WeatherInfo {
    private Date time;
    private String summary;
    private double temperature;

    public WeatherInfo(int timeStamp, String summary) {
        Date time = new Date((long) timeStamp * 1000);
        this.time = time;
        this.summary = summary;
    }

    public Date getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public double getTemp() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
