package org.uni.illuxplain.jain;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

public class SipProfile {
	private String localIp;
	private int localPort;
	private String transport;

	private String remoteIp = "127.0.0.1";
	private int remotePort = 5080;
	private String sipUserName;
	private String sipPassword;

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(boolean ipV4) {	
		this.localIp = getLocalIpAddress(ipV4);
		System.out.println("Setting localIp:" + localIp);
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		System.out.println("Setting localPort:" + localPort);
		this.localPort = localPort;
	}

	public String getLocalEndpoint() {
		return localIp + ":" + localPort;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		System.out.println("Setting remoteIp:" + remoteIp);
		this.remoteIp = remoteIp;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		System.out.println("Setting remotePort:" + remotePort);
		this.remotePort = remotePort;
	}

	public String getRemoteEndpoint() {
		return remoteIp + ":" + remotePort;
	}

	public String getSipUserName() {
		return sipUserName;
	}

	public void setSipUserName(String sipUserName) {
		System.out.println("Setting sipUserName:" + sipUserName);
		this.sipUserName = sipUserName;
	}

	public String getSipPassword() {
		return sipPassword;
	}

	public void setSipPassword(String sipPassword) {
		System.out.println("Setting sipPassword:" + sipPassword);
		this.sipPassword = sipPassword;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		System.out.println("Setting transport:" + transport);
		this.transport = transport;
	}

	// Local Ip of the device
	private String getLocalIpAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

}
