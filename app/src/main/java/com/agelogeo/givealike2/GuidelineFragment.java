package com.agelogeo.givealike2;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuidelineFragment extends Fragment {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout ;
    private SliderAdapter sliderAdapter;

    private TextView[] mDots;
    private Button mBackBtn , mNextBtn;
    private int mCurrentPage = 0 ;

    public GuidelineFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guideline, container, false);
        mSlideViewPager = v.findViewById(R.id.slideViewPager);
        mDotLayout = v.findViewById(R.id.dotsLayout);
        mBackBtn = v.findViewById(R.id.prevBtn);
        mNextBtn = v.findViewById(R.id.nextBtn);

        sliderAdapter = new SliderAdapter(getContext());
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPage == mDots.length-1){
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.mainConstraint, new MainFragment());
                    fragmentTransaction.commit();
                }else{
                    mSlideViewPager.setCurrentItem(mCurrentPage+1);
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
            }
        });

        return v;
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[4];
        mDotLayout.removeAllViews();
        for(int i=0 ; i < mDots.length ; i++){
            mDots[i] = new TextView(getContext());
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.light_gray));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;

            if(i == 0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("");
            }else if(i == mDots.length - 1){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Finish");
                mBackBtn.setText("Back");
            }else{
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

}
