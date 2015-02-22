package com.elegancesoft.weardailer;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableStatusCodes;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Imran on 2015-01-25.
 */
public class MessageService extends Service {
    private static final String TAG = "MessageService";
    private static final String APPLICATION_PATH = "/elegancesoft/weardailer/";
    private static PhoneActions phact ;
    private final Handler mHandler = new Handler();
    private static String  msg ;
    private static String  mpath ;
    private static Node node;
    private static GoogleApiClient googleApiClient;




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //android.os.Debug.waitForDebugger();
        Context mContext = getApplicationContext();
        phact = (PhoneActions) new PhoneActionsImpl(mContext);
        messageReceive();
        updateContacts(phact.getContacts());

        /*** Load and Send Contacts ***/
        ContactService contactService = ContactService.getContactsInstance(getApplicationContext());
        contactService.loadContacts();
        ArrayList<Contact> contactlist = new ArrayList<Contact>();
        contactlist.addAll(contactService.getContacts().values());
        try {
            MessageService.syncContacts(contactService.createContactsMessageJSON(contactlist));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /**** End Load and Send Contacts *****/
        Log.d(TAG,"onStartCommand Called");
        Toast.makeText(this,"Wear Dialer Message Service Start", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }
    @Override
    public void onCreate() {
    }


   
    public void messageReceive(){
        googleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        googleApiClient.connect();
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                for (int i = 0; i < result.getNodes().size(); i++) {
                    node = result.getNodes().get(i);
                    String nName = node.getDisplayName();
                    String nId = node.getId();
                    Log.d(TAG, "Node name and ID: " + nName + " | " + nId);

                    Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
                        @Override
                        public void onMessageReceived(MessageEvent messageEvent) {
                            Log.d(TAG, "Message received: " + messageEvent.getPath());
                             msg = new String(messageEvent.getData());
                             mpath = new String(messageEvent.getPath());

                            switch (mpath) {
                                case APPLICATION_PATH + "call":
                                    phact.makeCall(msg);
                                    break;
                                case APPLICATION_PATH + "endCall":
                                    phact.endCall();
                                    break;
                                case APPLICATION_PATH + "muteOn":
                                    phact.muteON();
                                    break;
                                case APPLICATION_PATH + "muteOff":
                                    phact.muteOFF();
                                    break;
                                case APPLICATION_PATH + "isMuteOn":
                                    sendMessage(APPLICATION_PATH +"isMuteOn",String.valueOf(phact.isMuteON()),node);
                                    break;
                                case APPLICATION_PATH + "muteButtonPressed":
                                    phact.muteButtonPressed();
                                    break;
                                case APPLICATION_PATH + "speakerOn":
                                    phact.speakerON();
                                    break;
                                case APPLICATION_PATH + "speakerOff":
                                    phact.speakerOFF();
                                    break;
                                case APPLICATION_PATH + "speakerButtonPressed":
                                    phact.speakerButtonPressed();
                                    break;
                                case APPLICATION_PATH + "isSpeakerOn":
                                    sendMessage(APPLICATION_PATH +"isSpeakerOn",String.valueOf(phact.isSpeakerON()),node);
                                    break;
                                case APPLICATION_PATH + "bluetoothOn":
                                    phact.bluetoothON();
                                    break;
                                case APPLICATION_PATH + "bluetoothOff":
                                    phact.bluetoothOFF();
                                    break;
                                case APPLICATION_PATH + "isBluetoothOn":
                                    sendMessage(APPLICATION_PATH +"isBluetoothOn",String.valueOf(phact.isBluetoothON()),node);
                                    break;
                                case APPLICATION_PATH + "bluetoothButtonPressed":
                                    phact.bluetoothButtonPressed();
                                    break;
                                case APPLICATION_PATH + "test":
                                    sendMessage(APPLICATION_PATH +"test","Test done successfully",node);
                                    break;
                            }
                            updateDeviceStatus();

                        }
                    });
                }
            }
        });
    }

    public void updateDeviceStatus()
    {

        PutDataMapRequest dataMap = PutDataMapRequest.create(APPLICATION_PATH+"deviceStatus");
        dataMap.getDataMap().putBoolean("isSpeakerOn",phact.isSpeakerON());
        dataMap.getDataMap().putBoolean("isMuteOn",phact.isMuteON());
        dataMap.getDataMap().putBoolean("isBluetoothOn",phact.isBluetoothON());
        dataMap.getDataMap().putBoolean("isCalling",phact.isCalling());
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
    }

    public static void  syncContacts(String contactsStr) {
        Log.d(TAG,contactsStr);
        PutDataMapRequest dataMap = PutDataMapRequest.create(APPLICATION_PATH + "syncContacts");
        dataMap.getDataMap().putString("syncContacts", contactsStr);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
    }



    /**** Not in used any more ****/
    public void updateContacts(HashMap<String,String> contacts)
    {
        PutDataMapRequest dataMap = PutDataMapRequest.create(APPLICATION_PATH+"contacts");
        for(String contactId :contacts.keySet())
        {
            dataMap.getDataMap().putString(contactId,contacts.get(contactId));
        }
    }

    public  void sendMessage(String path,String data,Node node)
    {
        PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, data==null?null:data.getBytes());
        messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                Status status = sendMessageResult.getStatus();
                //Log.d(TAG, "Status: " + status.toString());
                if (status.getStatusCode() != WearableStatusCodes.SUCCESS) {
                    //alertButton.setProgress(-1);
                    //label.setText("Tap to retry. Alert not sent :(");
                }
            }
        });
    }

}
