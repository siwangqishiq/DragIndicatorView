package com.xinlan.dragindicatorview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xinlan.dragindicator.DragIndicatorView;

public class MainActivity extends AppCompatActivity {
    private DragIndicatorView mIndiactorView;
    private DragIndicatorView mOtherView;
    private View mDismssBtn;
    private View mSecondBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIndiactorView = (DragIndicatorView) findViewById(R.id.indiactor_view);
        mOtherView = (DragIndicatorView) findViewById(R.id.indicator);

        mDismssBtn = findViewById(R.id.close_btn);
        mDismssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndiactorView.dismissView();
                mOtherView.dismissView();
            }
        });

        mSecondBtn = findViewById(R.id.second_btn);
        mSecondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to list activity
                SecondActivity.start(MainActivity.this);
            }
        });
    }

}//end class
