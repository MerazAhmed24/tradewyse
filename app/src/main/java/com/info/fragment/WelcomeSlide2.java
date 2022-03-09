package com.info.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.info.commons.Common;
import com.info.tradewyse.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeSlide2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeSlide2 extends Fragment {
    View view_transparent;
    TextView txtTitle;
    TextView txtDescriptionTour;
    public LottieAnimationView animationView;
    public WelcomeSlide2() {
        // Required empty public constructor
    }

    public static WelcomeSlide2 newInstance() {
        WelcomeSlide2 fragment = new WelcomeSlide2();
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
        return inflater.inflate(R.layout.welcome_slide2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view_transparent = view.findViewById(R.id.view_transparent);
        txtTitle = view.findViewById(R.id.titleTour);
        txtDescriptionTour = view.findViewById(R.id.descriptionTour);
        animationView = (LottieAnimationView)view.findViewById(R.id.animation_view);
        //Log.d("animation", "onViewCreated: 2");
        animationView.setAnimation("second.json");
        animationView.playAnimation();
        animationView.loop(true);
        //txtTitle.setTypeface(Common.getTypeface(getContext(), 1));
        //txtDescriptionTour.setTypeface(Common.getTypeface(getContext(), 1));
        setAnimation();
    }

    public void setLottieAnimation(){
        animationView.setAnimation("second.json");
        animationView.setProgress(0);
        animationView.pauseAnimation();
        animationView.playAnimation();
        animationView.loop(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        //Log.e("animation","onresume 2");
        setAnimation();
    }

    public void setAnimation() {
        Log.e("Refresh","refresh2");
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(500);
       // txtDescriptionTour.startAnimation(fadeOut);

        Animation fadeBackground = new AlphaAnimation(0, 1);
        fadeBackground.setInterpolator(new AccelerateInterpolator()); //and this
        fadeBackground.setStartOffset(700);
        fadeBackground.setDuration(1000);
     //   view_transparent.startAnimation(fadeBackground);
    }
}
