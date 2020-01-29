package com.example.securechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.Channel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatMessages extends AppCompatActivity implements View.OnClickListener {

    public EditText editTextMsg;
    public ImageButton buttonSend;
    public ListView messages_view;
    public Toolbar toolbarChatMessages;

    private FirebaseUser sender;
    private static final String TAG = "ChatMessagesActivity";

    public String key = "null";
    public List<Message> msgs;
    private String recipient;
    private String recipientName;
    MessageAdapter messageAdapter;

//    public static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        editTextMsg = findViewById(R.id.editTextMsg);
        buttonSend = findViewById(R.id.buttonSend);
        messages_view = findViewById(R.id.messages_view);
        toolbarChatMessages = findViewById(R.id.toolbarChatMessages);

        msgs = new ArrayList<Message>();
        recipient = getIntent().getExtras().get("userid").toString();
        recipientName = getIntent().getExtras().get("username").toString();
        sender = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbarChatMessages);
        getSupportActionBar().setTitle(recipientName);

        Log.i(TAG, "sender: " + sender.getUid() + " recipent: " + recipient);

        final DatabaseReference mChatsDatabase = FirebaseDatabase.getInstance().getReference("chatMessages");

        buttonSend.setOnClickListener(this);

        messages_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(ChatMessages.this, msgs.get(i).getMessage(), Toast.LENGTH_SHORT).show();

                if (msgs.get(i).getSender().equals(sender.getUid())) {
                    msgs.get(i).setMessage("This message was deleted");
                    msgs.get(i).setTime("null");
                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference("chatMessages");

                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(sender.getUid()+recipient)) {
                                db.child(sender.getUid()+recipient).setValue(msgs);
                            }
                            else {
                                db.child(recipient+sender.getUid()).setValue(msgs);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                }

                return false;
            }
        });


        mChatsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key1 = sender.getUid() + recipient;
                String key2 = recipient + sender.getUid();
                msgs.clear();

                if(dataSnapshot.child(key1).getChildrenCount() > 0) {

                    for(DataSnapshot ds: dataSnapshot.child(key1).getChildren()) {
                        Message m = ds.getValue(Message.class);
                        msgs.add(m);
                    }
                    key = key1;
                }
                else if (dataSnapshot.child(key2).getChildrenCount() > 0) {

                    for(DataSnapshot ds: dataSnapshot.child(key2).getChildren()) {
                        Message m = ds.getValue(Message.class);
                        msgs.add(m);
                    }
                    key = key2;
                }
                else {
                    key = "null";
                }
                for (Message m : msgs) {
                    if (!m.getSender().equals(sender.getUid())) {
                        m.setStatus(true);
                    }
                }

                messageAdapter = new MessageAdapter(ChatMessages.this, 0, msgs);
                messages_view.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSend: {
                sendMessage();
                break;
            }
        }
    }


    private void sendMessage() {

        DatabaseReference dbChats = FirebaseDatabase.getInstance().getReference("chatMessages");

        String new_msg = editTextMsg.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh:mm aa");
        final Date date = new Date();
        final String timeStamp = dateFormat.format(date);
        final String sender_id = sender.getUid();

        Message msg = new Message(sender_id, new_msg, timeStamp, false);
        String nkey = key.equals("null") ? sender.getUid()+recipient : key;

        msgs.add(msg);

        final int len = msgs.size();


        dbChats.child(nkey).setValue(msgs)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editTextMsg.setText("");
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatMessages.this, "Message Sent", Toast.LENGTH_SHORT).show();

                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+recipient);
                            userRef.child("lastMessageTime/" + sender.getUid()).setValue(date.getTime());

                        }
                        else {
                            Toast.makeText(ChatMessages.this, "Error Sending Message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
