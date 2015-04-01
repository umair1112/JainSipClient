package org.uni.illuxplain.jain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import org.mobicents.restcomm.android.sdk.SipManagerState;
import org.mobicents.restcomm.android.sdk.impl.SipEvent;
import org.mobicents.restcomm.android.sdk.impl.SipEvent.SipEventType;

import android.gov.nist.javax.sip.message.SIPMessage;
import android.javax.sip.Dialog;
import android.javax.sip.DialogTerminatedEvent;
import android.javax.sip.IOExceptionEvent;
import android.javax.sip.InvalidArgumentException;
import android.javax.sip.ListeningPoint;
import android.javax.sip.ObjectInUseException;
import android.javax.sip.PeerUnavailableException;
import android.javax.sip.RequestEvent;
import android.javax.sip.ResponseEvent;
import android.javax.sip.ServerTransaction;
import android.javax.sip.SipFactory;
import android.javax.sip.SipListener;
import android.javax.sip.SipProvider;
import android.javax.sip.SipStack;
import android.javax.sip.TimeoutEvent;
import android.javax.sip.TransactionTerminatedEvent;
import android.javax.sip.address.Address;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.header.ViaHeader;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;

public class SipManager implements SipListener, Serializable {
	private static SipStack sipStack;
	public SipProvider sipProvider;
	public HeaderFactory headerFactory;
	public AddressFactory addressFactory;
	public MessageFactory messageFactory;
	public SipFactory sipFactory;

	private ListeningPoint udpListeningPoint;
	private SipProfile sipProfile;
	private Dialog dialog;
	// Save the created ACK request, to respond to retransmitted 2xx
	private Request ackRequest;
	private boolean ackReceived;

	// private ArrayList<ISipEventListener> sipEventListenerList = new
	// ArrayList<ISipEventListener>();
	private boolean initialized;
	private SipManagerState sipManagerState;

	public SipManager(SipProfile sipProfile) {
		this.sipProfile = sipProfile;
		initialize();
	}

	private void initialize() {
		sipManagerState = SipManagerState.REGISTERING;
		this.sipProfile.setLocalIp(true);

		sipFactory = SipFactory.getInstance();
		sipFactory.resetFactory();
		sipFactory.setPathName("android.gov.nist");

		Properties properties = new Properties();
		properties.setProperty(
				"android.javax.sip.OUTBOUND_PROXY",
				sipProfile.getRemoteEndpoint() + "/"
						+ sipProfile.getTransport());
		properties.setProperty("android.javax.sip.STACK_NAME", "androidSip");

		try {
			if (udpListeningPoint != null) {
				// Binding again
				sipStack.deleteListeningPoint(udpListeningPoint);
				sipProvider.removeSipListener(this);
			}
			sipStack = sipFactory.createSipStack(properties);
			System.out.println("createSipStack " + sipStack);
		} catch (PeerUnavailableException e) {
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			e.printStackTrace();
		}
		try {
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			udpListeningPoint = sipStack.createListeningPoint(
					sipProfile.getLocalIp(), sipProfile.getLocalPort(),
					sipProfile.getTransport());
			sipProvider = sipStack.createSipProvider(udpListeningPoint);
			sipProvider.addSipListener(this);
			initialized = true;
			sipManagerState = SipManagerState.READY;
		} catch (PeerUnavailableException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {

	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {

	}

	@Override
	public void processRequest(RequestEvent arg0) {
		Request request = (Request) arg0.getRequest();
		ServerTransaction serverTransactionId = arg0.getServerTransaction();
		SIPMessage sp = (SIPMessage) request;
		System.out.println(request.getMethod());
		if (request.getMethod().equals("MESSAGE")) {
			sendOk(arg0);

			try {
				String message = sp.getMessageContent();
				dispatchSipEvent(new SipEvent(this, SipEventType.MESSAGE,
						message, sp.getFrom().getAddress().toString()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (request.getMethod().equals(Request.BYE)) {
			sipManagerState = SipManagerState.IDLE;
			processBye(request, serverTransactionId);
			dispatchSipEvent(new SipEvent(this, SipEventType.BYE, "", sp
					.getFrom().getAddress().toString()));

		}
		if (request.getMethod().equals("INVITE")) {
			processInvite(arg0, serverTransactionId);
		}
	}
	
	


	@Override
	public void processResponse(ResponseEvent arg0) {

	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {

	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {

	}

	public SipProfile getSipProfile() {
		return sipProfile;
	}

	
	//ArrayList for setting the via Header in sip 
	public ArrayList<ViaHeader> createViaHeader() {
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader myViaHeader;
		try {
			myViaHeader = this.headerFactory.createViaHeader(
					sipProfile.getLocalIp(), sipProfile.getLocalPort(),
					sipProfile.getTransport(), null);
			myViaHeader.setRPort();
			viaHeaders.add(myViaHeader);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		return viaHeaders;

	}

	//Contact Address of UA
	public Address createContactAddress() {
		try {
			return this.addressFactory.createAddress("sip:"
					+ getSipProfile().getSipUserName() + "@"
					+ getSipProfile().getLocalEndpoint() + ";transport=udp"
					+ ";registering_acc=" + getSipProfile().getRemoteIp());
		} catch (ParseException e) {
			return null;
		}
	}
	
	private void processInvite(RequestEvent arg0,
			ServerTransaction serverTransactionId) {
		// TODO Auto-generated method stub
		
	}

	private void processBye(Request request,
			ServerTransaction serverTransactionId) {
		// TODO Auto-generated method stub
		
	}

	private void dispatchSipEvent(org.uni.illuxplain.jain.SipEvent sipEvent) {
		// TODO Auto-generated method stub
		
	}

	private void sendOk(RequestEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
