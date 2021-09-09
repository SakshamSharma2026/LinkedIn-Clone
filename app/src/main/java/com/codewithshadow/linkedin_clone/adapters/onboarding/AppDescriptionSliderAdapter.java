package com.codewithshadow.linkedin_clone.adapters.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.codewithshadow.linkedin_clone.R;

public class AppDescriptionSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public AppDescriptionSliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
            R.drawable.pic_1,
            R.drawable.pic_2,
            R.drawable.pic_3
    };

    int headings[] = {
            R.string.search_heading,
            R.string.build_heading,
            R.string.stay_heading
    };


    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout, container, false);

        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView heading = view.findViewById(R.id.slider_heading);

        imageView.setImageResource(images[position]);
        heading.setText(headings[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
