package com.elegancesoft.weardailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Imran on 2015-01-24.
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MessageService.class);
        context.startService(startServiceIntent);
    }

}
