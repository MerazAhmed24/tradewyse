package com.info.adapter;

import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.commons.Common;
import com.info.tradewyse.R;

/**
 * Created by ankur on 5/5/2019.
 */

public class TourAdapter  extends PagerAdapter{

    private Context context;
    private int[] layouts;

    public TourAdapter(Context context,int[] layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        LayoutInflater layoutInflater  = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layouts[position], container, false);
        View view_transparent=view.findViewById(R.id.view_transparent);
        TextView txtTitle = view.findViewById(R.id.titleTour);
        TextView txtDescripion = view.findViewById(R.id.descriptionTour);
        RelativeLayout rlRoot = view.findViewById(R.id.rootLayout);
       // txtTitle.setTypeface(Common.getTypeface(context,1));
       // txtDescripion.setTypeface(Common.getTypeface(context,1));


        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
        txtTitle.setAnimation(fadeOut);

        Animation fadeBackground = new AlphaAnimation(0, 1);
        fadeBackground.setInterpolator(new AccelerateInterpolator()); //and this
        fadeBackground.setStartOffset(2000);
        fadeBackground.setDuration(3000);
        view_transparent.setAnimation(fadeBackground);

/*        Animation animation1 = new AlphaAnimation(1f, 0.5f);
        animation1.setInterpolator(new AccelerateInterpolator()); //and this
        animation1.setDuration(1000);
        animation1.setStartOffset(4000);
        animation1.setFillAfter(true);
        rlRoot.setAnimation(animation1);*/

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;

    }


}
