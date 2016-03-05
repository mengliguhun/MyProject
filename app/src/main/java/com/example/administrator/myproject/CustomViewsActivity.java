package com.example.administrator.myproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.myproject.utils.SortUtil;

public class CustomViewsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_views);
        int[] a={49,38,65,97,76,13,27,49,78,34,12,64,1,8};
//        SortUtil.insertSort(a);
        getMiddle(a,0,a.length-1);
    }
    private static int getMiddle(int[] a, int low, int high) {
        int temp = a[low];//基准元素
        while(low<high){
            //找到比基准元素小的元素位置
            while(low<high && a[high]>=temp){
                high--;
            }
            a[low] = a[high];
            while(low<high && a[low]<=temp){
                low++;
            }
            a[high] = a[low];
        }
        a[low] = temp;
        return low;
    }
}