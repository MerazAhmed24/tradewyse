package com.info.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.info.CustomToast.Toasty;
import com.info.tradewyse.R;


public class BaseFragment extends Fragment {
    ProgressDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void showProgressDialog(){
        showProgressDialog("Loading.....");
    }

    public void showProgressDialog(String message){
        try{
            if(!getActivity().isFinishing()){
                progressDialog = new ProgressDialog(getActivity(),R.style.style_progress_dialog);
                progressDialog.setMessage(message);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }
        }catch (Exception e){

        }
    }

    public void dismissProgressDialog(){
        try{
            if(!getActivity().isFinishing()&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }catch (Exception e){

        }

    }

    public void showToast(String msg) {
        Toasty.normal(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
