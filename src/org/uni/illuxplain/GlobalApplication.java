package org.uni.illuxplain;

public class GlobalApplication {
	private static GlobalApplication globalApp;
	private static String username;

	public static GlobalApplication getInstance() {
		if (globalApp == null) {
			return new GlobalApplication();
		} else {
			return globalApp;
		}
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return username;
	}
}
