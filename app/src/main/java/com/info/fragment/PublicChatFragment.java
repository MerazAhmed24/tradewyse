package com.info.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Constants;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.logger.Logger;
import com.info.model.FirestoreAuthentication;
import com.info.model.PublicChatModel;
import com.info.tradewyse.ChatActivity;
import com.info.tradewyse.CreateRoomActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.TradwyseApplication;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBirdException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amit Gupta on 23,June,2020
 */
public class PublicChatFragment extends BaseFragment {
    private static final String ARG_IsFromLoggedInProfile = "IsFromLoggedInProfile";
    public static final String TAG = "PublicChatFragment";
    private boolean fromLoggedInProfile = false;
    boolean isMentor = false;
    TextView tvText1, tvText2, tvText3, tvStartChatBtn, tvConnectingChatBtn;
    OpenChannel openChannel;

    String groupId, groupTitle, groupDesc, groupImage, socialGroupId = "";

    public static PublicChatFragment newInstance(boolean fromLoggedInProfile) {
        PublicChatFragment fragment = new PublicChatFragment();
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
        return inflater.inflate(R.layout.fragment_public_chat, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(boolean connectionEstablishWithFirebase) {
        if (connectionEstablishWithFirebase)
            checkIfPublicChatGroupIsAvailable();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(getActivity());
        String isMentorString = tradWyseSession.getIsMentor();
        if (!StringHelper.isEmpty(isMentorString) && isMentorString.equalsIgnoreCase("true")) {
            isMentor = true;
        }
        if (fromLoggedInProfile) {
            (view.findViewById(R.id.chat_frag_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_app_dark_bg));
        } else {
            (view.findViewById(R.id.chat_frag_parent_rl)).setBackgroundColor(getResources().getColor(R.color.color_other_profile_bg));
        }
        getIds(view);
        tvConnectingChatBtn.setVisibility(View.VISIBLE);
        tvStartChatBtn.setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.create_room_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRoomActivity.starCreateRoomActivity(getActivity());
            }
        });
        tvStartChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Chat room is undergoing maintenance. We will be back and running soon.", Toast.LENGTH_LONG).show();
                startPubliChat();

            }
        });
        checkIfPublicChatGroupIsAvailable();
    }


    private void checkIfPublicChatGroupIsAvailable() {
        if (TradwyseApplication.publicChatModel != null) {
            tvStartChatBtn.setVisibility(View.VISIBLE);
            tvConnectingChatBtn.setVisibility(View.GONE);
            groupId = TradwyseApplication.publicChatModel.getGroupId();
            groupImage = TradwyseApplication.publicChatModel.getGroupImage();
            groupTitle = TradwyseApplication.publicChatModel.getGroupTitle();
            groupDesc = TradwyseApplication.publicChatModel.getGroupDesc();
            socialGroupId = TradwyseApplication.socialChatModel.getGroupId();
            Log.d(TAG, groupId + " => " + "\ngroupImage:-" + groupImage + "\ngroupTitle:-" + groupTitle + "\ngroupDesc:-" + groupDesc);
        } else {
            tvStartChatBtn.setVisibility(View.GONE);
            tvConnectingChatBtn.setVisibility(View.VISIBLE);
            getFirestoreAuthToken();
        }

    }

    private void getOpenChanelObj() {
        OpenChannelListQuery mChannelListQuery;
        mChannelListQuery = OpenChannel.createOpenChannelListQuery();
        mChannelListQuery.setLimit(10);
        mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {

                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    Logger.debug(TAG, list.get(i).getName());
                    if (list.get(i).getName().equals(Constants.IS_PRODUCTION ? "Room One" : "PublicTest")) {
                        openChannel = list.get(i);
                        tvStartChatBtn.setVisibility(View.VISIBLE);
                        tvConnectingChatBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void getIds(View view) {
        //TODO: make the View.VISIBLE to View.GONE before publishing app
        if (isMentor)
            ((TextView) view.findViewById(R.id.create_room_tv)).setVisibility(View.GONE);
        else
            ((TextView) view.findViewById(R.id.create_room_tv)).setVisibility(View.GONE);
        tvText1 = view.findViewById(R.id.text1);
        tvText2 = view.findViewById(R.id.text2);
        tvText3 = view.findViewById(R.id.text3);
        tvStartChatBtn = ((TextView) view.findViewById(R.id.start_chat_tv));
        tvConnectingChatBtn = ((TextView) view.findViewById(R.id.connecting_chat_tv));
        setEmptyDataText();
    }

    private void startPubliChat() {
        if ((groupId != null && !groupId.isEmpty()) && (groupTitle != null && !groupTitle.isEmpty()))
            ChatActivity.starChatActivity(getActivity(), groupId, groupTitle, groupDesc, groupImage, false);
    }

    private void setEmptyDataText() {


        String s1 = getActivity().getResources().getString(R.string._1_we_believe_in_personal_rights_and_freedom);
        String s2 = getActivity().getResources().getString(R.string._2_this_space_is_for_sharing_winning_trades_and_investing_strategies);
        String s3 = getActivity().getResources().getString(R.string._3_the_app_was_designed_);
    }

    public void getFirestoreAuthToken() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<FirestoreAuthentication> call = apiInterface.getFirestoreAuth();
        call.enqueue(new Callback<FirestoreAuthentication>() {
            @Override
            public void onResponse(Call<FirestoreAuthentication> call, Response<FirestoreAuthentication> response) {
                if (response != null && response.body() != null) {
                    FirestoreAuthentication firestoreAuthentication = response.body();
                    ComplexPreferences mComplexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), Constants.FIRESTORE_AUTH_PREF, getActivity().MODE_PRIVATE);
                    mComplexPreferences.putObject(Constants.FIRESTORE_AUTH_PREF_OBJ, firestoreAuthentication);
                    mComplexPreferences.commit();
                    signInWithCustomToken(firestoreAuthentication.getAuthToken());
                } else {
                    Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: No response ");
                }
            }

            @Override
            public void onFailure(Call<FirestoreAuthentication> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: onFailure ", t.fillInStackTrace());
            }
        });
    }

    private void signInWithCustomToken(String FCMID) {
        FirebaseAuth.getInstance().signInWithCustomToken(FCMID).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfPublicChatGroupIsAvailableHere();
                } else {
                    Toast.makeText(getActivity(), "Unable to signInWithCustomToken", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkIfPublicChatGroupIsAvailableHere() {
        TradwyseApplication.getFirestoreDb().collection(Constants.OPEN_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            checkIfPublicChatGroupIsAvailable();
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            checkIfPublicChatGroupIsAvailable();
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
}
