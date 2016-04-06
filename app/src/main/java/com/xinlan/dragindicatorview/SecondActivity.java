package com.xinlan.dragindicatorview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * List Activity
 */
public class SecondActivity extends AppCompatActivity {
    private List<Bean> mList = new ArrayList<Bean>();

    private RecyclerView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initData();
        findViews();
        initListView();
    }

    private void findViews() {
        mListView = (RecyclerView) findViewById(R.id.list);
        mListView.setHasFixedSize(true);
    }

    private void initListView() {
        mListView.setAdapter(new ListAdapter(this, mList));
    }

    public static void start(Activity context) {
        context.startActivity(new Intent(context, SecondActivity.class));
    }

    private void initData() {
        mList.add(new Bean("海边的卡夫卡", "http://img10.360buyimg.com/n0/g14/M06/06/00/rBEhV1Hog7kIAAAAAAJBoy0J-0YAABLLAO-AUoAAkG7180.jpg", 1));
        mList.add(new Bean("挪威的森林", "http://img22.mtime.cn/up/2011/11/13/212010.80062662_500.jpg", 2));
        mList.add(new Bean("没有女人的男人们", "http://img1.cache.netease.com/catchpic/B/B2/B221247352A242589DB2251244D9EE58.jpg", 3));
        mList.add(new Bean("且听风吟", "http://images.bookuu.com/book_t/11/49/27/1149277.jpg", 3));
        mList.add(new Bean("神的孩子全跳舞", "http://images.zxhsd.com/photo/book_b/C/00843/97875327484951591635-fm-b.jpg", 3));
        mList.add(new Bean("海边的卡夫卡", "http://img10.360buyimg.com/n0/g14/M06/06/00/rBEhV1Hog7kIAAAAAAJBoy0J-0YAABLLAO-AUoAAkG7180.jpg", 1));
        mList.add(new Bean("挪威的森林", "http://img22.mtime.cn/up/2011/11/13/212010.80062662_500.jpg", 2));
        mList.add(new Bean("没有女人的男人们", "http://img1.cache.netease.com/catchpic/B/B2/B221247352A242589DB2251244D9EE58.jpg", 3));
        mList.add(new Bean("且听风吟", "http://images.bookuu.com/book_t/11/49/27/1149277.jpg", 3));
        mList.add(new Bean("神的孩子全跳舞", "http://images.zxhsd.com/photo/book_b/C/00843/97875327484951591635-fm-b.jpg", 3));
        mList.add(new Bean("海边的卡夫卡", "http://img10.360buyimg.com/n0/g14/M06/06/00/rBEhV1Hog7kIAAAAAAJBoy0J-0YAABLLAO-AUoAAkG7180.jpg", 1));
        mList.add(new Bean("挪威的森林", "http://img22.mtime.cn/up/2011/11/13/212010.80062662_500.jpg", 2));
        mList.add(new Bean("没有女人的男人们", "http://img1.cache.netease.com/catchpic/B/B2/B221247352A242589DB2251244D9EE58.jpg", 3));
        mList.add(new Bean("且听风吟", "http://images.bookuu.com/book_t/11/49/27/1149277.jpg", 3));
        mList.add(new Bean("神的孩子全跳舞", "http://images.zxhsd.com/photo/book_b/C/00843/97875327484951591635-fm-b.jpg", 3));
    }
}//end class
