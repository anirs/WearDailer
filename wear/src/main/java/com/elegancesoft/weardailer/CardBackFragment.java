package com.elegancesoft.weardailer;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by Imran on 2015-01-24.
 */

public class CardBackFragment extends Fragment {


    private DialerAdapter pagerAdapter;
    private static Button mbtEndCall;
    private static Button mbtSpeaker;
    private static Button mbtBluetooth;
    private static Button mbtMute;
    private static Activity mActivity;


    public CardBackFragment(){

    }
    public DialerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void setPagerAdapter(DialerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mActivity = getActivity();
        View v = inflater.inflate(R.layout.fragment_card_back, container, false);
          mbtEndCall = (Button)v.findViewById(R.id.btEndCall);
          mbtSpeaker = (Button)v.findViewById(R.id.btSpeaker);
          mbtBluetooth = (Button)v.findViewById(R.id.btBluetooth);
          mbtMute = (Button)v.findViewById(R.id.btMute);


        mbtEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerAdapter.isShowingBack()) {
                    Message ms = Message.getMessageInstance();
                    ms.connect(getPagerAdapter().mContext);
                    ms.sendMessage("endCall",null);
                    getFragmentManager().popBackStack();
                    pagerAdapter.setShowingBack(false);
                }
            }
        });

        mbtSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerAdapter.isShowingBack()) {
                    Message ms = Message.getMessageInstance();
                    ms.connect(getPagerAdapter().mContext);
                    ms.sendMessage("speakerButtonPressed",null);

                }
            }
        });

        mbtBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerAdapter.isShowingBack()) {
                    Message ms = Message.getMessageInstance();
                    ms.connect(getPagerAdapter().mContext);
                    ms.sendMessage("bluetoothButtonPressed",null);
                }
            }
        });

        mbtMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerAdapter.isShowingBack()) {
                    Message ms = Message.getMessageInstance();
                    ms.connect(getPagerAdapter().mContext);
                    ms.sendMessage("MuteButtonPressed",null);
                }
            }
        });

        return v ;
    }

    public static void updateLayout(final DataMap dm){
        mActivity.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(dm.getBoolean("Calling")) {
                    mbtEndCall.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                }else{
                     mbtEndCall.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    //mbtEndCall.setBackgroundResource(android.R.drawable.btn_default);
                }
                if(dm.getBoolean("isSpeakerOn")){
                    mbtSpeaker.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                }else {
                    mbtSpeaker.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
                if(dm.getBoolean("isMuteOn")){
                    mbtMute.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                }else {
                    mbtMute.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
                if(dm.getBoolean("isBluetoothOn")){
                    mbtBluetooth.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                }else {
                    mbtBluetooth.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
            }
        });
    }

}
