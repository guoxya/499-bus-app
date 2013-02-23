package com.example.reminder;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Check which alarm (arrival/departure/both) user has selected, and set alarms accordingly)
 */
public class ReminderSettingsView extends Activity {
    Toast mToast;    
    NumberPicker minToAlarm;
    NumberPicker metersToAlarm;
    String destination;
    boolean backToMainView = true;
    private Random random = new Random();
    private Context mContext = ReminderSettingsView.this;
    private CheckBox checkBoxDeparture;
	private CheckBox checkBoxArrive;
	private EditText edtxtDestination;
	public static PendingIntent departureAlarmSender;
	private static PendingIntent arrivalAlarmSender;
    public static SharedPreferences prefs;
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_settings_view);
        prefs = this.getSharedPreferences(
        	      "com.example.reminder", Context.MODE_PRIVATE);
        int prefsVal;
        /*Set up NumberPickers--------------------------------------*/
        minToAlarm = (NumberPicker) findViewById(R.id.numberPicker1);
        minToAlarm.setMaxValue(30);//30min away
        minToAlarm.setMinValue(1);//1min away
    	prefsVal = prefs.getInt("min_to_alarm", 5);//get latest user config
        minToAlarm.setValue(prefsVal);//display latest user config; default = 5  
       
        metersToAlarm = (NumberPicker) findViewById(R.id.NumberPicker01);
        metersToAlarm.setMaxValue(2000);//2000m away
        metersToAlarm.setMinValue(200);//200m away
    	prefsVal = prefs.getInt("meters_to_alarm", 200);//get latest user config; default = 200
        metersToAlarm.setValue(prefsVal);//display latest user config
        /*Finish setting up NumberPickers----------------------------*/

        /*Set up CheckBoxes------------------------------------------*/
        boolean isDepartureAlarmChecked = prefs.getBoolean("is_departure_alarm_checked", false);
    	boolean isArrivalAlarmChecked = prefs.getBoolean("is_arrival_alarm_checked", false);
        checkBoxDeparture = (CheckBox)findViewById(R.id.checkBoxDepartAlarm);
    	checkBoxArrive = (CheckBox)findViewById(R.id.checkBoxArriveAlarm);
    	
    	if(isDepartureAlarmChecked){
    		checkBoxDeparture.setChecked(true);
    	}else{
    		checkBoxDeparture.setChecked(false);
    	}

    	if(isArrivalAlarmChecked){
    		checkBoxArrive.setChecked(true);
    	}else{
    		checkBoxArrive.setChecked(false);
    	}
        /*Finish setting up CheckBoxes----------------------------*/
    
    	
    	/*Set up EditText-----------------------------------------*/
    	edtxtDestination = (EditText)findViewById(R.id.editTextDestination); 
        //get latest user config; default = "University of Victoria"
        destination = prefs.getString("destination", "University of Victoria");
        edtxtDestination.setText(destination);//display latest user config      
        /*Finish setting up EditText------------------------------*/
 	
    	/*Set up button click listeners*/
        Button buttonAlarmOK = (Button)findViewById(R.id.buttonAlarmOK);
        buttonAlarmOK.setOnClickListener(mAlarmListener);

    }

    /**
     * Function invoked when clicking "OK" in the "reminder settings view".
     */
    private OnClickListener mAlarmListener = new OnClickListener() {
        public void onClick(View v) {       	
        	int min_to_alarm = minToAlarm.getValue();
        	int meters_to_alarm = metersToAlarm.getValue();
        	destination = edtxtDestination.getText().toString();

        	//store user input value to SharedPreferences
            prefs.edit().putInt("min_to_alarm", min_to_alarm).commit();
            prefs.edit().putInt("meters_to_alarm", meters_to_alarm).commit();
            prefs.edit().putBoolean("is_departure_alarm_checked", checkBoxDeparture.isChecked()).commit();
            prefs.edit().putBoolean("is_arrival_alarm_checked", checkBoxArrive.isChecked()).commit();
            prefs.edit().putString("destination", destination).commit();

            if(checkBoxDeparture.isChecked()){ //If user selects 'departure alarm'...
        		/*Alarm goes off at a specific time. e.g. 2 min before the scheduled bus coming time*/
        				
                Intent intent = new Intent(ReminderSettingsView.this, ReceiveDepartureAlarm.class);
                intent.putExtra("min_to_alarm", min_to_alarm);
                departureAlarmSender = PendingIntent.getBroadcast(ReminderSettingsView.this,
                        random.nextInt(), intent, 0);
                Calendar calendar = Calendar.getInstance();
                
                
                /*
                 * --Code below will set an alarm at a specific time.--
                 * 
                 * calendar.set(Calendar.HOUR_OF_DAY, 15);//PASS PARAMETER: hour
                calendar.set(Calendar.MINUTE, 14 - min_to_alarm);//PASS PARAMETER: min
                calendar.set(Calendar.SECOND,0);//PASS PARAMETER: sec
*/              
              /* --Code below is for Testing Purpose--
               * We want the alarm to go off 5 seconds from now.
               */
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, min_to_alarm);                
      
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);//get departure alarm manager
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), departureAlarmSender);
                //startActivity(new Intent(ReminderSettingsView.this, MainView.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); 
        	 }else{//Cancel alarm if user uncheck alarm
        	     AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);//get departure alarm manager
        		 am.cancel(departureAlarmSender);
        		 mToast = Toast.makeText(ReminderSettingsView.this, "Alarm is canceled.",
	                       Toast.LENGTH_SHORT);
	             mToast.show(); 
        	 }
        		
        	 if(checkBoxArrive.isChecked()){//If user selects 'arrival alarm'...
        		 /* Alarm goes off if the user is near his destination. e.g. 400 meters from destination
        		  * To save battery (minimize pulling location data), the location handler wont be invoked
        		  * until some time (e.g. 5 min) before the scheduled bus arrival time. This is accomplished 
        		  * by setting an alarm some time (e.g. 5 min) before scheduled arriving time.
        		  * When the alarm goes off, the location handler will be triggered. See LocationHandler.java.
        		  * */
	        		
        		 	Intent intent = new Intent(ReminderSettingsView.this, ReceiveArrivalAlarm.class);
                    intent.putExtra("meters_to_alarm", meters_to_alarm);
                    intent.putExtra("destination_str", destination);
        		 	arrivalAlarmSender = PendingIntent.getBroadcast(ReminderSettingsView.this,
	                        random.nextInt(), intent, 0);
	                Calendar calendar = Calendar.getInstance();
	                
	                /*calendar.set(Calendar.HOUR_OF_DAY, 15);//PASS PARAMETER: arrive hour
	                calendar.set(Calendar.MINUTE, 4);//PASS PARAMETER: arrive min
	                calendar.set(Calendar.SECOND,0);//PASS PARAMETER: arrive sec
*/	            
	              /* --Code below is for Testing Purpose--
	               * We want the alarm to go off 5 seconds from now.
	               */
	                calendar.setTimeInMillis(System.currentTimeMillis());
	                calendar.add(Calendar.SECOND, 5);               
	      
	                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), arrivalAlarmSender);                 

                    LocationManager locationManager = (LocationManager) ReminderSettingsView.this
                            .getSystemService(LOCATION_SERVICE);
         
                    /*Check GPS and Network status: 
                     * if neither is enabled, prompt user to enable.
                     * */ 
                    // getting GPS status
                    boolean isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);
                    // getting network status
                    boolean isNetworkEnabled = locationManager
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if (!isGPSEnabled || !isNetworkEnabled) {
	                    backToMainView = false; //Don't go back to main view immediately.
	                    showSettingsAlert("GPS Settings");
                    }     
	       	 }else{//Cancel alarm if user uncheck alarm
	       		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);//get departure alarm manager
	       		am.cancel(arrivalAlarmSender);
	       		mToast = Toast.makeText(ReminderSettingsView.this, "Alarm is canceled.",
		                       Toast.LENGTH_SHORT);
	            mToast.show(); 
	       	 }
        	 
     		/*
     		 * Tell the user that the alarm is scheduled
     		 */
        	 if(checkBoxArrive.isChecked()||checkBoxDeparture.isChecked()){
	               if (mToast != null) {
	                   mToast.cancel();
	               }
	               mToast = Toast.makeText(ReminderSettingsView.this, R.string.alarmScheduled,
	                       Toast.LENGTH_SHORT);
	               mToast.show();  		
        	 }
        	 
        	 /* 
        	  * Go back to main view after clicking 'ok' button
        	  */
        	 if(backToMainView){
            	 startActivity(new Intent(ReminderSettingsView.this, MainView.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  
        	 }
        }
   
    };


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(String title){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
}


    