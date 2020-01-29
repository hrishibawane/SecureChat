package com.example.securechat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    FirebaseUser curr = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
        this.messages = messages;
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.getSender().equals(curr.getUid())) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.my_message_body);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.my_message_time);
            holder.readReceipt = (ImageView) convertView.findViewById(R.id.my_message_status);
            convertView.setTag(holder);

            if(message.getTime().equals("null")) {
                holder.messageBody.setTypeface(null, Typeface.ITALIC);
                holder.messageBody.setText(message.getMessage());
            }
            else {
                holder.messageBody.setTypeface(null, Typeface.NORMAL);
                holder.messageBody.setText(message.getMessage());
                holder.timeStamp.setText(message.getTime());
            }

            if (message.getStatus()) {
                holder.readReceipt.setImageResource(R.drawable.baseline_done_all_black_18dp);
            }
            else {
                holder.readReceipt.setImageResource(R.drawable.baseline_done_black_18dp);
            }
        }
        else {
//            messages.get(i).setStatus(true);
            convertView = messageInflater.inflate(R.layout.others_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.others_message_body);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.others_message_time);
            convertView.setTag(holder);

            if(message.getTime().equals("null")) {
                holder.messageBody.setTypeface(null, Typeface.ITALIC);
                holder.messageBody.setText(message.getMessage());
            }
            else {
                holder.messageBody.setTypeface(null, Typeface.NORMAL);
                holder.messageBody.setText(message.getMessage());
                holder.timeStamp.setText(message.getTime());
            }
        }
        return convertView;
    }

}

class MessageViewHolder {
    public TextView messageBody;
    public TextView timeStamp;
    public ImageView readReceipt;
}
