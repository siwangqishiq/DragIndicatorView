package com.xinlan.dragindicatorview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DragIndicatorView mIndiactorView;
    private View mDismssBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIndiactorView = (DragIndicatorView) findViewById(R.id.indiactor_view);
        mDismssBtn = findViewById(R.id.close_btn);

        mDismssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndiactorView.dismissView();
            }
        });
    }

}//end class
