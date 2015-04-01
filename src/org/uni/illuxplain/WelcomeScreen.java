package org.uni.illuxplain;


import org.uni.illuxplain.jain.DeviceImp;
import org.uni.illuxplain.jain.SipProfile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class WelcomeScreen extends ActionBarActivity {

	private DeviceImp device;
	private SipProfile sipProfile;
	private GlobalApplication global;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen);
		//Create User Sip Profile
		int localPort = 5060;
		String transport = "udp";
		sipProfile = new SipProfile();
		global = GlobalApplication.getInstance();
		sipProfile.setSipUserName(global.getUsername());
		sipProfile.setLocalIp(true);
		sipProfile.setLocalPort(localPort);
		sipProfile.setTransport(transport);
		
		device = DeviceImp.getInstance();
		device.getInitStack(this.getApplicationContext(), sipProfile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
