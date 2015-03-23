package jain.jainsipclientside;

import jain.sip.client.impl.IMessageProcessor;
import jain.sip.client.impl.SipLayer;
import android.app.ProgressDialog;
import android.javax.sip.InvalidArgumentException;
import android.javax.sip.SipException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		IMessageProcessor {

	private SipLayer sipManager = null;
	private String to;
	private String from;
	private String message;
	private EditText toE;
	private EditText fromE;
	private EditText messageE;
	private TextView ackMessage;
	private Handler handler;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler = new Handler();
		String username = "mubeen";
		int port = 5060;
		String ip = "10.0.2.15";

		toE = (EditText) findViewById(R.id.to_client_edit);
		fromE = (EditText) findViewById(R.id.from_client_edit);
		messageE = (EditText) findViewById(R.id.create_message);
		ackMessage = (TextView) findViewById(R.id.text_message);
		Button sendButton = (Button) findViewById(R.id.send_button);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Sending your Message...");
		progressDialog.setCancelable(true);
		
		sipManager = new SipLayer();
		sipManager.initialize(username, ip, port);
		sipManager.setMessageProcessor(this);
		from = "sip:" + username + "@" + ip + ":" + port;
		fromE.setText(from);

		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSendMessage();
			}

		});
	}

	private void getSendMessage() {
		this.to = toE.getText().toString();
		this.message = messageE.getText().toString();

		Runnable doNetworkOperation = new Runnable() {

			@Override
			public void run() {
				try {
					sipManager.sendMessage(to, message);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						//progressDialog.show();
					}
				});
			}
		};
		//Toast.makeText(this, "Sending..", Toast.LENGTH_SHORT).show();
		doNetworkOperation.run();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	@Override
	public void processMessage(String sender, String message) {
		ackMessage.append(sender + ":  " + message);

	}

	@Override
	public void processError(String errorMessage) {
		ackMessage.append(errorMessage);

	}

	@Override
	public void processInfo(String infoMessage) {
		ackMessage.append(infoMessage);

	}
}
