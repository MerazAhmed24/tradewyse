package com.info.tradewyse;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.info.adapter.WelcomePagerAdapter;
import com.info.commons.TradWyseSession;
import com.info.fragment.WelcomeSlide1;
import com.info.fragment.WelcomeSlide2;
import com.info.fragment.WelcomeSlide3;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TourActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext, btclosetour;
    Context context;
    final List<Fragment> fragmentList = new ArrayList<>();

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 6000; // time in milliseconds between successive task executions.
    private long NUM_PAGES = 3;
    public static final String TAG = "Tour";
    public static final String TAG_FLY = "TourFLY";
    WelcomePagerAdapter welcomePagerAdapter;
    WelcomeSlide1 welcomeSlide1;
    WelcomeSlide2 welcomeSlide2;
    WelcomeSlide3 welcomeSlide3;
    public int currentSelectedPage = 0;
    // Global
    private GestureDetectorCompat detector;
    private static final int SWIPE_MIN_DISTANCE = 75;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    String fromWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tour);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btn_next);
        btclosetour = findViewById(R.id.closetour);
        fromWhere = getIntent().getStringExtra("fromWhere");
        detector = new GestureDetectorCompat(TourActivity.this, (android.view.GestureDetector.OnGestureListener) onSwipeListener);
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4
        };
        // adding bottom dots
        addBottomDots(0);
        welcomeSlide1 = WelcomeSlide1.newInstance();
        welcomeSlide2 = WelcomeSlide2.newInstance();
        welcomeSlide3 = WelcomeSlide3.newInstance();
        fragmentList.add(welcomeSlide1);
        fragmentList.add(welcomeSlide2);
        fragmentList.add(welcomeSlide3);
        welcomePagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(welcomePagerAdapter);


        //springDotsIndicator.
        // registerWithFireBaseToken(); // call after new app setup for api
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOnTouchListener(viewPagerTouchListener); //set touch listener.
        if (!fromWhere.equalsIgnoreCase("AfterLogin")) {
            redirectToHomeIfLoggedIn();
        }
        if (fromWhere.equals("AfterLogin")) {
            btclosetour.setVisibility(View.VISIBLE);
        }

        btclosetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToHomeIfLoggedIn();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  int current = getItem(+1);
                if (currentSelectedPage >= 0 && currentSelectedPage < 2) {
                    // move to next screen
                    currentSelectedPage = currentSelectedPage + 1;
                    //viewPager.setCurrentItem(currentSelectedPage);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //  setPageAnimation(currentSelectedPage);
                        }
                    }, 500);
                }
                if (position1 >= 0 && position1 <= 2) {
                    if (position1 == 2) {
                        if (fromWhere.equalsIgnoreCase("AfterLogin")) {
                            redirectToHomeIfLoggedIn();
                        } else {
                            TourActivity.this.finish();
                        }
                    } else {
                        int positionlocal = position1;
                        viewPager.setCurrentItem(positionlocal + 1);
                        addBottomDots(positionlocal + 1);
                    }
                }
            }
        });
    }

    ViewPager.OnTouchListener viewPagerTouchListener = new ViewPager.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return detector.onTouchEvent(motionEvent);
        }

    };
    android.view.GestureDetector.OnGestureListener onSwipeListener = new android.view.GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            Log.d(TAG, "onDown: ");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
            Log.d(TAG, "onShowPress: ");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            Log.d(TAG, "onSingleTapUp: ");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            Log.d(TAG, "onLongPress: ");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (position1 == 2) {
                        Log.d(TAG_FLY, "onFling: swipe Left: in else current selectedPage" + currentSelectedPage);
                        if (fromWhere.equalsIgnoreCase("AfterLogin")) {
                            redirectToHomeIfLoggedIn();
                        } else {
                            TourActivity.this.finish();
                        }
                    }

                    //   }
                }

            } catch (Exception e) {

            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onScroll: ");
            return false;
        }

    };


    public void redirectToHomeIfLoggedIn() {
        if (!TradWyseSession.getSharedInstance(TourActivity.this).getLoginAccessToken().isEmpty()) {
            Intent i = new Intent(TourActivity.this, DashBoardActivity.class);
            startActivity(i);
            TourActivity.this.finish();
        }

    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length - 1];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setGravity(Gravity.CENTER_VERTICAL);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    int position1 = 0;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            position1 = position;
            Log.d(TAG, "onPageSelected:,  " + position);
            //setFragmentAnimation(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            addBottomDots(position);

            switch (position1) {
                case 0:
                    welcomeSlide1.setLottieAnimation();
                    break;
                case 1:
                    welcomeSlide2.setLottieAnimation();
                    break;
                case 2:
                    welcomeSlide3.setLottieAnimation();
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.d(TAG, "page scrolled::: arg0,arg1,arg2:\n" + arg0 + ",  " + arg1 + ",  " + arg2);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    };
}
