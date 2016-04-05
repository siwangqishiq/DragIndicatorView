package com.xinlan.dragindicatorview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DragIndicatorView mIndiactorView;
    private View mDismssBtn;

    private View mChangeBtn;
    private DragIndicatorView mSecondIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIndiactorView = (DragIndicatorView) findViewById(R.id.indiactor_view);
        mSecondIndicatorView = (DragIndicatorView) findViewById(R.id.indiactor_view2);
        mDismssBtn = findViewById(R.id.close_btn);

        mDismssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndiactorView.dismissView();
            }
        });

        mChangeBtn = findViewById(R.id.change_btn);
        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSecondIndicatorView.getVisibility() == View.VISIBLE) {
                    mSecondIndicatorView.setVisibility(View.GONE);
                } else {
                    mSecondIndicatorView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}//end class
