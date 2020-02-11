package com.drizzle.carrental.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.ClaimsActivity;
import com.drizzle.carrental.customcomponents.AppCompatImageView_Round_10;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.History;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapterForHistoryListView extends ArrayAdapter<History> {

    private ArrayList<History> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView textViewActiveState;
        TextView textViewTitle;
        TextView textViewState;
        TextView textViewPeriod;
        TextView textViewLocation;
        AppCompatImageView_Round_10 imageButton;
        ImageView mapMarker;

    }

    public CustomAdapterForHistoryListView(ArrayList<History> data, Context context) {
        super(context, R.layout.history_row_list_item, data);

        this.dataSet = data;
        this.mContext = context;

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

            viewHolder.textViewActiveState = convertView.findViewById(R.id.textview_active_state);

            viewHolder.textViewTitle = convertView.findViewById(R.id.textview_title);
            viewHolder.textViewState = convertView.findViewById(R.id.textview_state);
            viewHolder.textViewPeriod = convertView.findViewById(R.id.textview_period);
            viewHolder.textViewLocation = convertView.findViewById(R.id.textview_location);
            viewHolder.imageButton = convertView.findViewById(R.id.imagebutton);
            viewHolder.mapMarker = convertView.findViewById(R.id.imageview_mapmarker);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;

        }

        viewHolder.textViewActiveState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ClaimsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.INTENT_DATA_COVERAGE_ID, historyModel.getCoverage().getId());
                getContext().startActivity(intent);
            }
        });

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

                if (claimCount == 0) {
                    claimDescription = "No claims";
                } else {
                    claimDescription = String.format("%d claims", claimCount);
                }

                viewHolder.textViewActiveState.setText(claimDescription);
                viewHolder.textViewActiveState.setTextAppearance(R.style.AppTheme_HistoryRowItemEnabled);
                viewHolder.textViewActiveState.setVisibility(View.VISIBLE);
            } else {
                //viewHolder.textViewActiveState.setText(R.string.one_claim_resolved);
                viewHolder.textViewActiveState.setVisibility(View.GONE);
                viewHolder.textViewActiveState.setTextAppearance(R.style.AppTheme_HistoryRowItemEnabled);
            }
            viewHolder.textViewTitle.setText(historyModel.getCoverage().getTitle());
            viewHolder.textViewState.setText(historyModel.getCoverage().getState().toString());
            if (historyModel.getCoverage().getState() == CoverageState.CANCELLED) {
                viewHolder.textViewPeriod.setText(historyModel.getCoverage().getDateOperationAsString());
            }
            else {
                viewHolder.textViewPeriod.setText(historyModel.getCoverage().getPeriod());
            }


            Picasso picasso = Picasso.get();
            picasso.invalidate(historyModel.getCoverage().getUrlImageVehicle());
            picasso.load(historyModel.getCoverage().getUrlImageVehicle()).placeholder(R.drawable.history_row_item_image_corner_radius).into(viewHolder.imageButton);
            //Picasso.get().load(image_url).placeholder(R.drawable.ic_icon_add_coverage).into(viewHolder.imageButton);
            //viewHolder.imageButton.setImageResource(R.drawable.video_vehicle);


            viewHolder.textViewLocation.setText(historyModel.getCoverage().getLocationAddress());

        }

        // Return the completed view to render on screen
        return convertView;

    }

}
