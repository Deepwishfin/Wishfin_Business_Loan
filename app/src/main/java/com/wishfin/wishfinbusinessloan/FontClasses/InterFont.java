package com.wishfin.wishfinbusinessloan.FontClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class InterFont extends androidx.appcompat.widget.AppCompatTextView {

    public InterFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public InterFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InterFont(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "Poppins-Regular.ttf");
        setTypeface(typeface, Typeface.NORMAL);

    }
}