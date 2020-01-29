package com.drizzle.carrental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.History;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapterForHistoryListView extends ArrayAdapter<History> implements View.OnClickListener {

    private ArrayList<History> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView textViewActiveState;
        TextView textViewTitle;
        TextView textViewState;
        TextView textViewPeriod;
        TextView textViewLocation;
        ImageButton imageButton;
        ImageView mapMarker;

    }

    public CustomAdapterForHistoryListView(ArrayList<History> data, Context context) {
        super(context, R.layout.history_row_list_item, data);

        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);

        History historyModel = (History) object;

        switch (v.getId()) {
            case R.id.imageButton:

                break;
        }
    }

    private int lastPosition = -1;



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        History historyModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.history_row_list_item, parent, false);

            viewHolder.textViewActiveState = (TextView) convertView.findViewById(R.id.textview_active_state);
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.textview_title);
            viewHolder.textViewState = (TextView) convertView.findViewById(R.id.textview_state);
            viewHolder.textViewPeriod = (TextView) convertView.findViewById(R.id.textview_period);
            viewHolder.textViewLocation = (TextView) convertView.findViewById(R.id.textview_location);
            viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.imagebutton);
            viewHolder.mapMarker = (ImageView) convertView.findViewById(R.id.imageview_mapmarker);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;

        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        if (historyModel.isPaymentOrCoverage()) {
            viewHolder.textViewActiveState.setVisibility(View.GONE);
            viewHolder.textViewTitle.setText(historyModel.getPayment().getTitle());
            viewHolder.textViewState.setText(historyModel.getPayment().getState().toString());
            viewHolder.textViewPeriod.setText(historyModel.getPayment().getInformation());
            viewHolder.mapMarker.setVisibility(View.GONE);
            viewHolder.textViewLocation.setText(historyModel.getPayment().getPaymentDateAsString());
            viewHolder.imageButton.setImageResource(R.drawable.payment_success);

        } else {

            if (historyModel.getCoverage().getState() == CoverageState.COVERED) {
                int claimCount = 0;
                String claimDescription = "";
                try {
                    claimCount = historyModel.getCoverage().getClaimCount();
                } catch (Exception e) {

                }

                claimDescription = String.format("%d claims", claimCount);

                viewHolder.textViewActiveState.setText(claimDescription);
                viewHolder.textViewActiveState.setTextAppearance(R.style.AppTheme_HistoryRowItemEnabled);
            } else {
                //viewHolder.textViewActiveState.setText(R.string.one_claim_resolved);
                viewHolder.textViewActiveState.setVisibility(View.GONE);
                viewHolder.textViewActiveState.setTextAppearance(R.style.AppTheme_HistoryRowItemEnabled);
            }
            viewHolder.textViewTitle.setText(historyModel.getCoverage().getTitle());
            viewHolder.textViewState.setText(historyModel.getCoverage().getState().toString());
            viewHolder.textViewPeriod.setText(historyModel.getCoverage().getPeriod());

            Picasso picasso = Picasso.get();
            picasso.invalidate(historyModel.getCoverage().getUrlImageVehicle());
            picasso.load(historyModel.getCoverage().getUrlImageVehicle()).placeholder(R.drawable.video_vehicle).into(viewHolder.imageButton);
            //Picasso.get().load(image_url).placeholder(R.drawable.ic_icon_add_coverage).into(viewHolder.imageButton);
            viewHolder.imageButton.setImageResource(R.drawable.video_vehicle);


            viewHolder.textViewLocation.setText(historyModel.getCoverage().getLocationAddress());

        }

        // Return the completed view to render on screen
        return convertView;

    }

}
