package com.drizzle.carrental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.models.VehicleType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapterSubscriptionCarTypeSelect extends BaseAdapter {

    Context context;
    ArrayList<VehicleType> vehicleTypes;
    LayoutInflater inflter;

    public CustomAdapterSubscriptionCarTypeSelect(Context applicationContext, ArrayList<VehicleType> vehicleTypes) {

        this.context = applicationContext;
        this.vehicleTypes = vehicleTypes;
        inflter = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return vehicleTypes.size();
    }

    @Override
    public Object getItem(int i) {

        return vehicleTypes.get(i);

    }

    @Override
    public long getItemId(int i) {

        return vehicleTypes.get(i).getId();

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.cartype_select_spinner_item, null);

        try {
            ImageView icon = (ImageView) view.findViewById(R.id.imageView);
            TextView names = (TextView) view.findViewById(R.id.textView);

            Picasso.get().load(vehicleTypes.get(i).getIconURL()).placeholder(R.drawable.icon_company).into(icon);
            names.setText(vehicleTypes.get(i).getName());
        }
        catch (Exception e) {

        }
        return view;
    }
}