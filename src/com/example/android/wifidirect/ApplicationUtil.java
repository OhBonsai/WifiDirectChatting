package com.example.android.wifidirect;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ApplicationUtil {

	
    //��ȡ�ȸ趨λ��γ��
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
		
		//�����Ǽ���λ��״̬�仯���������Ӧ��д��context���档Ҫ�õ��Ļ������
		//��������manager
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
//		//��������
//		 manager.requestLocationUpdates(bestProvider, 2000, 10, locationListener);
    }
    
    
    	
    			
}
