package com.drizzle.carrental.adapters;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.AddClaimActivity;
import com.drizzle.carrental.activities.ClaimsActivity;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.Claim;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomAdapterForClaimListView extends ArrayAdapter<Claim> implements Callback<ResponseBody> {

    private ArrayList<Claim> dataSet;
    ClaimsActivity mActivity;
    private int lastPosition = -1;
    ProgressDialog progressDialog;

    private long idToBeRemoved = -1;

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


        hideWaitingScreen();

        String responseString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                responseString = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(getContext(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        if (object == null) {

            Toast.makeText(getContext(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();

                mActivity.removeClaimFromModelList(idToBeRemoved);
                mActivity.updateView();
                idToBeRemoved = -1;


            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getContext(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(getContext(), R.string.message_no_response, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        idToBeRemoved = -1;
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        idToBeRemoved = -1;
        hideWaitingScreen();
        Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    // View lookup cache
    private static class ViewHolder {

        TextView textViewClaimDate;
        TextView textViewClaimLocation;
        TextView textViewClaimStatus;
        ImageButton imageButtonRemoveClaim;
        ImageView imageViewClaimStatus;

    }

    public CustomAdapterForClaimListView(ArrayList<Claim> data, ClaimsActivity activity) {
        super(activity, R.layout.claim_row_list_item, data);

        this.dataSet = data;
        this.mActivity = activity;

        progressDialog = new ProgressDialog(getContext());

    }

    private void removeClaimFromServer(long claimId) {

        if (claimId == -1) {
            return;
        }
        JSONObject paramObject = new JSONObject();

        try {

            paramObject.put("access_token", SharedHelper.getKey(getContext(), "access_token"));
            paramObject.put("claim_id", claimId);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //display waiting dialog
        showWaitingScreen();
        //send request

        apiInterface.removeClaim(gSonObject).enqueue(this);
    }

    private void showWaitingScreen() {


        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideWaitingScreen() {

        progressDialog.dismiss();
    }


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

            viewHolder.imageButtonRemoveClaim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("Remove Claim")
                            .setMessage("Are you sure you want to remove this claim?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    idToBeRemoved = getItem(position).getId();
                                    removeClaimFromServer(idToBeRemoved);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Globals.selectedClaim = claim;
                    Intent intent = new Intent(mActivity, AddClaimActivity.class);
                    mActivity.startActivityForResult(intent, ClaimsActivity.CLAIM_ADD_REQUEST);
                }
            });
            result = convertView;
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;

        }

        Animation animation = AnimationUtils.loadAnimation(mActivity, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.textViewClaimDate.setText(claim.getDateString());
        viewHolder.textViewClaimLocation.setText(claim.getName());

        viewHolder.imageButtonRemoveClaim.setVisibility(View.GONE);

        switch (claim.getClaimState()) {
            case PENDING_REVIEW:
                viewHolder.imageViewClaimStatus.setImageResource(R.drawable.claim_state_expert_undergoing);
                viewHolder.textViewClaimStatus.setText(R.string.text_claim_state_pending);
                viewHolder.textViewClaimStatus.setTextColor(getContext().getResources().getColor(R.color.colorClaimListClaimStateExpertUndergoing, null));
                break;
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
                viewHolder.imageButtonRemoveClaim.setVisibility(View.VISIBLE);
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
