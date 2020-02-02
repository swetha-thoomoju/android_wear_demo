package io.swetha.wear_demo;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class WearOsMessageSender {

    private static final String TAG = WearOsMessageSender.class.getSimpleName();
    private static final String MSG_PATH = "/cordova/plugin/wearos";

    private Context context;

    public WearOsMessageSender(Context context){
        super();
        Log.i(TAG, "constructor");
        this.context = context;
    }

    public void sendMessage(String msg) throws Exception {
        Task<List<Node>> listTask = Wearable.getNodeClient(this.context).getConnectedNodes();
        List<Node> nodes = Tasks.await(listTask);
        for(Node node : nodes){
            MessageClient client = Wearable.getMessageClient(this.context);
            client.sendMessage(node.getId(), MSG_PATH, msg.getBytes());
        }
    }

}
