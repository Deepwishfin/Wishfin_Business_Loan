package com.wishfin.wishfinbusinessloan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Collections;
import java.util.List;

public class DotIndicatorPagerAdapter extends PagerAdapter {
    private static final List<Item> items = Collections.singletonList(new Item(R.color.md_indigo_500));

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View item = LayoutInflater.from(container.getContext()).inflate(R.layout.material_page, container, false);
        ImageView img = item.findViewById(R.id.item_image);

        if (position == 0) {
            img.setBackgroundResource(R.drawable.onboarding);
        }
        container.addView(item);
        return item;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private static class Item {
        private final int color;

        private Item(int color) {
            this.color = color;
        }
    }
}
