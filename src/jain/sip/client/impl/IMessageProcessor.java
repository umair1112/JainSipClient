package jain.sip.client.impl;

public interface IMessageProcessor
{
    public void processMessage(String sender, String message);
    public void processError(String errorMessage);
    public void processInfo(String infoMessage);
}
