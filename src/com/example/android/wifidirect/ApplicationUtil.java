package com.example.android.wifidirect;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ApplicationUtil {

	
    //获取谷歌定位经纬度
    public static void getGrid(Context context, double latitude, double longtitude){
    	LocationManager manager = (LocationManager)context.getSystemService("location");
    	Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		String bestProvider = manager.getBestProvider(criteria, true);
		Location location = manager.getLastKnownLocation(bestProvider);
		if(location != null){
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
		}		
		
		//下面是监听位置状态变化，这个监听应该写在context里面。要用到的话，这个
		//函数返回manager
//		LocationListener locationListener = new LocationListener() {
//			
//			@Override
//			public void onStatusChanged(String provider, int status, Bundle extras) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onProviderEnabled(String provider) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onProviderDisabled(String provider) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onLocationChanged(Location location) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//		//更新设置
//		 manager.requestLocationUpdates(bestProvider, 2000, 10, locationListener);
    }
    
    
    	
    			
}
