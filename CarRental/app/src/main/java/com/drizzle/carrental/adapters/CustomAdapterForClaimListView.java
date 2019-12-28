package com.drizzle.carrental.adapters;

import android.content.Context;
import android.content.res.Resources;
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
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.models.Claim;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapterForClaimListView extends ArrayAdapter<Claim> implements View.OnClickListener {

    private ArrayList<Claim> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {

        TextView textViewClaimDate;
        TextView textViewClaimLocation;
        TextView textViewClaimStatus;
        ImageButton imageButtonRemoveClaim;
        ImageView imageViewClaimStatus;

    }

    public CustomAdapterForClaimListView(ArrayList<Claim> data, Context context) {
        super(context, R.layout.claim_row_list_item, data);

        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);

        Claim claim = (Claim) object;

        switch (v.getId()) {
            case R.id.imageButton:

                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Claim claim = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.claim_row_list_item, parent, false);

            viewHolder.textViewClaimDate = convertView.findViewById(R.id.textview_claim_date);
            viewHolder.textViewClaimLocation = convertView.findViewById(R.id.textview_claim_location);
            viewHolder.textViewClaimStatus = convertView.findViewById(R.id.textview_claim_description);
            viewHolder.imageButtonRemoveClaim = convertView.findViewById(R.id.imagebutton_remove_claim);
            viewHolder.imageViewClaimStatus = convertView.findViewById(R.id.imageview_claim_state);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;

        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.textViewClaimDate.setText(claim.getDateString());
        viewHolder.textViewClaimLocation.setText(claim.getAddressHappened());

        switch (claim.getClaimState()) {
            case APPROVED:
                viewHolder.imageViewClaimStatus.setImageResource(R.drawable.claim_state_approved);
                viewHolder.textViewClaimStatus.setText(R.string.text_claim_state_approved);
                viewHolder.textViewClaimStatus.setTextColor(getContext().getResources().getColor(R.color.colorClaimListClaimStateApproved, null));
                break;
            case NOT_APPROVED:
                viewHolder.imageViewClaimStatus.setImageResource(R.drawable.claim_state_not_approved);
                viewHolder.textViewClaimStatus.setText(R.string.text_claim_state_not_approved);
                viewHolder.textViewClaimStatus.setTextColor(getContext().getResources().getColor(R.color.colorClaimListClaimStateNotApproved, null));
                break;
            case INCOMPLETE:
                viewHolder.imageViewClaimStatus.setImageResource(R.drawable.claim_state_incomplete);
                viewHolder.textViewClaimStatus.setText(R.string.text_claim_state_incomplete);
                viewHolder.textViewClaimStatus.setTextColor(getContext().getResources().getColor(R.color.colorClaimListClaimStateIncomplete, null));
                break;
            case EXPERT_UNDERGOING:
                viewHolder.imageViewClaimStatus.setImageResource(R.drawable.claim_state_expert_undergoing);
                viewHolder.textViewClaimStatus.setText(R.string.text_claim_state_expert_undergoing);
                viewHolder.textViewClaimStatus.setTextColor(getContext().getResources().getColor(R.color.colorClaimListClaimStateExpertUndergoing, null));
                break;
            default:
                break;
        }


        // Return the completed view to render on screen
        return convertView;

    }

}
