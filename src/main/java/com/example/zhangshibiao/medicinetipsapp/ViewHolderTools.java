package com.example.zhangshibiao.medicinetipsapp;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by zhangshibiao on 15/12/8.
 */
public class ViewHolderTools {
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view,int id) {
        SparseArray<View> viewHolder = (SparseArray<View>)view.getTag();
        if(viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if(childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T)childView;
    }
}
