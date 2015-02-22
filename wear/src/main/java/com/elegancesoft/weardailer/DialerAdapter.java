package com.elegancesoft.weardailer;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.GridPagerAdapter;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.elegancesoft.weardailer.dummy.DummyContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class DialerAdapter extends GridPagerAdapter{

    private static final String TAG = "DialerAdapter";
    final Context mContext;
    final FragmentManager fm;
    static String phonenumber = "";
    private static TextView mphno;
    private static TextView mContact;
    private  boolean showingBack;
    private static ContactService contactService ;

    public DialerAdapter(final Context context, FragmentManager fm) {
        mContext = context.getApplicationContext();
        contactService = ContactService.getContactsInstance(mContext);
        this.fm = fm;
    }

    @Override
    public int getRowCount() {
        return 3;
    }

    @Override
    public int getColumnCount(int i) {
        if(i == 1) {
            return 1;
        } else {
            return 4;
        }
    }

    @Override
    public int getCurrentColumnForRow(int row, int currentColumn) {
        int ret = super.getCurrentColumnForRow(row, currentColumn);
        Log.d("test", String.format("getCurColForRow: ret=%d   row=%d   curCol=%d", ret, row, currentColumn));
        return super.getCurrentColumnForRow(row, currentColumn);
    }

    @Override
    protected Object instantiateItem(ViewGroup viewGroup, int row, int col) {
        Log.d("test", "instantiateItem: row=" + row + " col=" + col);
        View v;
        contactService.loadContacts();

        if(row == 1){
            v = View.inflate(mContext, R.layout.dialer_front, null);
            ImageView mCall = (ImageView) v.findViewById(R.id.btcall);
            ImageView mbtdback = (ImageView) v.findViewById(R.id.btdback);
            ImageView mbtd1 = (ImageView) v.findViewById(R.id.btd1);
            ImageView mbtd2 = (ImageView) v.findViewById(R.id.btd2);
            ImageView mbtd3 = (ImageView) v.findViewById(R.id.btd3);
            ImageView mbtd4 = (ImageView) v.findViewById(R.id.btd4);
            ImageView mbtd5 = (ImageView) v.findViewById(R.id.btd5);
            ImageView mbtd6 = (ImageView) v.findViewById(R.id.btd6);
            ImageView mbtd7 = (ImageView) v.findViewById(R.id.btd7);
            ImageView mbtd8 = (ImageView) v.findViewById(R.id.btd8);
            ImageView mbtd9 = (ImageView) v.findViewById(R.id.btd9);
            ImageView mbtd0 = (ImageView) v.findViewById(R.id.btd0);
            mphno = (TextView) v.findViewById(R.id.phonenumber);
            mContact = (TextView) v.findViewById(R.id.contactResult);

            final Message msg = Message.getMessageInstance();
            msg.connect(mContext);

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardBackFragment cardBackFragment = new CardBackFragment();
                cardBackFragment.setPagerAdapter(DialerAdapter.this);
                msg.sendMessage("call",phonenumber);
                fm
                  .beginTransaction()
                   .setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out, R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                   .replace(R.id.watch_view_stub, cardBackFragment)
                   .addToBackStack(null)
                   .commit();
                setShowingBack(true);
/* Message Code Begin
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

                            PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), "elegancesoft/call", phonenumber.getBytes());
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
                });
*/
                msg.sendMessageTest("test", phonenumber, true);
                Log.d("TEST", "Clicked Test");
            }
        });
        mbtd1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "1";
                    updateDialerView();
                }
            });
            mbtd2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "2";
                    updateDialerView();
                }
            });
            mbtd3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "3";
                    updateDialerView();
                }
            });
            mbtd4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "4";
                    updateDialerView();
                }
            });
            mbtd5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "5";
                    updateDialerView();
                }
            });
            mbtd6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "6";
                    updateDialerView();
                }
            });
            mbtd7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "7";
                    updateDialerView();
                }
            });
            mbtd8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "8";
                    updateDialerView();
                }
            });
            mbtd9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "9";
                    updateDialerView();
                }
            });
            mbtd0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonenumber += "0";
                    updateDialerView();
                }
            });
            mbtdback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(phonenumber.length()>0) {
                        phonenumber = phonenumber.substring(0, phonenumber.length() - 1);
                        updateDialerView();
                    }
                }
            });
        }else if(row == 2 && col == 0){
            v = View.inflate(mContext, R.layout.fragment_contact, null);
            AbsListView mListView = (AbsListView) v.findViewById(android.R.id.list);

            ListAdapter mAdapter = new ArrayAdapter<DummyContent.DummyItem>(mContext,
                    android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);

            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
        else {
            Random random = new Random();
            v = View.inflate(mContext, R.layout.testview, null);
            v.setBackgroundColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        }
        viewGroup.addView(v);
        return v;
    }

    private void updateDialerView(){
        mphno.setText((CharSequence) phonenumber);
        Log.d(TAG,"phonenumber :"+phonenumber);
        HashMap<String,ArrayList<String>> contacts = contactService.searchContact(phonenumber);
        Log.d(TAG,"contact length"+contacts.size());
        if(contacts.size() > 0)
        {
            contactService.printSearchContacts(contacts);
            String [] searchKey= contacts.keySet().toArray(new String[0]);
            String name = contactService.getContact(contacts.get(searchKey[0]).get(0)).getName();
            mContact.setText(Html.fromHtml(contactService.setSearchContactColor(name, searchKey[0])));
        }
    }

    @Override
    protected void destroyItem(ViewGroup viewGroup, int i, int i2, Object o) {
        Log.d("test", "destroyItem  row=" + i + " col=" +i2);
        viewGroup.removeView((View)o);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        Log.d("test", "isViewFromObject");
        return view.equals(o);
    }

    public  boolean isShowingBack() {
        return showingBack;
    }

    public  void setShowingBack(boolean showingBack) {
        this.showingBack = showingBack;
    }
}
