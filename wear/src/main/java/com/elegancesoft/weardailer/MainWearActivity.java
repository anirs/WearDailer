package com.elegancesoft.weardailer;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.widget.Toast;

public class MainWearActivity extends Activity {

    private TextView mTextView;
    private String msg;
    private String TAG = "MainWearActivity";
    private  GridViewPager pager;
    private DialerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_wear);
//        setContentView(R.layout.grid);
//
//
//                final GridViewPager mGridPager = (GridViewPager) findViewById(R.id.pager);
//                Log.d(TAG, mGridPager + "");
//                SampleGridPagerAdapter mSampleGridPagerAdapter = new SampleGridPagerAdapter(MainWearActivity.this, getFragmentManager());
//                Log.d(TAG, mSampleGridPagerAdapter.getRowCount() + "");
//                mGridPager.setAdapter(mSampleGridPagerAdapter);
        Message.setActivity(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                 pager = (GridViewPager)stub.findViewById(R.id.pager);
                pagerAdapter =  new DialerAdapter(MainWearActivity.this,getFragmentManager());
                pager.setAdapter(pagerAdapter);
            }
        });






        /* Send message code ***

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);


            }
        });
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainWearActivity.this).addApi(Wearable.API).build();

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
                            //Log.d(TAG, "Message received: " + messageEvent);
                            //showToast(messageEvent.getPath());
                            msg = new String(messageEvent.getData());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //TextView tv = (TextView) findViewById(R.id.quote);//Text To be edited
                                    mTextView.setText(msg);//Set the Text
                                }
                            });
                            //MainWearActivity.this.recreate();
                            //mTextView.setText(messageEvent.getPath());

                        }
                    });
                }
            }
        });*/
    }




    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
