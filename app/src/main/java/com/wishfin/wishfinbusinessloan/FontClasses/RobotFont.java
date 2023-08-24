package com.wishfin.wishfinbusinessloan.FontClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RobotFont extends androidx.appcompat.widget.AppCompatTextView {

    public RobotFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RobotFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotFont(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "Poppins-SemiBold.ttf");
        setTypeface(typeface, Typeface.BOLD);

    }
}
