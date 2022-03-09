package com.info.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.ChatUserListAdapter;
import com.info.model.User;
import com.info.tradewyse.CreateRoomActivity;
import com.info.tradewyse.R;

import java.util.ArrayList;


public class CreateRoomFragmentStep2 extends BaseFragment {
    RecyclerView recyclerView;
    LinearLayout rowParentRl;
    ImageView ivAccept;
    TextView tvCreateChannel;
    AppCompatEditText edtSearch;
    public static final String TAG = "CreateRoomFragmentStep2";

    public CreateRoomFragmentStep2() {
    }

    public static CreateRoomFragmentStep2 newInstance() {
        CreateRoomFragmentStep2 fragment = new CreateRoomFragmentStep2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Fragment", "classes fragment");
        return inflater.inflate(R.layout.frag_create_room_step2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getIds(view);
        rowParentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivAccept.getVisibility() == View.VISIBLE) {
                    ivAccept.setVisibility(View.GONE);
                } else {
                    ivAccept.setVisibility(View.VISIBLE);
                }
            }
        });
        loadUsers("");

        tvCreateChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 1) {
                    loadUsers(s.toString());
                }else if(s.toString().isEmpty()){
                    loadUsers("");
                }
            }
        });

        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        edtSearch.setText("");
                        loadUsers("");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void loadUsers(String searchString) {

    }

    private void setDatainRv(ArrayList<User> users) {
        ChatUserListAdapter mAdapter = new ChatUserListAdapter(users, getActivity(), this, (CreateRoomActivity) getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void getIds(@NonNull View view) {
        recyclerView = view.findViewById(R.id.user_list_rv);
        rowParentRl = view.findViewById(R.id.row_parent_rl);
        ivAccept = view.findViewById(R.id.iv_accept);
        tvCreateChannel = view.findViewById(R.id.create_room_tv);
        edtSearch = view.findViewById(R.id.edt_search);
    }
}
