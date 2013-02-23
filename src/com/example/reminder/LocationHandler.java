package com.example.reminder;
 
import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


/**
 * This class pulls location (GPS) data, and notifies user when he's near destination.
 */ 
public class LocationHandler extends Service implements LocationListener {
	private Random random = new Random();
 
	private final Context mContext;
	
	private final Intent mIntent;
	 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 50; // 50 meters, to be modified
 
    // The minimum time between updates in milliseconds
    private static long MIN_TIME_BW_UPDATES = 1000 *30 * 0; // 30 sec, to be modified
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
    
	private final String title = "My Bus Reminder"; 

 
    public LocationHandler(Context context, Intent intent) {
		// TODO Auto-generated constructor stub
    	this.mContext = context;
        this.mIntent = intent;
        getLocation();
	}

	public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            	// check if GPS enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
 
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationHandler.this);
        }
    }
 
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
 
        // return latitude
        return latitude;
    }
 
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
 
        // return longitude
        return longitude;
    }
 
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
 
    /**
     * --THIS FUNCTION IS NOT USED--
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
    
    /**
     * --THIS FUNCTION IS NOT WORKING--
     * Show alarm message dialog box: 
     */
    public void showAlarmMsg(String title, String message){
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	    alertDialog.setTitle(title);
	    alertDialog.setMessage(message);
	    alertDialog.setPositiveButton("OK", null);
	    alertDialog.show();	
    }
    
    /**
     * Show "My Bus Reminder" notification with red LED flashing
     */
    public void showNotification(String title, String message){
    	// Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(mContext)
            .setContentTitle(title)
            .setContentText(message).setSmallIcon(R.drawable.ic_launcher).build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.ledARGB = 0xFFff0000;
        noti.flags = Notification.FLAG_SHOW_LIGHTS;
        noti.ledOnMS = 500; 
        noti.ledOffMS = 500; 
        notificationManager.notify(random.nextInt(), noti);
    }
    
    
    /**
     * Get GPS coordinates of a location string: NOT IN USE FOR NOW    
     */
    public double[] getGPSofLocation(String locationName, Context context){
    	Geocoder geoCoder = new Geocoder(context);    
    	double[]gps = new double[2];
        try {
            List<Address> addresses = geoCoder.getFromLocationName(
                locationName, 5);
            
            if (addresses.size() > 0) {
            	double lat = addresses.get(0).getLatitude();
            	double lon = addresses.get(0).getLongitude();
            	gps[0] = lat;
            	gps[1] = lon;
            	return gps;
            	
            }    
            
        } catch (IOException e) {
            e.printStackTrace();
        }
		return gps;
    }
    
    /**
     * Play notification tone.
     */
    public void playNotificationTone(){
    	try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();
        } catch (Exception e) {}
    }
    
    /**
     * Get distance (in meters) between two locations with coordinates.
     */
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
                        (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang *6371;
        return dist*1000; // in meters
    }
    
    
    @Override
    public void onLocationChanged(Location location) { 
    	String dest = mIntent.getStringExtra("destination_str");
        double[]gps = getGPSofLocation(dest, mContext);//Testing Purpose
        double dist = getDistance(location.getLatitude(), location.getLongitude(), gps[0], gps[1]);//PASS PARAMETERS: gps coordinates <gps[0], gps[1]> of the destination bus stop
        //String targetGPS = "Lat: " + String.format("%.2f", gps[0]) + " Long: " + String.format("%.2f", gps[1]);  
        int distanceToAlarm = mIntent.getIntExtra("meters_to_alarm", -1); //(in meters) PASS PARAMETER: distanceToAlarm
        //If the user is near destination, e.g. <distanceToAlarm> meters away from the destinated bus stop
        
        if(dist < distanceToAlarm){
            playNotificationTone();
            String message = "You are " + (int)dist + " meters away from " + dest;
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();        
            //showAlarmMsg(title, message); <----CODE NOT WORKING, DON'T USE
            showNotification(title, message);
        }
    }
    
 
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
}