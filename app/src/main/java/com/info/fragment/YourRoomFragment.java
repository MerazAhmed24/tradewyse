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

import com.info.adapter.YourRoomAdapter;
import com.info.commons.Common;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.interfaces.RefreshChannelsListListner;
import com.info.logger.Logger;
import com.info.tradewyse.CreateRoomActivity;
import com.info.tradewyse.R;

/**
 * Created by Amit Gupta on 23,June,2020
 */
public class YourRoomFragment extends BaseFragment implements RefreshChannelsListListner {
    private static final String ARG_IsFromLoggedInProfile = "IsFromLoggedInProfile";
    public static final String TAG = "YourRoomFragment";
    RelativeLayout defaultViewForFirstTimeUser;
    ProgressBar progress;
    RelativeLayout rlWhenDataAvailable;
    private RecyclerView recList;
    TradWyseSession tradWyseSession;
    TextView tvCreateRoom;
    boolean isMentor = false;
    private boolean fromLoggedInProfile = false;

    public static YourRoomFragment newInstance(boolean fromLoggedInProfile) {
        YourRoomFragment fragment = new YourRoomFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IsFromLoggedInProfile, fromLoggedInProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromLoggedInProfile = getArguments().getBoolean(ARG_IsFromLoggedInProfile);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (fromLoggedInProfile) {
            (view.findViewById(R.id.chat_frag_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
            (view.findViewById(R.id.recYourRoom)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
        } else {
            (view.findViewById(R.id.chat_frag_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
            (view.findViewById(R.id.recYourRoom)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
        }

        tradWyseSession = TradWyseSession.getSharedInstance(getActivity());
        String isMentorString = tradWyseSession.getIsMentor();
        if (!StringHelper.isEmpty(isMentorString) && isMentorString.equalsIgnoreCase("true")) {
            isMentor = true;
        }
        getIds(view);

        tvCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRoomActivity.starCreateRoomActivity(getActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.debug(TAG, "onResumeMethodCalled");
//        getChanelList();
    }

    /*private void getChanelList() {
        showProgress();
        Logger.debug(TAG, "Chat User Id:- " + TradwyseApplication.getChatClient().getCurrentUser().getId());
        ChannelListViewModel chanelListViewModel = new ViewModelProvider(this).get(ChannelListViewModel.class);

        *//*FilterObject filter = Filters.INSTANCE.or(Filters.INSTANCE.and(Filters.INSTANCE.ne("type", Constants.MENTOR_SELECTED_ROOM), Filters.INSTANCE.eq("invites", "accepted")), Filters.INSTANCE.eq("created_by_id", TradwyseApplication.getChatClient().getCurrentUser().getId()));*//*

     *//* FilterObject filter = Filters.INSTANCE.and(Filters.INSTANCE.ne("type", Constants.MENTOR_SELECTED_ROOM), Filters.INSTANCE.eq("invites", "accepted"));*//*

        FilterObject filter = Filters.INSTANCE.or(Filters.INSTANCE.eq("type", Constants.MENTOR_SELECTED_ROOM), Filters.INSTANCE.eq("type", Constants.MENTOR_ALL_ROOM));

        QuerySort sort = new QuerySort().desc("created_at");
        chanelListViewModel.setQuery(filter, sort);

        chanelListViewModel.getChannels().observe(getViewLifecycleOwner(), channels -> {
            List<Channel> channelList = new ArrayList<>();
            String currentUserId = TradwyseApplication.getChatClient().getCurrentUser().getId();
            Logger.debug(TAG,"Current user Stream Id:- "+currentUserId);
            for (int i = 0; i < channels.size(); i++) {
                List<Member> memberList = channels.get(i).getMembers();
                for (int j = 0; j < memberList.size(); j++) {
                    if ((channels.get(i).getType().equalsIgnoreCase(Constants.MENTOR_SELECTED_ROOM) || channels.get(i).getType().equalsIgnoreCase(Constants.MENTOR_ALL_ROOM)) && (memberList.get(j).getUser().getId().equals(currentUserId) && memberList.get(j).getRole().equalsIgnoreCase("owner"))) {
                        channelList.add(channels.get(i));
                    } else if (memberList.get(j).getUserId().equals(currentUserId)) {
                        if (memberList.get(j).getInviteAcceptedAt() != null) {
                            channelList.add(channels.get(i));
                        }
                    }
                }
            }
            hideProgress();
            setDataInRv(channelList);
            Logger.debug(TAG, "Channels list updated. Current size is " + channels.size());
        });
    }*/

    private void getIds(View view) {
        defaultViewForFirstTimeUser = view.findViewById(R.id.default_view_for_first_time_user);
        rlWhenDataAvailable = view.findViewById(R.id.rl_data_available);
        progress = view.findViewById(R.id.progress);
        recList = view.findViewById(R.id.recYourRoom);
        tvCreateRoom = view.findViewById(R.id.create_room_tv);
        defaultViewForFirstTimeUser.setVisibility(View.VISIBLE);
        rlWhenDataAvailable.setVisibility(View.GONE);

        if (isMentor)
            ((TextView) view.findViewById(R.id.create_room_tv)).setVisibility(View.VISIBLE);
        else
            ((TextView) view.findViewById(R.id.create_room_tv)).setVisibility(View.GONE);

    }

    private void setDataInRv(/*List<Channel> channels*/) {
        /*if (channels.isEmpty()) {
            defaultViewForFirstTimeUser.setVisibility(View.VISIBLE);
            rlWhenDataAvailable.setVisibility(View.GONE);
        } else {
            rlWhenDataAvailable.setVisibility(View.VISIBLE);
            defaultViewForFirstTimeUser.setVisibility(View.GONE);
        }*/

        YourRoomAdapter mAdapter = new YourRoomAdapter(/*channels, */getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recList.setLayoutManager(mLayoutManager);
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(mAdapter);
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
    public void refreshChannelList(int chennelCount) {
        //getChanelList();
    }

    @Override
    public void showDeleteChannelOption(/*ChannelController channelController, Channel channel*/) {
//        showDeleteRoom(channelController, channel);
    }

    /*private void showDeleteRoom(ChannelController channelController, Channel channel) {
        try {
            BottomSheetDialog moreOptionsDialog = new BottomSheetDialog(getActivity());
            View sheetView = getLayoutInflater().inflate(R.layout.delete_room_popup, null);
            TextView tvRoomName = sheetView.findViewById(R.id.tvRoomName);
            TextView tvRequestDelete = sheetView.findViewById(R.id.tvRequestDelete);
            TextView tvCancel = sheetView.findViewById(R.id.tvCancel);
            moreOptionsDialog.setContentView(sheetView);
            tvRoomName.setText(channel.getId());
            tvRequestDelete.setOnClickListener(view -> {
                deleteRoom(channelController, channel);
                moreOptionsDialog.cancel();
            });
            tvCancel.setOnClickListener(view -> {
                moreOptionsDialog.cancel();
            });
            moreOptionsDialog.show();

        } catch (Exception e) {
        }
    }

    private void deleteRoom(ChannelController channelController, Channel channel) {
        channelController.delete().enqueue(result -> {
            if (result.isSuccess()) {
                Toast.makeText(getActivity(), channel.getId() + " deleted successfully....", Toast.LENGTH_SHORT).show();
                //Channel channel = result.data();
                refreshChannelList(0);
            } else {
                ChatError error = result.error();
                error.printStackTrace();
            }
            return Unit.INSTANCE;
        });
    }*/
}
