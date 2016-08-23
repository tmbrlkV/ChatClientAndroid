package com.chat_client.util.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chat_client.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatLayoutMessage> {
    protected TextView chatText;
    protected TextView timeView;
    private List<ChatLayoutMessage> chatLayoutMessageList = new ArrayList<>();

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(ChatLayoutMessage message) {
        chatLayoutMessageList.add(message);
        super.add(message);
    }

    @Override
    public int getCount() {
        return chatLayoutMessageList.size();
    }

    @Override
    public ChatLayoutMessage getItem(int index) {
        return chatLayoutMessageList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatLayoutMessage chatLayoutMessage = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatLayoutMessage.getLayout()) {
            row = inflater.inflate(R.layout.right_message_layout, parent, false);
        } else {
            row = inflater.inflate(R.layout.left_message_layout, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.message_row);
        chatText.setText(chatLayoutMessage.getMessage());

        timeView = (TextView) row.findViewById(R.id.message_time);
        timeView.setText(DateFormat.getTimeInstance().format(chatLayoutMessage.getTime()));

        return row;
    }
}
