package com.chat_client.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.chat_client.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LobbyActivity extends AppCompatActivity {
    protected String[] data = {"1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13", "14"};
    @BindView(R.id.gvMain)
    protected GridView rooms;
    @BindView(R.id.enterRoomButton)
    protected Button enterRoomButton;
    @BindView(R.id.createRoomButton)
    protected Button createRoomButton;
    protected ArrayAdapter<String> adapter;
    private View lastView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        ButterKnife.bind(this);
        adapter = new ArrayAdapter<>(this, R.layout.room_item, R.id.tvText, data);
        rooms.setAdapter(adapter);
        rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastView != null) {
                    lastView.setBackgroundColor(Color.GRAY);
                    if (lastView.equals(view)) {
                        lastView = null;
                        enterRoomButton.setVisibility(View.INVISIBLE);
                        createRoomButton.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                enterRoomButton.setVisibility(View.VISIBLE);
                view.setBackgroundColor(Color.DKGRAY);
                createRoomButton.setVisibility(View.INVISIBLE);
                lastView = view;

            }
        });
        adjustGridView();

    }

    private void adjustGridView() {
        rooms.setNumColumns(3);
        rooms.setColumnWidth(150);
        rooms.setVerticalSpacing(10);
        rooms.setHorizontalSpacing(5);
        rooms.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }
}
