package com.agelogeo.givealike2;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuidelineFragment extends Fragment {
    private ViewPager mSlideViewPager;
    private ConstraintLayout mDotLayout;
    private SliderAdapter sliderAdapter;

    public GuidelineFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guideline, container, false);
        mSlideViewPager = v.findViewById(R.id.slideViewPager);
        mDotLayout = v.findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(getContext());

        mSlideViewPager.setAdapter(sliderAdapter);

        return v;
    }

}
