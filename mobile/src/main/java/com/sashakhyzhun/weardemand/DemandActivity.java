package com.sashakhyzhun.weardemand;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DemandActivity extends AppCompatActivity {

    public static final String ACTION_DEMAND = "com.androidweardocs.ACTION_DEMAND";
    public static final String EXTRA_MESSAGE = "com.androidweardocs.EXTRA_MESSAGE";
    public static final String EXTRA_VOICE_REPLY = "com.androidweardocs.EXTRA_VOICE_REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create an Intent for the demand
        Intent demandIntent = new Intent(this, DemandIntentReceiver.class)
                .putExtra(EXTRA_MESSAGE, "Reply icon selected.")
                .setAction(ACTION_DEMAND);

        // Create a pending intent using the local broadcast receiver
        PendingIntent demandPendingIntent =
                PendingIntent.getBroadcast(this, 0, demandIntent, 0);

        // Create RemoteInput object for a voice reply (demand)
        String replyLabel = getResources().getString(R.string.app_name);
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .build();

        // Create a wearable action
        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send,
                        getString(R.string.reply_label), demandPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Create a wearable extender for a notification
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .addAction(replyAction);

        // Create a notification and extend it for the wearable
        Notification notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Hello Wearable!")
                        .setContentText("First wearable demand.")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .extend(wearableExtender)
                        .build();
        // Get the notification manager
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Dispatch the extended notification
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);


        // Register the local broadcast receiver for the users demand.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }


    // Class to receive demand text from the wearable demand receiver
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Display the received demand
            TextView demandView = (TextView) findViewById(R.id.demand_text);
            String demand = demandView.getText() + intent.getStringExtra("reply");
            demandView.setText(demand);
        }
    }

}