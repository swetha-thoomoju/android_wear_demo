package io.swetha.wear_demo;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeClient;

import java.util.List;

import static com.google.android.gms.wearable.Wearable.getMessageClient;
import static com.google.android.gms.wearable.Wearable.getNodeClient;

public class MainActivity extends WearableActivity {

    private static final String TAG = "AndroidWatch";
    private TextView mTextView;
    private MyMessageListener messageListener;
    private Context context;
    public String message = "HI am here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
        context = this;
        listenToMessages(this);
    }

    protected void listenToMessages(Context context) {
        Log.i(TAG,"listenToMessages");
        if (this.messageListener == null) {
            this.messageListener = new MyMessageListener(context);
            getMessageClient(context).addListener(this.messageListener);
        }
    }

    private static final String MESSAGE_PATH = "/cordova/plugin/wearos";

    public void sendMsgToAllNodes(View view) {
        (new Thread(new MessageRunnable())).start();
    }
    private class MessageRunnable implements Runnable {

        @Override
        public void run() {
            Task<List<Node>> nodesTask = getNodeClient(MainActivity.this)
                    .getConnectedNodes();
            nodesTask.addOnSuccessListener(new OnSuccessListener<List<Node>>() {

                @Override
                public void onSuccess(List<Node> nodes) {
                    for (final Node node : nodes) {
                        Task<Integer> sendMessageTask =
                                getMessageClient(MainActivity.this).sendMessage(node.getId(), MESSAGE_PATH, message.getBytes());

                        sendMessageTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                            @Override
                            public void onSuccess(Integer aVoid) {
                                Log.d("Wear", "Message sent to : " + node.getId());
                            }
                        });
                        sendMessageTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception aVoid) {
                                Log.d("Wear", "Message send failed");
                            }
                        });

                    }
                }
            });
        }
    }

    protected void sendMessage(final String msg) {
        Log.i(TAG, "sendMessage: " + msg);
        NodeClient nodeClient = getNodeClient(context);
        Task<List<Node>> connectedNodes = nodeClient.getConnectedNodes();
        connectedNodes.addOnCompleteListener(new OnCompleteListener<List<Node>>() {
            @Override
            public void onComplete(@NonNull Task<List<Node>> task) {
                List<Node> nodes = task.getResult();
                for(Node node : nodes) {
                    sendMessageToNode(node.getId(), msg);
                }
            }
        });
    }

    protected void sendMessageToNode(String nodeId, String msg) {
        Log.i(TAG, "sendMessageToNode");
        byte[] message = msg.getBytes();
        getMessageClient(context).sendMessage(nodeId, MESSAGE_PATH, message);
    }
}
