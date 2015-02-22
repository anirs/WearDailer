package com.elegancesoft.weardailer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableStatusCodes;

import org.json.JSONException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainPhoneActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_phone);


        String phone = null;
        String emailContact = null;
        String emailType = null;
        String image_uri = "";
        StringBuffer sb = new StringBuffer();

        //Intent startServiceIntent = new Intent(this, MessageService.class);
        startService(new Intent(this, MessageService.class));
        Log.d(TAG,"Service is Called");


        sb.append("......Contact Details.....");

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //sb.append("\n Contact id:" + id);
                //sb.append("\n Contact Name:" + name);

                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //System.out.println("name : " + name + ", ID : " + id);
                    sb.append("\n Contact Name:" + name);
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        sb.append("\n Phone number:" + phone);
                        //System.out.println("phone" + phone);
                    }
                    pCur.close();
                    sb.append("\n........................................");
                }

            }
        }
        TextView mContactView = (TextView) findViewById(R.id.textView1);
        mContactView.setText(sb);
        mContactView.setMovementMethod(new ScrollingMovementMethod());
        //setContentView(mContactView);


        //phact = (PhoneActions) new PhoneActionsImpl(this);
       // messageReceived();

        /**** Test Contacts List JSON *****/
        TextView mContactJSONView = (TextView) findViewById(R.id.textView2);
        ContactService contactService = ContactService.getContactsInstance(getApplicationContext());
        contactService.loadContacts();
        ArrayList<Contact> contactlist = new ArrayList<Contact>();
        contactlist.addAll(contactService.getContacts().values());
        try {
            mContactJSONView.setText(contactService.createContactsMessageJSON(contactlist));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContactJSONView.setMovementMethod(new ScrollingMovementMethod());

        /**** End Contacts List JSON ******/


        Button wearButton = (Button) findViewById(R.id.button1);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 001;

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                        MainPhoneActivity.this).setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Title")
                        .setContentText("Android Wear Notification");

                NotificationManagerCompat notificationManager = NotificationManagerCompat
                        .from(MainPhoneActivity.this);

                notificationManager.notify(notificationId,
                        notificationBuilder.build());
                Toast.makeText(MainPhoneActivity.this, R.string.popuptext,
                        Toast.LENGTH_SHORT).show();
                performDial("611");

            }
        });

        Button mEndCall = (Button) findViewById(R.id.button2);
        mEndCall.setOnClickListener(new View.OnClickListener() {
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

            @Override
            public void onClick(View v) {
				/*
				 * TelephonyManager telephony = (TelephonyManager)
				 * getSystemService(Context.TELEPHONY_SERVICE); Class c =
				 * Class.forName(telephony.getClass().getName()); Method m =
				 * c.getDeclaredMethod("getITelephony"); m.setAccessible(true);
				 * com.android.internal.telephony.ITelephony telephonyService =
				 * (ITelephony) m.invoke(tm);
				 *
				 * telephonyService = (ITelephony) m.invoke(telephony);
				 */

                try {
                    // Method getService;
                    Object telephonyObject;
                    Object serviceManagerObject;

                    telephonyClass = Class.forName(telephonyName);
                    telephonyStubClass = telephonyClass.getClasses()[0];
                    serviceManagerClass = Class.forName(serviceManagerName);
                    serviceManagerNativeClass = Class
                            .forName(serviceManagerNativeName);
                    telephonyEndCall = telephonyClass.getMethod("endCall");
                    Method getService = // getDefaults[29];
                            serviceManagerClass.getMethod("getService", String.class);

                    Method tempInterfaceMethod = serviceManagerNativeClass
                            .getMethod("asInterface", IBinder.class);

                    Binder tmpBinder = new Binder();
                    tmpBinder.attachInterface(null, "fake");

                    serviceManagerObject = tempInterfaceMethod.invoke(null,
                            tmpBinder);
                    IBinder retbinder = (IBinder) getService.invoke(
                            serviceManagerObject, "phone");
                    Method serviceMethod = telephonyStubClass.getMethod(
                            "asInterface", IBinder.class);

                    telephonyObject = serviceMethod.invoke(null, retbinder);
                    telephonyEndCall = telephonyClass.getMethod("endCall");
                    telephonyEndCall.invoke(telephonyObject);
                    Toast.makeText(MainPhoneActivity.this, R.string.popuptext2,
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Log.error(DialerActivity.this,
                    // "FATAL ERROR: could not connect to telephony subsystem");
                    // Log.error(DialerActivity.this, "Exception object: " + e);
                }

            }

        });

        Button mButtonSpeaker = (Button) findViewById(R.id.button3);
        mButtonSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);

                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    Toast.makeText(MainPhoneActivity.this, "Speaker Off",
                            Toast.LENGTH_SHORT).show();
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    Toast.makeText(MainPhoneActivity.this, "Speaker On.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button mButtonBluetooth = (Button) findViewById(R.id.button4);
        mButtonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

				/*
				 * BluetoothAdapter mBluetoothAdapter =
				 * BluetoothAdapter.getDefaultAdapter(); Set<BluetoothDevice>
				 * pairedDevices = mBluetoothAdapter.getBondedDevices();
				 *
				 * if (pairedDevices.size() > 0) { // Loop through paired
				 * devices for (BluetoothDevice device : pairedDevices) {
				 *
				 * BluetoothDevice mdevice =
				 * mBluetoothAdapter.getRemoteDevice(device.getAddress()); try {
				 * mdevice.createRfcommSocketToServiceRecord(null); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } Toast.makeText(MainPhoneActivity.this,
				 * device.getName()+" "+device.getBondState(),
				 * Toast.LENGTH_SHORT).show();
				 *
				 * } }
				 */

                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);

                if (audioManager.isBluetoothScoOn()) {
                    audioManager.setBluetoothScoOn(false);
                    Toast.makeText(MainPhoneActivity.this, "Bluetooth Off",
                            Toast.LENGTH_SHORT).show();
                } else {
                    audioManager.setBluetoothScoOn(true);
                    Toast.makeText(MainPhoneActivity.this, "Bluetooth On.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainPhoneActivity.this).addApi(Wearable.API).build();
        googleApiClient.connect();

        Button mButtonSendMsg = (Button) findViewById(R.id.button5);
        mButtonSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                Toast.makeText(MainPhoneActivity.this, "Sync Pressed", Toast.LENGTH_SHORT).show();

                ContactService contactService = ContactService.getContactsInstance(getApplicationContext());
                contactService.loadContacts();
                ArrayList<Contact> contactlist = new ArrayList<Contact>();
                contactlist.addAll(contactService.getContacts().values());
                try {
                    MessageService.syncContacts(contactService.createContactsMessageJSON(contactlist));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


/*
                startService(new Intent(MainPhoneActivity.this, MessageService.class));
                Log.d(TAG,"Service is Called2 ");

                String nodeId = null;
                PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
//                NodeApi.GetConnectedNodesResult result =   Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
//                List<Node> nodes = result.getNodes();
//                if (nodes.size() > 0) {
//                    nodeId = nodes.get(0).getId();

//                }

                nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult result) {
                        for (int i = 0; i < result.getNodes().size(); i++) {
                            Node node = result.getNodes().get(i);
                            String nName = node.getDisplayName();
                            String nId = node.getId();
                            // Log.d(TAG, "Node name and ID: " + nName + " | " + nId);

                            Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
                                @Override
                                public void onMessageReceived(MessageEvent messageEvent) {
                                    //Log.d(TAG, "Message received: " + messageEvent);
                                }
                            });

                            PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), "test", "hello 1".getBytes());
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
                            Toast.makeText(MainPhoneActivity.this, "Message Send.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

            }
        });
    }

    private void performDial(String numberString) {
        if (!numberString.equals("")) {
            Uri number = Uri.parse("tel:" + numberString);
            Intent dial = new Intent(Intent.ACTION_CALL, number);

            startActivity(dial);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    static String msg ="";
    static String mpath= "";
    static PhoneActions phact;
    static final private String APPLICATION_PATH="elegancesoft/weardailer/";
    static final String TAG = "MainPhoneActivity";

    public void messageReceived() {
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainPhoneActivity.this).addApi(Wearable.API).build();
        googleApiClient.connect();
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                for (int i = 0; i < result.getNodes().size(); i++) {
                    Node node = result.getNodes().get(i);
                    String nName = node.getDisplayName();
                    String nId = node.getId();
//                            Log.d(TAG, "Node name and ID: " + nName + " | " + nId);

                    Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
                        @Override
                        public void onMessageReceived(MessageEvent messageEvent) {
                            Log.d(TAG, "Message received: " + messageEvent.getPath());
                            //showToast(messageEvent.getPath());
                            msg = new String(messageEvent.getData());
                            mpath = new String(messageEvent.getPath());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //TextView tv = (TextView) findViewById(R.id.quote);//Text To be edited
                                    //mTextView.setText(msg);//Set the Text
                                    switch (mpath)
                                    {
                                        case APPLICATION_PATH+"call":
                                            phact.makeCall(msg);
                                            break;
                                        case APPLICATION_PATH+"endCall":
                                            phact.endCall();
                                            break;
                                        case APPLICATION_PATH+"muteOn":
                                            phact.muteON();
                                            break;
                                        case APPLICATION_PATH+"muteOff":
                                            phact.muteOFF();
                                            break;
                                        case APPLICATION_PATH+"isMuteON":
                                            phact.isMuteON();
                                            break;
                                        case APPLICATION_PATH+"speakerOn":
                                            phact.speakerOFF();
                                            break;
                                        case APPLICATION_PATH+"speakerOff":
                                            phact.speakerOFF();
                                            break;
                                        case APPLICATION_PATH+"isSpeakerON":
                                            phact.isSpeakerON();
                                            break;
                                        case APPLICATION_PATH+"bluetoothOn":
                                            phact.bluetoothON();
                                            break;
                                        case APPLICATION_PATH+"bluetoothOff":
                                            phact.bluetoothOFF();
                                            break;


                                    }

                                }
                            });
                            //MainWearActivity.this.recreate();
                            //mTextView.setText(messageEvent.getPath());

                        }
                    });
                }
            }
        });
    }
}
