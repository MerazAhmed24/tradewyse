package com.info.tradewyse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.adapter.ChatRoomMemberAdapter;
import com.info.model.GroupMemberInfo;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomMemberActivity extends BaseActivity {

    ImageView backAction;
    RecyclerView rvGroupMembersDetails;
    ChatRoomMemberAdapter chatRoomMemberAdapter;
    private List<GroupMemberInfo> groupMemberInfoList = new ArrayList<>();
    TextView tvCountMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_member);

        rvGroupMembersDetails = findViewById(R.id.rvGroupMembersDetails);
        tvCountMembers = findViewById(R.id.tvCountMembers);
        backAction = findViewById(R.id.backAction);

        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent() != null) {
            groupMemberInfoList = getIntent().getParcelableArrayListExtra("memberList");
        }

        tvCountMembers.setText("Members " + groupMemberInfoList.size());
        chatRoomMemberAdapter = new ChatRoomMemberAdapter(this, groupMemberInfoList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvGroupMembersDetails.setLayoutManager(mLayoutManager);
        rvGroupMembersDetails.setAdapter(chatRoomMemberAdapter);
    }
}