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

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Notify user when the bus is about to come.
 */
public class ReceiveDepartureAlarm extends BroadcastReceiver
{
	
	Random random = new Random();
	private final String title = "My Bus Reminder";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
            int min_to_alarm = intent.getIntExtra("min_to_alarm", -1);
            String message = "Your bus is coming in " + min_to_alarm + " minutes";
        	 // Build notification
            // Actions are just fake
            Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(message).setSmallIcon(R.drawable.ic_launcher).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            // Hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            noti.ledARGB = 0xFFff0000; //red led
            noti.flags = Notification.FLAG_SHOW_LIGHTS;
            noti.ledOnMS = 500; 
            noti.ledOffMS = 500; 
            notificationManager.notify(random.nextInt(), noti);

        	//Play notification tone
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
            //Display departure alarm message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            
/*          -----------CODE BELOW is NOT WORKING--------
 * 		*/	/*AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    	    alertDialog.setTitle(title);
    	    alertDialog.setMessage(message);
    	    alertDialog.setPositiveButton("OK", null);
    	    alertDialog.show();	*/


        } catch (Exception e) {}
    }
    
}

