package jain.sip.client.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import android.javax.sip.DialogTerminatedEvent;
import android.javax.sip.IOExceptionEvent;
import android.javax.sip.InvalidArgumentException;
import android.javax.sip.ListeningPoint;
import android.javax.sip.ObjectInUseException;
import android.javax.sip.PeerUnavailableException;
import android.javax.sip.RequestEvent;
import android.javax.sip.ResponseEvent;
import android.javax.sip.ServerTransaction;
import android.javax.sip.SipException;
import android.javax.sip.SipFactory;
import android.javax.sip.SipListener;
import android.javax.sip.SipProvider;
import android.javax.sip.SipStack;
import android.javax.sip.TimeoutEvent;
import android.javax.sip.TransactionTerminatedEvent;
import android.javax.sip.TransportNotSupportedException;
import android.javax.sip.address.Address;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.address.SipURI;
import android.javax.sip.header.CSeqHeader;
import android.javax.sip.header.CallIdHeader;
import android.javax.sip.header.ContactHeader;
import android.javax.sip.header.ContentTypeHeader;
import android.javax.sip.header.FromHeader;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.header.MaxForwardsHeader;
import android.javax.sip.header.ToHeader;
import android.javax.sip.header.ViaHeader;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;
import android.javax.sip.message.Response;
import android.os.AsyncTask;
import android.util.Log;

public class SipLayer extends AsyncTask<String, Void, String> implements SipListener {
	private static SipStack sipStack;
	private SipProvider sipProvider;
	private SipFactory sipFactory;
	private MessageFactory messageFactory;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	private IMessageProcessor messageProcessor;
	
	private String username;
	private String ip;
	private int port;

	public SipLayer() {

	}
	public void initialize(String username, String ip, int port) {
		setUsername(username);
		setPort(port);
		setHost(ip);
		
		sipFactory = SipFactory.getInstance();
		sipFactory.resetFactory();
		sipFactory.setPathName("android.gov.nist");

		Properties properties = new Properties();
		properties.setProperty("android.javax.sip.STACK_NAME", "TextClient");
		properties.setProperty("android.javax.sip.IP_ADDRESS", ip);

		try {
			sipStack = sipFactory.createSipStack(properties);

			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();

			ListeningPoint tcp = sipStack.createListeningPoint(ip, port, "tcp");
			ListeningPoint udp = sipStack.createListeningPoint(ip, port, "udp");

			sipProvider = sipStack.createSipProvider(tcp);
			sipProvider.addSipListener(this);
			sipProvider = sipStack.createSipProvider(udp);
			sipProvider.addSipListener(this);
		} catch (PeerUnavailableException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendMessage(String to, String message) throws ParseException,
			InvalidArgumentException, SipException {

		SipURI from = addressFactory.createSipURI(getUsername(), getHost()
				+ ":" + getPort());
		Address fromNameAddress = addressFactory.createAddress(from);
		fromNameAddress.setDisplayName(getUsername());
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
				"textclientv1.0");

		String username = to.substring(to.indexOf(":") + 1, to.indexOf("@"));
		String address = to.substring(to.indexOf("@") + 1);

		SipURI toAddress = addressFactory.createSipURI(username, address);
		Address toNameAddress = addressFactory.createAddress(toAddress);
		toNameAddress.setDisplayName(username);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

		SipURI requestURI = addressFactory.createSipURI(username, address);
		requestURI.setTransportParam("udp");

		ArrayList viaHeaders = new ArrayList();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(),
				getPort(), "udp", "branch1");
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1,
				Request.MESSAGE);

		MaxForwardsHeader maxForwards = headerFactory
				.createMaxForwardsHeader(70);

		Request request = messageFactory.createRequest(requestURI,
				Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);

		SipURI contactURI = addressFactory.createSipURI(getUsername(),
				getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		ContactHeader contactHeader = headerFactory
				.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		ContentTypeHeader contentTypeHeader = headerFactory
				.createContentTypeHeader("text", "plain");
		request.setContent(message, contentTypeHeader);

		
		sipProvider.sendRequest(request);
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processRequest(RequestEvent evt) {
		Log.v("Your Message is Requested", evt.getRequest().toString());
		Request req = evt.getRequest();
		//System.out.println("Request Arrived");
		String method = req.getMethod();
		if (!method.equals("MESSAGE")) { // bad request type.
			messageProcessor.processError("Bad request type: " + method);
			return;
		}

		FromHeader from = (FromHeader) req.getHeader("From");
		messageProcessor.processMessage(from.getAddress().toString(),
				new String(req.getRawContent()));
		Response response = null;
		try { // Reply with OK
			response = messageFactory.createResponse(200, req);
			ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			toHeader.setTag("888"); // This is mandatory as per the spec.
			ServerTransaction st = sipProvider.getNewServerTransaction(req);
			st.sendResponse(response); 
			
		} catch (Throwable e) {
			e.printStackTrace();
			messageProcessor.processError("Can't send OK reply.");
		}

	}

	@Override
	public void processResponse(ResponseEvent evt) {
		Log.v("Your Message is Responsed", evt.getResponse().toString());
		Response response = evt.getResponse();
		int status = response.getStatusCode();

		if ((status >= 200) && (status < 300)) { // Success!
			messageProcessor.processInfo("--Sent");
			return;
		}

		messageProcessor.processError("Previous message not sent: " + status);

	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	private void setUsername(String username){
		this.username = username;
	}
	private String getUsername(){
		return this.username;
	}
	
	private void setPort(int port){
		this.port = port;
	}
	private int getPort(){
		return this.port;
	}
	
	private void setHost(String ip){
		this.ip = ip;
	}
	private String getHost(){
		return this.ip;
	}
	
	//Interface, That connects to the MainActity for message transport
	public IMessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(IMessageProcessor newMessageProcessor) {
		messageProcessor = newMessageProcessor;
	}

	@Override
	protected String doInBackground(String... arg0) {
		return null;
	}
}
