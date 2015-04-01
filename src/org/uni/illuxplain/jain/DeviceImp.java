package org.uni.illuxplain.jain;

import android.content.Context;

public class DeviceImp {
	private static DeviceImp deviceImpl;
	private Context context;
	private SipProfile sipProfile;
	
	public static DeviceImp getInstance(){
		if(deviceImpl == null){
			return new DeviceImp();
		}else{
			return deviceImpl;
		}
	}
	
	
	public void getInitStack(Context context ,SipProfile sipProfile){
		this.context = context; 
		this.sipProfile = sipProfile;
		
		SipManager sipManager = new SipManager(sipProfile);
	}
}
