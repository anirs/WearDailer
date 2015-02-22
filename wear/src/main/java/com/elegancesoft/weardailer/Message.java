package com.elegancesoft.weardailer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.WearableStatusCodes;

/**
 * Created by Imran on 2015-01-23.
 */
public class Message extends WearableListenerService {
    private GoogleApiClient googleApiClient;
    private String nodeId = null;
    private Node node;
    private boolean connection;
    private final String TAG = "Message";
    private static final String  APPLICATION = "/elegancesoft/weardailer/";
    private static volatile Message instance = null;
    private static String responseValue = "";
    private Context mContext;
    private static Activity mActivity;
    private static Thread messageSendThread;
    private static String method;

    public Message() {

    }

    public static String getMethod() {
        return method;
    }

    public static void setMethod(String method) {
        Message.method = method;
    }

    public static void  setActivity(Activity activity){
        mActivity = activity;
    }

    public boolean isConnection() {
        return connection;
    }

    public static Message getMessageInstance()
    {
        if (instance == null) {
            synchronized (Message.class) {
                if (instance == null) {
                    instance = new Message();
                }
            }
        }

        return instance;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void connect(Context mContext){
        this.mContext = mContext;
        googleApiClient = new GoogleApiClient.Builder(mContext).addApi(Wearable.API).build();
        googleApiClient.connect();
        messageReceiver();

        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
        //NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                    @Override
                                    public void onResult(NodeApi.GetConnectedNodesResult result) {
                                        node = result.getNodes().get(0);
                                        Log.d(TAG, "Node ID:" + node.getDisplayName());
                                    }
                                });
        setConnection(true);


    }

    public String sendMessage(final String method,String data)
    {
        return sendMessage(method,data,false);
    }
    public String  sendMessage(final String method,String data,Boolean returnValue)
    {
        responseValue= "";

        if(isConnection()){
            PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), APPLICATION+method, data==null?null:data.getBytes());
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
/*          new Thread() {
                @Override
                public void run() {
            Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
                        @Override
                        public void onMessageReceived(MessageEvent messageEvent) {

                            String mPath = messageEvent.getPath();
                            final String mData = new String(messageEvent.getData());
                            Log.d(TAG, "Path Value :"+mPath+" Data:"+mData+" compare : "+mPath.equalsIgnoreCase(application+method));
                            if(mPath.equalsIgnoreCase(application+method)){
                                        synchronized(Message.this) {
                                            responseValue = mData;
                                            Log.d(TAG, "Notify Response value :"+responseValue);
                                            Message.this.notify();
                                       }


                            }
                        }
                    });

                        }
            }.start();
           if(returnValue)
            {

                    try{
                        synchronized(this) {
                            Log.d(TAG, "Wait Response value :"+responseValue);
                            wait();
                        }


                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Getting Value :"+responseValue);
            }*/
            Log.d(TAG, "Message Sent");
        }else{
            Log.d(TAG,"Connection Down !!!!");
        }
        Log.d(TAG, "responseValue : "+responseValue);
        return responseValue;

    }

    public String  sendMessageTest(final String method,final String data,Boolean returnValue)
    {
        responseValue= "";
        setMethod(method);

        if(isConnection()){
            messageSendThread = new Thread(){
                @Override
                public void run() {
                    synchronized (this) {
                        Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), APPLICATION + method, data == null ? null : data.getBytes()).await();
                        try {
                            wait();
                            Log.d(TAG, "Notified :");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            messageSendThread.start();

/*          new Thread() {
                @Override
                public void run() {
            Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
                        @Override
                        public void onMessageReceived(MessageEvent messageEvent) {

                            String mPath = messageEvent.getPath();
                            final String mData = new String(messageEvent.getData());
                            Log.d(TAG, "Path Value :"+mPath+" Data:"+mData+" compare : "+mPath.equalsIgnoreCase(application+method));
                            if(mPath.equalsIgnoreCase(application+method)){
                                        synchronized(Message.this) {
                                            responseValue = mData;
                                            Log.d(TAG, "Notify Response value :"+responseValue);
                                            Message.this.notify();
                                       }


                            }
                        }
                    });

                        }
            }.start();
           if(returnValue)
            {

                    try{
                        synchronized(this) {
                            Log.d(TAG, "Wait Response value :"+responseValue);
                            wait();
                        }


                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Getting Value :"+responseValue);
            }*/
            Log.d(TAG, "Message Sent");
        }else{
            Log.d(TAG,"Connection Down !!!!");
        }
        Log.d(TAG, "responseValue : "+responseValue);
        return responseValue;

    }

    public void messageReceiver()
    {
        Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {

                String mPath = messageEvent.getPath();
                final String mData = new String(messageEvent.getData());
                Log.d(TAG, "Path Value :"+mPath+" Data:"+mData+" compare : "+mPath.equalsIgnoreCase(APPLICATION+getMethod()));
                if(mPath.equalsIgnoreCase(APPLICATION+method)){
                    synchronized(messageSendThread) {
                        responseValue = mData;
                        Log.d(TAG, "Notify Response value :"+responseValue);
                        messageSendThread.notify();
                    }
                }
            }
        });
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(APPLICATION+"deviceStatus")) {
                    DataMap dm = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    CardBackFragment.updateLayout(dm);
                    Log.d(TAG, "Speaker ON :" + dm.getBoolean("isSpeakerOn") + " Mute On :" + dm.getBoolean("isMuteOn"));
                }
                if (path.equals(APPLICATION+"contacts")) {
                    Log.d(TAG,"contacts");
                }
                if (path.equals(APPLICATION+"syncContacts")) {
                    DataMap dm = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.d(TAG,dm.getString("syncContacts"));
                    ContactService.getContactsInstance(this.getApplicationContext()).saveContacts(dm.getString("syncContacts"));
                    Log.d(TAG,"syncContacts");
                }
            }
        }
    }
}
