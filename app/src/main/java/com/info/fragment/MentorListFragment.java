package com.info.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.info.tradewyse.R;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link MentorListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MentorListFragment extends Fragment {


    public MentorListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MentorListFragment.
     */
    public static MentorListFragment newInstance(String param1, String param2) {
        MentorListFragment fragment = new MentorListFragment();
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
        return inflater.inflate(R.layout.fragment_mentor_list, container, false);
    }

}
