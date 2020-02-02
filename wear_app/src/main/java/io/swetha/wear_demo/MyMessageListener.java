package io.swetha.wear_demo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;

import java.nio.charset.StandardCharsets;

public class MyMessageListener implements MessageClient.OnMessageReceivedListener {

    private static final String TAG = "AndroidWatch";
    private static final String MESSAGE_PATH = "/cordova/plugin/wearos";

    private Context context;

    public MyMessageListener(Context context) {
        this.context = context;
        Log.i(TAG, "constructor");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG,"onMessageReceived");
        if(messageEvent != null && MESSAGE_PATH.equals(messageEvent.getPath())){
            String message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.i(TAG,("Message received: " + message));
            System.out.println("Message received: " + message);
            TextView textViewToChange = ((Activity)context).findViewById(R.id.text);
            textViewToChange.setText(message);
        }
    }

}