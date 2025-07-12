package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.fu.thinh_nguyen.qrfoodorder.R;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        setupIndicators(3); // số trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });


    }

    private void setupIndicators(int count) {
        LinearLayout indicatorsLayout = findViewById(R.id.layoutIndicator);
        ImageView[] indicators = new ImageView[count];

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.onboarding_indicator));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            indicatorsLayout.addView(indicators[i], params);
        }
    }

    private void setCurrentIndicator(int index) {
        LinearLayout indicatorsLayout = findViewById(R.id.layoutIndicator);
        int count = indicatorsLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView imageView = (ImageView) indicatorsLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.onboarding_indicator));
            }
        }
    }


}

