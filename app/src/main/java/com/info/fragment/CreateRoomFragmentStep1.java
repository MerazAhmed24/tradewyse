package com.info.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.info.commons.Common;
import com.info.commons.FIleHelper;
import com.info.commons.PhotoUtility;
import com.info.commons.StringHelper;
import com.info.interfaces.PhotoOptionSelectListener;
import com.info.tradewyse.CreateRoomActivity;
import com.info.tradewyse.R;

import java.io.File;
import java.io.IOException;


public class CreateRoomFragmentStep1 extends BaseFragment implements CreateRoomActivity.onActivityResultCalled {
    public static final String TAG = "CreateRoomFragmentStep1";
    EditText edtChanelName;
    PhotoUtility photoUtility;
    SimpleDraweeView ivRoomPic;
    CreateRoomActivity createRoomActivity;
    ProgressBar progress;

    public CreateRoomFragmentStep1(CreateRoomActivity createRoomActivity) {
        this.createRoomActivity = createRoomActivity;
    }

    public static CreateRoomFragmentStep1 newInstance(CreateRoomActivity createRoomActivity) {
        CreateRoomFragmentStep1 fragment = new CreateRoomFragmentStep1(createRoomActivity);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_create_room_step1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getIds(view);
        (view.findViewById(R.id.next_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chanelName = edtChanelName.getText().toString().trim();
                if (chanelName == null || chanelName.isEmpty()) {
                    edtChanelName.setError("Enter Channel Name");
                } else {
                    ((CreateRoomActivity) getActivity()).setChannelName(chanelName);
                    ((CreateRoomActivity) getActivity()).showCreateRoomStep2Frag();
                }
            }
        });

        ivRoomPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void getIds(View view) {
        edtChanelName = view.findViewById(R.id.edtChanelName);
        ivRoomPic = view.findViewById(R.id.iv_room_pic);
        progress = view.findViewById(R.id.progress);
    }

    private void takePhoto() {
        try {
            photoUtility = new PhotoUtility.Builder(createRoomActivity)
                    .setImageView(ivRoomPic)
                    .setOutPutFile(FIleHelper.createNewFile(getActivity(), FIleHelper.createFileName("RoomImage")))
                    .build();
            photoUtility.setPhotoOptionSelectListener(new PhotoOptionSelectListener() {
                @Override
                public void requestPermissions(@NonNull String[] permissions, int requestCode) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
                }

                @Override
                public void startActivityForResult(Intent intent, int requestCode) {
                    ActivityCompat.startActivityForResult(getActivity(), intent, requestCode, null);
                }
            });

            photoUtility.requestPermissionsCameraAndStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void imageResult(int requestCode, int resultCode, @Nullable Intent data) {
        photoUtility.setResult(requestCode, resultCode, data);

        String selectedFilePath = photoUtility.getSelectedFilePath();
        if (StringHelper.isEmpty(selectedFilePath)) return;
        File file = new File(selectedFilePath);
        if (file != null && file.exists()) {
            showProgress();
            //uploadImageFileToStreamImageBucket(file);
        }
    }


    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
        Common.disableInteraction((AppCompatActivity) getActivity());
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
        Common.enableInteraction((AppCompatActivity) getActivity());
    }
}
