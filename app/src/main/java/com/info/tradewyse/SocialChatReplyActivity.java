package com.info.tradewyse;

import static com.info.commons.Constants.MessageProfileImageUrl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.Distribution;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.info.adapter.SocialChatCommentAdapter;
import com.info.adapter.SocialMessageAdapter;
import com.info.commons.Constants;
import com.info.logger.Logger;
import com.info.model.ChatModel;
import com.info.model.SocialChatReply;
import com.info.model.SocialChatStocksDetails;
import com.info.model.SocialComment;
import com.info.model.SocialMessageDetails;
import com.info.sendbird.utils.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SocialChatReplyActivity extends BaseActivity {

    private ProgressBar commentLoader;
    private TextView tvCountComment;
    private RecyclerView recycleViewComment;
    private SocialChatCommentAdapter socialChatCommentAdapter;
    private SocialMessageDetails chatModel;
    TextView tvroom;
    ImageView menuTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_chat_reply);
        setToolBarTitle("Comments");
        tvroom = findViewById(R.id.tvRoom);
        tvroom.setVisibility(View.GONE);
        menuTab = findViewById(R.id.menuTab);
        menuTab.setVisibility(View.GONE);
        if (getIntent() != null) {
            chatModel = (SocialMessageDetails) getIntent().getSerializableExtra("chatModel");
        }

        findViewById(R.id.backAction).setOnClickListener(v -> super.onBackPressed());

        commentLoader = findViewById(R.id.commentLoader);
        tvCountComment = findViewById(R.id.tvCountComment);
        recycleViewComment = findViewById(R.id.recycleViewComment);
        tvCountComment.setText(chatModel.getComment().size() == 1 ? "Comments " + chatModel.getComment().size() :
                "Comments " + chatModel.getComment().size());
        setDataToAdapter();
    }

    private void setToolBarTitle(String title) {
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
    }

    private void setDataToAdapter() {
        socialChatCommentAdapter = new SocialChatCommentAdapter(SocialChatReplyActivity.this, chatModel.getComment());
        RecyclerView.LayoutManager messageLayoutManager = new LinearLayoutManager(this);
        recycleViewComment.setLayoutManager(messageLayoutManager);
        recycleViewComment.setAdapter(socialChatCommentAdapter);
    }
}