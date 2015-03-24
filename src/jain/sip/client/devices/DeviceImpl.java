package jain.sip.client.devices;

public class DeviceImpl {
	
	private static DeviceImpl device;
	private DeviceImpl(){
		
	}
	
	public static DeviceImpl getInstance(){
		if(device == null){
			return new DeviceImpl();
		}
		return device;
	}
	
	
	public void getInitStack(){
		
	}
	
	public void sendMessages(){
		
	}
}
