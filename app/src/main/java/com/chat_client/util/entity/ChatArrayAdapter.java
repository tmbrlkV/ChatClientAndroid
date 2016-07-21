package com.chat_client.util.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chat_client.R;
import com.chat_client.util.entity.ChatMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {
    protected TextView chatText;
    protected TextView timeView;
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(ChatMessage message) {
        chatMessageList.add(message);
        super.add(message);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public ChatMessage getItem(int index) {
        return chatMessageList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessage.getLayout()) {
            row = inflater.inflate(R.layout.right_message_layout, parent, false);
        } else {
            row = inflater.inflate(R.layout.left_message_layout, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.message_row);
        chatText.setText(chatMessage.getMessage());

        timeView = (TextView) row.findViewById(R.id.message_time);
        timeView.setText(DateFormat.getTimeInstance().format(chatMessage.getTime()));

        return row;
    }
}
