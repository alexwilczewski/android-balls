package com.example.AnimationTutorial;

import android.app.Activity;
import android.os.Bundle;

public class AnimationExampleActivity extends Activity {
    CustomDrawableView mCustomDrawableView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomDrawableView = new CustomDrawableView(this);

        setContentView(mCustomDrawableView);
    }
}
