/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.example.reminder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

/**
 * When scheduled arriving time is approaching, initiate an instance of LocationHandler to pull location data and notify user.
 */
public class ReceiveArrivalAlarm extends BroadcastReceiver
{
	LocationHandler gps;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // create class object
        gps = new LocationHandler(context, intent);
      /* 
       * --CODE BELOW NOT WORKING--
       *  // check if GPS enabled
        if(!gps.canGetLocation()){
            gps.showSettingsAlert("GPS settings");
        }*/

    }
}

