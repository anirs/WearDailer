package com.elegancesoft.weardailer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Imran on 2015-01-18.
 */
public class PhoneActionsImpl implements PhoneActions {
    private static final String TAG = "PhoneActionsImpl";

    private boolean calling = false;
    private Context mContext;
    private AudioManager audioManager;

    public PhoneActionsImpl(Context mContext) {
        this.mContext = mContext;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

    }

    public boolean isSpeakerON() {
        return audioManager.isSpeakerphoneOn();
    }



    public boolean isBluetoothON() {
        return audioManager.isBluetoothScoOn();
    }

    @Override
    public void bluetoothButtonPressed() {
        Log.d(TAG,"Bluetooth Button Pressed");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(audioManager.isBluetoothScoOn()){
            audioManager.setBluetoothScoOn(false);
        }else
        {
            audioManager.setBluetoothScoOn(true);
        }
    }


    public boolean isMuteON() {
        return audioManager.isMicrophoneMute();
    }

    @Override
    public void muteButtonPressed() {
        Log.d(TAG,"Mute Button Pressed");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(audioManager.isMicrophoneMute()){
            audioManager.setMicrophoneMute(false);
        }else
        {
            audioManager.setMicrophoneMute(true);
        }
    }


    public boolean isCalling() {
        return calling;
    }

    public void setCalling(boolean calling) {
        this.calling = calling;
    }


    @Override
    public void speakerON() {
        Log.d(TAG,"Speaker ON");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
        //setSpeakerON(audioManager.isSpeakerphoneOn());
    }

    public void speakerButtonPressed(){
        Log.d(TAG,"Speaker Button Pressed");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(audioManager.isSpeakerphoneOn()){
            audioManager.setSpeakerphoneOn(false);
        }else
        {
            audioManager.setSpeakerphoneOn(true);
        }
    }

    @Override
    public void speakerOFF() {
        Log.d(TAG,"Speaker OFF");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);
       // setSpeakerON(audioManager.isSpeakerphoneOn());
    }

    @Override
    public void muteON() {
        Log.d(TAG,"Mute ON");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMicrophoneMute(true);
       // setMuteON(audioManager.isMicrophoneMute());
    }

    @Override
    public void muteOFF() {
        Log.d(TAG,"Mute OFF");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMicrophoneMute(false);
        //setMuteON(audioManager.isMicrophoneMute());
    }

    @Override
    public void bluetoothON() {
        Log.d(TAG,"Mute OFF");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMicrophoneMute(false);
       // setMuteON(audioManager.isMicrophoneMute());
    }

    @Override
    public void bluetoothOFF() {
        Log.d(TAG,"Mute OFF");
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setMicrophoneMute(false);
        //setMuteON(audioManager.isMicrophoneMute());
    }

    @Override
    public void makeCall(String numberString) {
        Log.d(TAG,"Start making Call to '"+numberString+"'");
        if (!numberString.equals("")) {
            Uri number = Uri.parse("tel:" + numberString);
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            dial.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(dial);
        }
        setCalling(true);

    }

    @Override
    public void endCall() {
        Log.d(TAG,"End Call");
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerStubClass;
            Class serviceManagerNativeClass;
            Class serviceManagerNativeStubClass;

            Method telephonyCall;
            Method telephonyEndCall;
            Method telephonyAnswerCall;
            Method getDefault;
            // Method getService;
            Object telephonyObject;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            Method getService =  serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null,tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            Log.e(TAG,"ERROR "+e.getMessage());
            e.printStackTrace();
        }
        setCalling(false);

    }

    @Override
    public HashMap<String, String> getContacts() {
        Log.d(TAG,"Getting Contacts");
        String phone = null;
        String emailContact = null;
        String emailType = null;
        String image_uri = "";

        HashMap<String,String> contacts = new HashMap<String,String>();

        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            StringBuffer sb = new StringBuffer();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    sb.append(name+",");
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        sb.append(phone+",");
                    }
                    pCur.close();
                    String s = sb.substring(0,sb.length()-1);
                    contacts.put(id,s);
                    //sb.append("\n........................................");
                }

            }
        }

        return contacts;
    }
}
