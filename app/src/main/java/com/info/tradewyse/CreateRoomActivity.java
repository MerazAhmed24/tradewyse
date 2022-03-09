package com.info.tradewyse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.info.commons.Constants;
import com.info.fragment.CreateRoomFragmentStep1;
import com.info.fragment.CreateRoomFragmentStep2;

import java.util.ArrayList;

public class CreateRoomActivity extends BaseActivity{

    private String channelName;
    private String channelType = Constants.MENTOR_SELECTED_ROOM;
    CreateRoomFragmentStep1 createRoomFragmentStep1;
    CreateRoomFragmentStep2 createRoomFragmentStep2;
    ArrayList<String> members = new ArrayList<>();
    public String roomImageUrl = null;


    public static void starCreateRoomActivity(Context context) {
        Intent intent = new Intent(context, CreateRoomActivity.class);
        ((Activity) context).startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        setToolBar("Create Room");
        getSupportActionBar().setElevation(0f);

        displayRoom1Frag();

        //loadUrl();
    }



    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    private void displayRoom1Frag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        createRoomFragmentStep1 = CreateRoomFragmentStep1.newInstance(this);
        fragmentManager.beginTransaction()
                .replace(R.id.create_room_frag_container, createRoomFragmentStep1)
                .commit();
    }

    public void showCreateRoomStep2Frag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        createRoomFragmentStep2 = CreateRoomFragmentStep2.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.create_room_frag_container, createRoomFragmentStep2, CreateRoomFragmentStep2.TAG)
                .addToBackStack(CreateRoomFragmentStep1.TAG)
                .commit();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(createRoomFragmentStep1!=null)
            createRoomFragmentStep1.imageResult(requestCode,resultCode,data);
    }

    public interface onActivityResultCalled{
        public void imageResult(int requestCode, int resultCode, @Nullable Intent data);
    }
}