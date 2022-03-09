package com.info.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.InvitationRoomAdapter;
import com.info.commons.Common;
import com.info.commons.TradWyseSession;
import com.info.interfaces.RefreshChannelsListListner;
import com.info.logger.Logger;
import com.info.tradewyse.CreateRoomActivity;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;

/**
 * Created by Amit Gupta on 23,June,2020
 */
public class InvitationFragment extends BaseFragment implements RefreshChannelsListListner {
    public static final String TAG = "InviatationFragment";
    RelativeLayout defaultViewForFirstTimeUser;
    ProgressBar progress;
    RelativeLayout rlWhenDataAvailable;
    private RecyclerView recList;
    TradWyseSession tradWyseSession;
    static ChatsFragment chatsFragmentObj;

    public static InvitationFragment newInstance(ChatsFragment chatsFragment) {
        chatsFragmentObj = chatsFragment;
        return new InvitationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (((ProfileTabbedActivity) getActivity()).fromLoggedInProfile) {
            (view.findViewById(R.id.chat_frag_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
            (view.findViewById(R.id.recYourRoom)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
        } else {
            (view.findViewById(R.id.chat_frag_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
            (view.findViewById(R.id.recYourRoom)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
        }
        getIds(view);

        tradWyseSession = TradWyseSession.getSharedInstance(getActivity());
        ((TextView) view.findViewById(R.id.create_room_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRoomActivity.starCreateRoomActivity(getActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getInvitationList(true);
    }

    private void getInvitationList(boolean showLoader) {
        if (showLoader)
            showProgress();
            hideProgress();
    }

    private void getIds(View view) {
        defaultViewForFirstTimeUser = view.findViewById(R.id.default_view_for_first_time_user);
        rlWhenDataAvailable = view.findViewById(R.id.rl_data_available);
        progress = view.findViewById(R.id.progress);
        recList = view.findViewById(R.id.recYourRoom);
        (view.findViewById(R.id.create_room_tv)).setVisibility(View.GONE);
    }

    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
        Common.disableInteraction((AppCompatActivity) getActivity());
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
        Common.enableInteraction((AppCompatActivity) getActivity());
    }

    @Override
    public void refreshChannelList(int channelCount) {
        getInvitationList(false);
    }

    @Override
    public void showDeleteChannelOption(/*ChannelController channelController, Channel channel*/) {

    }
}
