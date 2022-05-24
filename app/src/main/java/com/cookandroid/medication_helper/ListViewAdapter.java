package com.cookandroid.medication_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {

    ArrayList<listViewAdapterData> list = new ArrayList<listViewAdapterData>();

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int i){
        return list.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){

        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_listview,viewGroup,false);
        }

        TextView tvName = (TextView) view.findViewById(R.id.item_name);
        TextView tvBirth = (TextView) view.findViewById(R.id.item_birth);
        TextView tvGender = (TextView) view.findViewById(R.id.item_gender);

        listViewAdapterData listdata = list.get(i);

        tvName.setText(listdata.getuName());
        tvName.setText(listdata.getuBirth());
        tvName.setText(listdata.getuGender());

        return view;
    }

    public void addItemToList(String uName, String uBirth, String uGender){
        listViewAdapterData listdata = new listViewAdapterData();

        listdata.setuName(uName);
        listdata.setuBirth(uBirth);
        listdata.setuGender(uGender);

        list.add(listdata);
    }

}
