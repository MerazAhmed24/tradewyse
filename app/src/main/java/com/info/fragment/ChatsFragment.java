package com.info.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.info.commons.TradWyseSession;
import com.info.interfaces.RefreshChannelsListListner;
import com.info.tradewyse.R;

// Test comment
public class ChatsFragment extends BaseFragment implements RefreshChannelsListListner {
    private static final String ARG_IsFromLoggedInProfile = "IsFromLoggedInProfile";
    private boolean fromLoggedInProfile = false;
    private static final String TAG = "ChatsFragment";

    TradWyseSession tradWyseSession;
    TextView tvInvitationTab;

    public static ChatsFragment newInstance(boolean fromLoggedInProfile) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IsFromLoggedInProfile, fromLoggedInProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromLoggedInProfile = getArguments().getBoolean(ARG_IsFromLoggedInProfile);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Fragment", "classes fragment");
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tradWyseSession = TradWyseSession.getSharedInstance(getActivity());
        tvInvitationTab = view.findViewById(R.id.tv_invitation);
        if (fromLoggedInProfile) {
            (view.findViewById(R.id.parentLL)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
        } else {
            (view.findViewById(R.id.parentLL)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
        }
        showPublicChatFragment();
        //(view.findViewById(R.id.ll_btn)).setVisibility(View.GONE);
        (view.findViewById(R.id.tv_public_chat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPublicChatClicked(view);
            }
        });
        (view.findViewById(R.id.tv_your_rooms)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvYourRoomClicked(view);
            }
        });
        (view.findViewById(R.id.tv_invitation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInvitationClicked(view);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void tvPublicChatClicked(View view) {
        ((TextView) view.findViewById(R.id.tv_public_chat)).setBackground(getResources().getDrawable(R.drawable.bg_fill_white_round_border));
        ((TextView) view.findViewById(R.id.tv_public_chat)).setTextColor(getResources().getColor(R.color.color_app_dark_bg));

        ((TextView) view.findViewById(R.id.tv_your_rooms)).setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        ((TextView) view.findViewById(R.id.tv_your_rooms)).setTextColor(getResources().getColor(R.color.text_color_white));

        ((TextView) view.findViewById(R.id.tv_invitation)).setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        ((TextView) view.findViewById(R.id.tv_invitation)).setTextColor(getResources().getColor(R.color.text_color_white));
        showPublicChatFragment();
    }

    private void tvYourRoomClicked(View view) {
        ((TextView) view.findViewById(R.id.tv_public_chat)).setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        ((TextView) view.findViewById(R.id.tv_public_chat)).setTextColor(getResources().getColor(R.color.text_color_white));

        ((TextView) view.findViewById(R.id.tv_your_rooms)).setBackground(getResources().getDrawable(R.drawable.bg_fill_white_round_border));
        ((TextView) view.findViewById(R.id.tv_your_rooms)).setTextColor(getResources().getColor(R.color.color_app_dark_bg));

        ((TextView) view.findViewById(R.id.tv_invitation)).setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        ((TextView) view.findViewById(R.id.tv_invitation)).setTextColor(getResources().getColor(R.color.text_color_white));
        showYourRoomFragment();
    }

    private void tvInvitationClicked(View view) {
        ((TextView) view.findViewById(R.id.tv_public_chat)).setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        ((TextView) view.findViewById(R.id.tv_public_chat)).setTextColor(getResources().getColor(R.color.text_color_white));

        ((TextView) view.findViewById(R.id.tv_your_rooms)).setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        ((TextView) view.findViewById(R.id.tv_your_rooms)).setTextColor(getResources().getColor(R.color.text_color_white));

        ((TextView) view.findViewById(R.id.tv_invitation)).setBackground(getResources().getDrawable(R.drawable.bg_fill_white_round_border));
        ((TextView) view.findViewById(R.id.tv_invitation)).setTextColor(getResources().getColor(R.color.color_app_dark_bg));
        showInivationFragment();
    }

    private void showInivationFragment() {
        getChildFragmentManager().beginTransaction()
                .add(R.id.frag_chat_container, InvitationFragment.newInstance(this), InvitationFragment.TAG)
                .commit();
    }

    private void showYourRoomFragment() {
        getChildFragmentManager().beginTransaction()
                .add(R.id.frag_chat_container, YourRoomFragment.newInstance(fromLoggedInProfile), YourRoomFragment.TAG)
                .commit();
    }


    public void showPublicChatFragment() {
        getChildFragmentManager().beginTransaction()
                .add(R.id.frag_chat_container, PublicChatFragment.newInstance(fromLoggedInProfile), PublicChatFragment.TAG)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Onactivityresult", "onactivity result classes fragment");
    }

    @Override
    public void refreshChannelList(int channelCount) {
        if (channelCount > 0)
            tvInvitationTab.setText(getActivity().getResources().getString(R.string.invitations) + "(" + channelCount + ")");
        else
            tvInvitationTab.setText(getActivity().getResources().getString(R.string.invitations));
    }

    @Override
    public void showDeleteChannelOption(/*ChannelController channelController, Channel channel*/) {

    }
}
