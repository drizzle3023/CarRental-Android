package com.drizzle.carrental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.models.Company;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapterCompanySelect extends BaseAdapter {

    Context context;
    ArrayList<Company> companies;
    LayoutInflater inflter;

    public CustomAdapterCompanySelect(Context applicationContext, ArrayList<Company> companies) {

        this.context = applicationContext;
        this.companies = companies;
        inflter = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return companies.size();
    }

    @Override
    public Object getItem(int i) {

        return companies.get(i);

    }

    @Override
    public long getItemId(int i) {

        return companies.get(i).getId();

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.company_select_spinner_item, null);

        TextView textViewCompanyName = view.findViewById(R.id.textView);
        textViewCompanyName.setText(companies.get(i).getName());
        return view;
    }
}