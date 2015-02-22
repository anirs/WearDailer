package com.elegancesoft.weardailer;

import java.util.HashMap;

/**
 * Created by Imran on 2015-01-18.
 */
public interface PhoneActions {
    public void speakerON();
    public void speakerOFF();
    public void speakerButtonPressed();
    public boolean isSpeakerON();
    public void muteON();
    public void muteOFF();
    public boolean isMuteON();
    public void muteButtonPressed();
    public void bluetoothON();
    public void bluetoothOFF();
    public boolean isBluetoothON();
    public void bluetoothButtonPressed();
    public void makeCall(String number);
    public void endCall();
    public boolean isCalling();
    public HashMap<String,String> getContacts();

}
