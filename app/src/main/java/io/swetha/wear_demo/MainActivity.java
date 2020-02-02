package io.swetha.wear_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WEAR";
    private Intent intent;
    private WearOsServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    protected void init(){
        Log.i(TAG,"init");
        if (this.intent == null){
            this.intent = new Intent(getApplicationContext(), WearOsListenerService.class);
            this.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // Start service then bind; so onCreate is invoked on the 'Service' class
            getApplicationContext().startService(this.intent);
            this.serviceConnection = new WearOsServiceConnection();
            getApplicationContext().bindService(this.intent, this.serviceConnection, Context.BIND_AUTO_CREATE);

        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                registerMessageListener();
            }
        }, 5000);
    }


    protected void registerMessageListener() {
        Log.i(TAG,"registerMessageListener :: listener: ");
        if (this.serviceConnection != null) {
            WearOsMessageListener listener = createWearOsMessageListener();
            try {
                this.serviceConnection.getService().registerMessageListener(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private WearOsMessageListener createWearOsMessageListener(){
        Log.i(TAG,"createWearOsMessageListener");
        WearOsMessageListener listener = new WearOsMessageListener() {
            @Override
            public void messageReceived(String msg) {
                Log.i(TAG,"msg"+msg);
            }
        };
        return listener;
    }



    protected void sendMessage(JSONArray args){
        try {
            Log.i(TAG,"sendMessage :: args: "+args);
            if (this.serviceConnection != null && args != null) {
                Activity context = this;
                final WearOsMessageSender sender = new WearOsMessageSender(context);
                final String msg = args.getString(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sender.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        } catch (Exception ex){
            Log.i(TAG,"error");
            ex.printStackTrace();
        }
    }

    public void sendDummyMessage(View view) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("firstName", "John");
            jo.put("lastName", "Doe");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray ja = new JSONArray();
        JSONArray put = ja.put(jo);
        sendMessage(put);
    }
}
