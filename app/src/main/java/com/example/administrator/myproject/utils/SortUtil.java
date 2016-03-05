package com.example.administrator.myproject.utils;

/**
 * Created by Administrator on 2016/2/29.
 */
public class SortUtil {

    public static int[] selectSort(int[] array){
        for (int i = 0; i <array.length ; i++) {
            int min = i;
            for (int j = i+1; j < array.length; j++) {
                if (array[min] >array[j]){
                    min = j;
                }
            }
            int temp = array[i];
            array[i] = array[min];
            array[min] = temp;
        }
        return array;
    }
    public static int[] BubbleSort(int[] array){
        for (int i = 0; i < array.length -1; i++) {
            for (int j = 0; j < array.length-i-1; j++) {
                int temp = array[j];
                if (array[j]>array[j+1]){
                    array[j] = array[j+1];
                    array[j+1] = temp;
                }
            }
        }
        return array;
    }
    public static int[] insertSort(int[] array){
        for (int i = 1; i < array.length; i++) {
            int temp = array[i];
            int j;
            for (j = i-1; j >=0; j--) {
                if (temp < array[j]){
                    array[j+1] = array[j];
                }
                else {
                    break;
                }
            }
            array[j+1] = temp;
        }
        return array;
    }

}
