package com.example.zhangshibiao.medicinetipsapp;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangshibiao on 15/12/13.
 */
public class RecordAdapter extends BaseAdapter {
    public Context context;
    public List<Map> data;
    ViewHolder vh = new ViewHolder();
    public RecordAdapter(Context c, List<Map> tipsList) {
        context = c;
        data = tipsList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tips, null);
            TextView tipTv = vh.get(convertView, R.id.item_tips);
            String text = formatTimeStr(data.get(position).get("timeStr").toString());
            text = text!="" ? text : data.get(position).get("timeStr").toString();
            Log.i("get_veiw_isEat",data.get(position).get("isEat").toString()+"--" + text);
            String isEatDesp = ("yes".equals(data.get(position).get("isEat").toString())) ? "已服药" : "未服药";
            text = isEatDesp + "   " + text;

            tipTv.setText(text);
        }
        return convertView;
    }

    private String formatTimeStr (String timeStr) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String newDateString = "";
        long timeStamp = Long.parseLong(timeStr);
        newDateString = df.format(new Date(timeStamp));
        return newDateString;

    }
    /**
     * 写Apdater的时候用这个可以减少很多代码量
     * @author wuruize
     */
    class ViewHolder {

        @SuppressWarnings("unchecked")
        public  <T extends View> T get(View view,int id) {
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
}
