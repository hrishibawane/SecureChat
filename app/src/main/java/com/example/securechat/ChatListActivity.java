package com.example.securechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    public ListView listView;
    public TextView textViewList;
    public TextView textViewHidden;
    public Toolbar toolbarChatlist;

    public List<String> listItems;
    public List<String> userIds;
    public List<String> chats;
    public String lastTime = "";

    private FirebaseUser mUser;
    private static final String TAG = "ChatListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        listItems = new ArrayList<String>();
        userIds = new ArrayList<String>();
        chats = new ArrayList<String>();

        listView = findViewById(R.id.listView);
        textViewList = findViewById(R.id.textViewList);
        textViewHidden = findViewById(R.id.textViewHidden);
        toolbarChatlist = findViewById(R.id.toolbarChatlist);

        toolbarChatlist.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);

        Log.i(TAG, mUser.getUid());

        setSupportActionBar(toolbarChatlist);
//        getSupportActionBar().setTitle("ChatList");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listItems.clear();
                userIds.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    final String name = ds.child("username").getValue().toString();
                    final String uid = ds.getKey();
                    String mob_no = "";

                    if(uid.equals(mUser.getUid())) {
                        continue;
                    }

                    if(ds.child("mobile_no").getValue() != null) {
                        mob_no = ds.child("mobile_no").getValue().toString();
                    }

                    if (mob_no.length()>0 && (contactExists(ChatListActivity.this, mob_no) || contactExists(ChatListActivity.this, "+91"+mob_no))) {
                        Log.i(TAG, name + ": Present");
                        listItems.add(name);
                        userIds.add(uid);
                    }


//                    Log.i(TAG, name + ": " + lastMsgTime);

                }
                ArrayAdapter<String> mHistory = new ArrayAdapter<String>(ChatListActivity.this, R.layout.listchats, R.id.textViewList, listItems);
                listView.setAdapter(mHistory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String value = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(ChatListActivity.this, value, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ChatListActivity.this, ChatMessages.class);
                intent.putExtra("userid", userIds.get(i));
                intent.putExtra("username", listItems.get(i));
                startActivity(intent);

            }
        });
    }


    private boolean contactExists(Context context, String mob_no) {

        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(mob_no));

        String[] mPhoneProj = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneProj, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }

        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuProfile: {
                return true;
            }
            case R.id.menuLogout: {
                logoutUser();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ChatListActivity.this, LoginActivity.class));
        finishAffinity();

    }

}
